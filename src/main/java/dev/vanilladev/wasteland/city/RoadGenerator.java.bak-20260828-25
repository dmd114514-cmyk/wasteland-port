package dev.vanilladev.wasteland.city;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.utils.Vector;

// World main highway: one axis-aligned road, 10000 blocks long, through the
// world center (lane 0). It is paved chunk by chunk during population so a
// 10000-long road never stalls worldgen. City centers are aligned to sit
// beside the road (176-275 blocks off the lane, outside the city plain), so
// cities grow along both sides and the road never cuts through a city plain.
// Surface: black concrete lanes, white edge lines + centre dashes, on a
// polished-andesite foundation; iron-bar railings raised one block (two
// andesite blocks below); ground below the fixed deck is bridged with 3x3
// twin pillars every 5 blocks down to the ground; hills >= 6 above the deck
// are tunnelled (5-high passage).
public class RoadGenerator
{
	private static final int MAIN_LEN = 5000;   // +-5000 = 10000 blocks
	private static final int CITY_RADIUS = 80;
	private static final int DECK_Y = 64;       // fixed deck height

	private static boolean initialized = false;
	private static boolean mainAlongX = true;
	private static int mainLane = 0;

	public static void ensure(Random random)
	{
		if (initialized)
			return;
		initialized = true;
		mainAlongX = random.nextBoolean();
		mainLane = 0;
		System.out.println("Road main: " + (mainAlongX ? "x-axis" : "z-axis") + " lane " + mainLane + " from " + (-MAIN_LEN) + " to " + MAIN_LEN);
	}

	// pull a city center to one side of the main road, outside the city plain;
	// tries further offsets until it lands on wasteland ground for the city
	public static Vector alignCity(World world, Vector v, Random random)
	{
		ensure(random);
		int sideBase = CITY_RADIUS + 96 + random.nextInt(100);
		for (int attempt = 0; attempt < 6; attempt++)
		{
			int side = (random.nextBoolean() ? 1 : -1) * (sideBase + attempt * 32);
			int x = mainAlongX ? v.X : side;
			int z = mainAlongX ? side : v.Z;
			if (biomeOk(world, x, z) || attempt == 5)
				return new Vector(x, v.Y, z);
		}
		return v;
	}

	private static boolean biomeOk(World world, int x, int z)
	{
		Biome b = world.getBiome(new BlockPos(x, 0, z));
		return b == WastelandBiomes.apocalypse || b == WastelandBiomes.city;
	}

	// pave the part of the main road crossing this chunk (16 columns max)
	public static void paveChunk(World world, Random random, int chunkX, int chunkZ)
	{
		ensure(random);
		int minX = chunkX * 16, minZ = chunkZ * 16;
		int maxX = minX + 15, maxZ = minZ + 15;
		if (mainAlongX)
		{
			if (mainLane < minZ - 7 || mainLane > maxZ + 7)
				return;
			int a0 = Math.max(minX, -MAIN_LEN), a1 = Math.min(maxX, MAIN_LEN);
			for (int a = a0; a <= a1; a++)
			{
				int type = paveColumn(world, random, a, mainLane);
				if (type == 1 && Math.floorMod(a, 5) == 0)
					pillars(world, a, mainLane);
			}
		}
		else
		{
			if (mainLane < minX - 7 || mainLane > maxX + 7)
				return;
			int a0 = Math.max(minZ, -MAIN_LEN), a1 = Math.min(maxZ, MAIN_LEN);
			for (int a = a0; a <= a1; a++)
			{
				int type = paveColumn(world, random, mainLane, a);
				if (type == 1 && Math.floorMod(a, 5) == 0)
					pillars(world, mainLane, a);
			}
		}
	}

	// one 3x3 pillar each side (+-5), from the deck down to the ground
	private static void pillars(World world, int x, int z)
	{
		for (int side = -1; side <= 1; side += 2)
		{
			int px = x + 5 * side; // pillar centre on the lateral axis
			int gy = groundY(world, px, z);
			for (int dd = -1; dd <= 1; dd++)
				for (int de = -1; de <= 1; de++)
				{
					int bx = mainAlongX ? x + dd : px + dd;
					int bz = mainAlongX ? z + 5 * side + de : z + de;
					for (int y = DECK_Y - 1; y > gy - 1; y--)
						if (y > 0)
							world.setBlockState(new BlockPos(bx, y, bz), Blocks.CONCRETE.getStateFromMeta(15));
				}
		}
	}

	// one column at the fixed deck; 0 = on ground, 1 = bridge, 2 = tunnel
	private static int paveColumn(World world, Random random, int x, int z)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return 0;
		int ground = gy - 1; // surface block level
		if (ground < DECK_Y)
		{
			paveSurface(world, random, x, z, DECK_Y, false); // any dip: bridge
			return 1;
		}
		if (ground > DECK_Y + 5)
		{
			// hill >= 6 above the deck: drill a 5-high tunnel
			int ty = ground - 5;
			for (int dy = 1; dy <= 4; dy++)
				for (int dx = -5; dx <= 5; dx++)
					world.setBlockToAir(new BlockPos(x + dx, ty + dy, z));
			paveSurface(world, random, x, z, ty, true);
			return 2;
		}
		paveSurface(world, random, x, z, ground, false);
		return 0;
	}

	// lane deck on a polished-andesite foundation: black lanes, white edge
	// lines + centre dashes; railings raised one block (two andesite blocks
	// below, aligned with the foundation); no weeds outside the railings
	private static void paveSurface(World world, Random random, int x, int z, int y, boolean tunnel)
	{
		// polished-andesite foundation under the whole deck
		for (int dx = -5; dx <= 5; dx++)
			world.setBlockState(new BlockPos(x + dx, y - 1, z), Blocks.STONE.getStateFromMeta(6));
		for (int dx = -5; dx <= 5; dx++)
		{
			int lane = Math.abs(dx);
			boolean white;
			if (lane == 5)
				white = random.nextInt(25) != 0;      // edge solid line, worn
			else if (lane == 0)
				white = random.nextInt(3) < 2;        // centre dashes
			else
				white = false;                        // black lane
			int px = x + dx;
			if (white)
				setRoad(world, px, y, z, 0);
			else if (!tunnel && random.nextInt(12) == 0) // pothole / weed patch
			{
				if (random.nextBoolean())
					setWeeds(world, random, px, y + 1, z);
				else
					world.setBlockToAir(new BlockPos(px, y, z));
			}
			else
				setRoad(world, px, y, z, 15);
		}
		if (tunnel)
			return;
		// railings raised one block, two andesite blocks underneath; no outer weeds
		for (int sx = -6; sx <= 6; sx += 12)
		{
			int px = x + sx;
			world.setBlockState(new BlockPos(px, y - 1, z), Blocks.STONE.getStateFromMeta(6));
			if (random.nextInt(8) != 0)
			{
				world.setBlockState(new BlockPos(px, y, z), Blocks.STONE.getStateFromMeta(6));
				world.setBlockState(new BlockPos(px, y + 1, z), Blocks.IRON_BARS.getDefaultState());
			}
		}
	}

	private static void setRoad(World world, int x, int y, int z, int meta)
	{
		world.setBlockState(new BlockPos(x, y, z), Blocks.CONCRETE.getStateFromMeta(meta));
	}

	private static void setWeeds(World world, Random random, int x, int y, int z)
	{
		BlockPos p = new BlockPos(x, y, z);
		if (random.nextBoolean())
			world.setBlockState(p, Blocks.TALLGRASS.getStateFromMeta(1));
		else
			world.setBlockState(p, Blocks.DEADBUSH.getDefaultState());
	}

	private static int groundY(World world, int x, int z)
	{
		int h = world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getHeightValue(x & 15, z & 15);
		return (h > 0) ? h : 0;
	}
}