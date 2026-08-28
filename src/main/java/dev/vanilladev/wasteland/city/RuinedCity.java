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
	// HBM CE linkage: NTM civilian house (9x4) and large office (14x5),
	// generated reflectively when the HBM mod is loaded
	private static final int HBM_HOUSE = 1000;
	private static final int HBM_OFFICE = 1001;
	private static final boolean HBM = Loader.isModLoaded("hbm");

	private final Vector center;
	private final List<Vector> chunks;
	private final RuinLightposts lamp = new RuinLightposts(null);
	private final WorldGenRandomRubble rubble = new WorldGenRandomRubble();
	// footprints taken by buildings (inflated by 1 block for spacing)
	private final java.util.HashSet<Long> occupied = new java.util.HashSet<Long>();
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
		if (type == HBM_HOUSE || type == HBM_OFFICE)
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
		int w = (type == HBM_HOUSE) ? 9 : 14;
		int l = (type == HBM_HOUSE) ? 4 : 5;
		int x0 = groundSnap ? x - w / 2 : x;
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
			String cls = (type == HBM_HOUSE)
					? "com.hbm.world.gen.component.CivilianFeatures$NTMHouse1"
					: "com.hbm.world.gen.component.OfficeFeatures$LargeOffice";
			Class<?> c = Class.forName(cls);
			Object comp = c.getConstructor(Random.class, int.class, int.class).newInstance(random, x0, z0);
			// runtime names are srg (StructureComponent): getBoundingBox=func_74874_b,
			// addComponentParts=func_74875_a
			Object box = comp.getClass().getMethod("func_74874_b").invoke(comp);
			comp.getClass().getMethod("func_74875_a", World.class, Random.class,
					StructureBoundingBox.class).invoke(comp, world, random, box);
			this.placedBuildings++;
			this.hbmBuildings++;
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
		if (random.nextInt(100) < 15)
			placeBuilding(world, random, pickLarge(random), cx, cz, true);
		fillSubCells(world, random, bx, bz, false);
	}

	// four sub-cells: small buildings + rubble; the 9-wide cell fits 7-wide
	// buildings with a margin, so nothing sticks together
	private void fillSubCells(World world, Random random, int bx, int bz, boolean central)
	{
		Integer[] order = { 0, 1, 2, 3 };
		Collections.shuffle(Arrays.asList(order), random);
		int target = central ? (3 + random.nextInt(2)) : (2 + random.nextInt(3));
		int placed = 0;
		for (int i = 0; i < 4 && placed < target; i++)
		{
			// sub-cell center: block origin + 4 + col*SUB_W fills the 18-wide
			// block ([bx,bx+8] and [bx+9,bx+17]); streets stay outside
			int px = bx + 4 + (order[i] % 2) * SUB_W;
			int pz = bz + 4 + (order[i] / 2) * SUB_W;
			if (random.nextInt(100) < (central ? 65 : 55))
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

	// small pool fits 7-wide sub-cells (no 9-wide or wider buildings)
	private int pickSmall(Random random)
	{
		// HBM linkage: 1/4 of the small slots become an NTM civilian house
		if (HBM && random.nextInt(4) == 0)
			return HBM_HOUSE;
		int[] small = { Building.S_HOUSE1, Building.S_HOUSE2, Building.STAND, Building.WELL };
		return small[random.nextInt(small.length)];
	}

	// mid pool: same 7-wide limit, used by the central block's sub-cells
	private int pickMid(Random random)
	{
		if (HBM && random.nextInt(4) == 0)
			return HBM_HOUSE;
		int[] mid = { Building.S_HOUSE1, Building.S_HOUSE2, Building.STAND, Building.WELL };
		return mid[random.nextInt(mid.length)];
	}

	// large pool for 15% normal blocks: ruins + church + clock tower
	// (hospital stays exclusive to the central plaza; 24 wide does not fit
	// a normal 18-wide block); HBM office joins the pool when HBM is loaded
	private int pickLarge(Random random)
	{
		if (HBM && random.nextInt(4) == 0)
			return HBM_OFFICE;
		int[] large = { Building.L_HOUSE1, Building.L_HOUSE2, Building.LIBRARY, Building.DINER,
				Building.CHURCH, Building.CLOCK_TOWER };
		return large[random.nextInt(large.length)];
	}
}