package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.Loader;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.ruin.Building;
import dev.vanilladev.wasteland.ruin.RuinLightposts;
import dev.vanilladev.wasteland.utils.Vector;
import dev.vanilladev.wasteland.world.gen.WorldGenRandomRubble;

public class RuinedCity 
{
	private static final int BLOCK_ORIGIN = 9; // block origin offset from center; block spans [-9,8]
	private static final int STREET_W = 6;   // street band width
	private static final int BLOCK_W = 18;   // city block side length
	private static final int CYCLE = 24;     // block + street period
	private static final int SUB_W = 9;      // block sub-cell side (BLOCK_W / 2)
	// HBM CE linkage: NTM civilian houses/labs/offices (9-15 wide), generated
	// reflectively when the HBM mod is loaded; at most 3 of each per city
	private static final int HBM_HOUSE = 1000;    // CivilianFeatures$NTMHouse1 (9x4)
	private static final int HBM_LAB1 = 1001;     // CivilianFeatures$NTMLab1 (9x4)
	private static final int HBM_OFFICE = 1002;   // OfficeFeatures$LargeOffice (14x5)
	private static final int HBM_OFFICE_C = 1003; // OfficeFeatures$LargeOfficeCorner (11x15)
	private static final int HBM_HOUSE2 = 1004;   // CivilianFeatures$NTMHouse2 (15x5)
	private static final int HBM_LAB2 = 1005;     // CivilianFeatures$NTMLab2 (12x11)
	private static final int HBM_RURAL = 1006;    // CivilianFeatures$RuralHouse1 (14x8)
	private static final int HBM_RUIN1 = 1007;    // dungeon.Ruin001 (12x14, WorldGenerator)
	private static final boolean HBM = Loader.isModLoaded("hbm");

	private final Vector center;
	private final List<Vector> chunks;
	private final RuinLightposts lamp = new RuinLightposts(null);
	private final WorldGenRandomRubble rubble = new WorldGenRandomRubble();
	// footprints taken by buildings (inflated by 1 block for spacing)
	private final java.util.HashSet<Long> occupied = new java.util.HashSet<Long>();
	// HBM per-type counter so no linked building repeats more than 3 times
	private final int[] hbmCount = new int[8];
	// temp diagnostics (removed for release)
	private int blocksSeen, biomeFail, groundFail, createFail, placedBuildings, hbmBuildings;

	public RuinedCity(World world, Vector center, List<Vector> chunks, Random random) 
	{
		this.center = center;
		this.chunks = chunks;
	}

	public void generate(World world, Random random) 
	{
		// city is built around the center only; a bounded core keeps worldgen
		// cascading loads finite instead of storming over the whole biome patch
		int core = 3 * CYCLE; // 6x6 city blocks
		int extent = core + STREET_W;
		System.out.println("City gen: center biome " + world.getBiome(new BlockPos(center.X, 0, center.Z)).getRegistryName() + " core " + core);
		// the whole city footprint becomes one big plain first, so streets and
		// buildings always sit on the same plane (no riverbed dips, no floats)
		flattenCity(world, center.X - extent, center.X + extent, center.Z - extent, center.Z + extent);
		fillStreets(world, random, center.X - extent, center.X + extent, center.Z - extent, center.Z + extent);
		placeBlocks(world, random, center.X - core - STREET_W, center.X + core, center.Z - core - STREET_W, center.Z + core);
		System.out.println("City gen done: blocks=" + this.blocksSeen + " biomeFail=" + this.biomeFail + " groundFail=" + this.groundFail + " createFail=" + this.createFail + " buildings=" + this.placedBuildings + " hbm=" + this.hbmBuildings + " hbmLoaded=" + HBM);
	}

	private static boolean isStreet(int offset)
	{
		// streets sit at [9,14] mod 24, right after each block [-9,8]
		return Math.floorMod(offset - BLOCK_ORIGIN, CYCLE) < STREET_W;
	}

	private int groundY(World world, int x, int z)
	{
		// getHeight(int,int) returns 0 on ungenerated chunks; force-load instead,
		// otherwise streets/buildings are skipped while the core chunks are new
		int h = world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getHeightValue(x & 15, z & 15);
		return (h > 0) ? h : 0;
	}

	// flatten the whole city footprint into one big plain at the center's
	// ground level: high ground is cut, dips (dry riverbeds) are filled, and
	// the surface is re-laid so streets and buildings sit on the same plane
	private void flattenCity(World world, int minX, int maxX, int minZ, int maxZ)
	{
		int base = groundY(world, center.X, center.Z);
		if (base <= 0)
			base = 64;
		// edge transition: an 8-wide band around the plain fades back to the
		// natural terrain, so the city merges into the wastes without a cliff
		int blend = 8;
		System.out.println("City flatten: base=" + base + " blend=" + blend);
		for (int x = minX - blend; x <= maxX + blend; x++)
		{
			for (int z = minZ - blend; z <= maxZ + blend; z++)
			{
				int gy = groundY(world, x, z);
				if (gy <= 0)
					continue;
				int dx = (x < minX) ? minX - x : (x > maxX) ? x - maxX : 0;
				int dz = (z < minZ) ? minZ - z : (z > maxZ) ? z - maxZ : 0;
				int dist = (dx > dz) ? dx : dz;
				int target = base;
				if (dist > blend)
					continue; // outside the band - natural terrain untouched
				else if (dist > 0)
					target = base + (gy - base) * dist / blend; // fade back to natural
				if (target > gy)
				{
					for (int y = gy; y < target; y++)
						world.setBlockState(new BlockPos(x, y, z), Blocks.DIRT.getDefaultState());
				}
				else if (target < gy)
				{
					for (int y = target; y < gy; y++)
						world.setBlockToAir(new BlockPos(x, y, z));
				}
				world.setBlockState(new BlockPos(x, target - 1, z), ModConfig.getSurfaceBlock().getDefaultState());
			}
		}
	}

	// street surface patches + street lamps
	private void fillStreets(World world, Random random, int minX, int maxX, int minZ, int maxZ)
	{
		for (int x = minX; x <= maxX; x++)
		{
			boolean streetX = isStreet(x - center.X);
			for (int z = minZ; z <= maxZ; z++)
			{
				if (!streetX && !isStreet(z - center.Z))
					continue;
				int y = groundY(world, x, z) - 1; // surface block itself
				if (y <= 0 || world.getBlockState(new BlockPos(x, y, z)).getBlock() != ModConfig.getSurfaceBlock())
					continue;
				int r = random.nextInt(100);
				if (r < 60)
					world.setBlockState(new BlockPos(x, y, z), Blocks.GRAVEL.getDefaultState());
				else if (r < 85)
					world.setBlockState(new BlockPos(x, y, z), Blocks.COBBLESTONE.getDefaultState());
				else if (random.nextInt(24) == 0)
					lamp.placeRuin(world, random, x, y, z); // lamp on the remaining dirt patch
			}
		}
	}

	private boolean isCityBiome(World world, int x, int z)
	{
		// the city center corner often lands on the base wasteland biome at
		// biome edges; treat both as city ground so every block builds
		Biome b = world.getBiome(new BlockPos(x, 0, z));
		return b == WastelandBiomes.city || b == WastelandBiomes.apocalypse;
	}

	private void placeBlocks(World world, Random random, int minX, int maxX, int minZ, int maxZ)
	{
		int firstX = firstOrigin(minX, center.X);
		int firstZ = firstOrigin(minZ, center.Z);
		for (int bx = firstX; bx <= maxX; bx += CYCLE)
		{
			for (int bz = firstZ; bz <= maxZ; bz += CYCLE)
			{
				this.blocksSeen++;
				int cx = bx + BLOCK_W / 2;
				int cz = bz + BLOCK_W / 2;
				if (!isCityBiome(world, cx, cz))
				{
					this.biomeFail++;
					continue;
				}
				boolean central = (bx <= center.X && center.X <= bx + BLOCK_W - 1
						&& bz <= center.Z && center.Z <= bz + BLOCK_W - 1);
				if (central)
					placeCentralBlock(world, random, bx, bz, cx, cz);
				else
					placeNormalBlock(world, random, bx, bz, cx, cz);
			}
		}
	}

	// smallest block origin = center + 6 + 32k, >= min
	private int firstOrigin(int min, int coord)
	{
		// block origins sit at coord - 13 + 32k, so the center lands mid-block
		int origin = coord - BLOCK_ORIGIN;
		if (origin >= min)
		{
			int k = (origin - min) / CYCLE;
			origin -= k * CYCLE;
		}
		else
		{
			int k = (min - origin + CYCLE - 1) / CYCLE; // ceil
			origin += k * CYCLE;
		}
		return origin;
	}

	// returns true when placed. The footprint is flattened to the highest
	// ground level in its area (so buildings never float), and footprints that
	// touch an already-placed building are skipped (nothing overlaps or sticks).
	private boolean placeBuilding(World world, Random random, int type, int x, int z, boolean groundSnap)
	{
		if (type >= HBM_HOUSE && type <= HBM_RUIN1)
			return placeHbmBuilding(world, random, type, x, z, groundSnap);
		Building b = Building.create(type);
		if (b == null)
		{
			this.createFail++;
			return false;
		}
		int rot = random.nextInt(4);
		int w = ((rot & 1) == 0) ? b.width : b.length;
		int l = ((rot & 1) == 0) ? b.length : b.width;
		int x0, z0;
		if (groundSnap)
		{
			x0 = x - w / 2;
			z0 = z - l / 2;
		}
		else
		{
			x0 = x;
			z0 = z;
		}
		// overlap + spacing check (one-block margin around the footprint)
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				if (occupied.contains(key(px, pz)))
					return false;
			}
		}
		// highest ground under the footprint (with margin) - foundation level
		int maxY = 0;
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				int gy = groundY(world, px, pz);
				if (gy > maxY)
					maxY = gy;
			}
		}
		if (maxY <= 0)
		{
			this.groundFail++;
			return false;
		}
		// fill the footprint up to maxY-1 so the building sits on flat ground
		for (int px = x0; px < x0 + w; px++)
		{
			for (int pz = z0; pz < z0 + l; pz++)
			{
				int gy = groundY(world, px, pz);
				for (int yy = gy - 1; yy < maxY - 1; yy++)
					world.setBlockState(new BlockPos(px, yy, pz), Blocks.GRAVEL.getDefaultState());
			}
		}
		// mark occupied (with the same one-block margin)
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				occupied.add(key(px, pz));
			}
		}
		b.generate(world, random, new Vector(x0, maxY - 1, z0), rot);
		this.placedBuildings++;
		return true;
	}

	private static long key(int x, int z)
	{
		// city coordinates stay within +/-512 of the center
		return ((long)(x + 512) << 16) | (z + 512 & 0xFFFF);
	}

	// HBM CE linkage: generate an NTM civilian house or large office component
	// reflectively when the HBM mod is loaded; skipped on any failure
	private boolean placeHbmBuilding(World world, Random random, int type, int x, int z, boolean groundSnap)
	{
		if (!HBM)
			return false;
		int w, l;
		String cls;
		switch (type)
		{
			case HBM_HOUSE:    w = 9;  l = 4;  cls = "com.hbm.world.gen.component.CivilianFeatures$NTMHouse1"; break;
			case HBM_LAB1:     w = 9;  l = 4;  cls = "com.hbm.world.gen.component.CivilianFeatures$NTMLab1"; break;
			case HBM_OFFICE:   w = 14; l = 5;  cls = "com.hbm.world.gen.component.OfficeFeatures$LargeOffice"; break;
			case HBM_OFFICE_C: w = 11; l = 15; cls = "com.hbm.world.gen.component.OfficeFeatures$LargeOfficeCorner"; break;
			case HBM_HOUSE2:   w = 15; l = 5;  cls = "com.hbm.world.gen.component.CivilianFeatures$NTMHouse2"; break;
			case HBM_LAB2:     w = 12; l = 11; cls = "com.hbm.world.gen.component.CivilianFeatures$NTMLab2"; break;
			case HBM_RURAL:    w = 14; l = 8;  cls = "com.hbm.world.gen.component.CivilianFeatures$RuralHouse1"; break;
			case HBM_RUIN1:    w = 12; l = 14; cls = "com.hbm.world.dungeon.Ruin001"; break;
			default:
				return false;
		}
		// per-type cap: at most 3 of each linked building per city
		int idx = type - HBM_HOUSE;
		if (hbmCount[idx] >= 3)
			return false;
		int x0 = groundSnap ? x - w / 2 : x;
		if (type == HBM_RUIN1)
			x0--; // Ruin001 draws one block to the right of its anchor
		int z0 = groundSnap ? z - l / 2 : z;
		// overlap + spacing check (one-block margin, same as the ruins)
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				if (occupied.contains(key(px, pz)))
					return false;
			}
		}
		// highest ground under the footprint - foundation levels to it
		int maxY = 0;
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				int gy = groundY(world, px, pz);
				if (gy > maxY)
					maxY = gy;
			}
		}
		if (maxY <= 0)
		{
			this.groundFail++;
			return false;
		}
		for (int px = x0; px < x0 + w; px++)
		{
			for (int pz = z0; pz < z0 + l; pz++)
			{
				int gy = groundY(world, px, pz);
				for (int yy = gy - 1; yy < maxY - 1; yy++)
					world.setBlockState(new BlockPos(px, yy, pz), Blocks.GRAVEL.getDefaultState());
			}
		}
		for (int px = x0 - 1; px < x0 + w + 1; px++)
		{
			for (int pz = z0 - 1; pz < z0 + l + 1; pz++)
			{
				occupied.add(key(px, pz));
			}
		}
		try
		{
			Class<?> c = Class.forName(cls);
			if (type == HBM_RUIN1)
			{
				// Ruin001 is a plain WorldGenerator: no-arg ctor, generate(World,Random,BlockPos)
				Object gen = c.getConstructor().newInstance();
				gen.getClass().getMethod("generate", World.class, Random.class,
						BlockPos.class).invoke(gen, world, random, new BlockPos(x0, maxY - 1, z0));
			}
			else
			{
				Object comp = c.getConstructor(Random.class, int.class, int.class).newInstance(random, x0, z0);
				// runtime names are srg (StructureComponent): getBoundingBox=func_74874_b,
				// addComponentParts=func_74875_a
				Object box = comp.getClass().getMethod("func_74874_b").invoke(comp);
				comp.getClass().getMethod("func_74875_a", World.class, Random.class,
						StructureBoundingBox.class).invoke(comp, world, random, box);
			}
			this.placedBuildings++;
			this.hbmBuildings++;
			this.hbmCount[idx]++;
			return true;
		}
		catch (Throwable t)
		{
			System.out.println("City HBM skip: " + t); // temp diag, removed for release
			return false; // HBM component not reachable - skip
		}
	}

	// central block: 36x36 plaza (central block + street bands + neighbor edges)
	// holds the hospital in the middle; the landmark (church/clock tower) appears
	// in normal blocks instead so the plaza stays a single clear building
	private void placeCentralBlock(World world, Random random, int bx, int bz, int cx, int cz)
	{
		for (int px = center.X - 18; px <= center.X + 17; px++)
		{
			for (int pz = center.Z - 18; pz <= center.Z + 17; pz++)
			{
				int y = groundY(world, px, pz) - 1;
				if (y <= 0)
					continue;
				world.setBlockState(new BlockPos(px, y, pz),
						(random.nextInt(4) == 0) ? Blocks.COBBLESTONE.getDefaultState() : Blocks.GRAVEL.getDefaultState());
			}
		}
		placeBuilding(world, random, Building.HOSPITAL, center.X, center.Z, true);
		fillSubCells(world, random, bx, bz, true);
	}

	// normal block: 15% larger ruin (including church/clock tower) +
	// small sub-cell buildings
	private void placeNormalBlock(World world, Random random, int bx, int bz, int cx, int cz)
	{
		if (random.nextInt(100) < 25)
			placeBuilding(world, random, pickLarge(random), cx, cz, true);
		fillSubCells(world, random, bx, bz, false);
	}

	// four sub-cells: small buildings + rubble; the 9-wide cell fits 7-wide
	// buildings with a margin, so nothing sticks together
	private void fillSubCells(World world, Random random, int bx, int bz, boolean central)
	{
		Integer[] order = { 0, 1, 2, 3 };
		Collections.shuffle(Arrays.asList(order), random);
		int target = 4; // build in every sub-cell - denser city
		int placed = 0;
		for (int i = 0; i < 4 && placed < target; i++)
		{
			// sub-cell center: block origin + 4 + col*SUB_W fills the 18-wide
			// block ([bx,bx+8] and [bx+9,bx+17]); streets stay outside
			int px = bx + 4 + (order[i] % 2) * SUB_W + random.nextInt(3) - 1;
			int pz = bz + 4 + (order[i] / 2) * SUB_W + random.nextInt(3) - 1;
			if (random.nextInt(100) < (central ? 90 : 85))
			{
				if (placeBuilding(world, random, central ? pickMid(random) : pickSmall(random), px, pz, true))
					placed++;
			}
			else if (random.nextInt(100) < 40)
			{
				int y = groundY(world, px, pz) - 1;
				if (y > 0)
					rubble.generate(world, random, new BlockPos(px, y, pz));
			}
		}
	}

	// small pool fits 9-wide sub-cells: sand houses are weighted down so the
	// city does not look like one big sandstone carpet (houses 2/8, stands,
	// wells and small farms 2/8 each)
	private int pickSmall(Random random)
	{
		if (HBM && random.nextInt(4) == 0)
		{
			int t = hbmSmallAvail(random);
			if (t >= 0)
				return t;
		}
		int[] small = { Building.S_HOUSE1, Building.S_HOUSE2, Building.STAND, Building.WELL,
				Building.S_FARM, Building.S_FARM, Building.STAND, Building.WELL };
		return small[random.nextInt(small.length)];
	}

	// mid pool (central block sub-cells): same small set
	private int pickMid(Random random)
	{
		if (HBM && random.nextInt(4) == 0)
		{
			int t = hbmSmallAvail(random);
			if (t >= 0)
				return t;
		}
		int[] mid = { Building.S_HOUSE1, Building.S_HOUSE2, Building.STAND, Building.WELL,
				Building.S_FARM, Building.S_FARM, Building.STAND, Building.WELL };
		return mid[random.nextInt(mid.length)];
	}

	// one HBM small building (9x4 civilian house / lab) that is not at its
	// per-type cap of 3 yet, or -1 when none is available
	private int hbmSmallAvail(Random random)
	{
		java.util.List<Integer> a = new java.util.ArrayList<Integer>();
		if (hbmCount[0] < 3)
			a.add(HBM_HOUSE);
		if (hbmCount[1] < 3)
			a.add(HBM_LAB1);
		return a.isEmpty() ? -1 : a.get(random.nextInt(a.size()));
	}

	// large pool for 15% normal blocks: ruins, church, clock tower, farms and
	// mid houses (hospital stays exclusive to the central plaza); HBM offices,
	// labs and houses join the pool when the HBM mod is loaded
	private int pickLarge(Random random)
	{
		if (HBM && random.nextInt(4) == 0)
		{
			int t = hbmLargeAvail(random);
			if (t >= 0)
				return t;
		}
		int[] large = { Building.L_HOUSE1, Building.L_HOUSE2, Building.LIBRARY, Building.DINER,
				Building.CHURCH, Building.CLOCK_TOWER, Building.L_FARM,
				Building.M_HOUSE1, Building.M_HOUSE2 };
		return large[random.nextInt(large.length)];
	}

	// one HBM large building (office / corner office / house2 / lab2 / rural
	// house) that is not at its per-type cap of 3 yet, or -1 when none is
	private int hbmLargeAvail(Random random)
	{
		java.util.List<Integer> a = new java.util.ArrayList<Integer>();
		if (hbmCount[2] < 3)
			a.add(HBM_OFFICE);
		if (hbmCount[3] < 3)
			a.add(HBM_OFFICE_C);
		if (hbmCount[4] < 3)
			a.add(HBM_HOUSE2);
		if (hbmCount[5] < 3)
			a.add(HBM_LAB2);
		if (hbmCount[6] < 3)
			a.add(HBM_RURAL);
		if (hbmCount[7] < 3)
			a.add(HBM_RUIN1);
		return a.isEmpty() ? -1 : a.get(random.nextInt(a.size()));
	}
}