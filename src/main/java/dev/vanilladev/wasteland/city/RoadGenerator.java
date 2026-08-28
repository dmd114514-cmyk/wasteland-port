package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Long decayed highway connecting cities. One road is drawn from the new city
// toward the nearest already-generated city (so a road always passes at least
// two cities), plus occasionally one random long dead-end stretch. Roads are
// axis-aligned, may cross, and parallel roads stay at least 160 blocks apart
// (10 chunks). Surface: black concrete lanes with white concrete edge lines
// and centre dashes, iron-bar railings and weeds on the shoulders; hills are
// tunnelled (5-high passage), dips are bridged.
public class RoadGenerator
{
	private static final int AXIS_X = 0;
	private static final int AXIS_Z = 1;
	private static final int PARALLEL_MIN = 160; // 10 chunks

	private static final List<BlockPos> cities = new ArrayList<BlockPos>();
	private static final List<int[]> placed = new ArrayList<int[]>(); // {axis, coord}

	public static void registerCity(BlockPos c)
	{
		cities.add(c);
	}

	public static void generate(World world, Random random, BlockPos from)
	{
		BlockPos dest = nearestCity(from);
		if (dest != null)
		{
			generateOne(world, random, from, dest); // main road to an earlier city
			System.out.println("Road gen: city " + from.getX() + "," + from.getZ() + " -> " + dest.getX() + "," + dest.getZ());
		}
		generateSideRoad(world, random, from); // one long dead-end stretch per city
	}

	private static BlockPos nearestCity(BlockPos from)
	{
		BlockPos best = null;
		int bestD = Integer.MAX_VALUE;
		for (BlockPos c : cities)
		{
			if (c.equals(from))
				continue;
			int dx = c.getX() - from.getX();
			int dz = c.getZ() - from.getZ();
			int d = dx * dx + dz * dz;
			if (d < bestD)
			{
				bestD = d;
				best = c;
			}
		}
		return best;
	}

	// random orientation long stretch from the city centre, one-sided dead end
	private static void generateSideRoad(World world, Random random, BlockPos from)
	{
		int len = 300 + random.nextInt(301);
		boolean alongX = random.nextBoolean();
		int coord = alongX ? from.getZ() : from.getX();
		int lane = (int)((random.nextDouble() * 2 - 1) * 96);
		if (!passesParallel(alongX ? AXIS_X : AXIS_Z, coord + lane))
			return;
		placed.add(new int[]{ alongX ? AXIS_X : AXIS_Z, coord + lane });
		System.out.println("Road side: " + (alongX ? "x-axis" : "z-axis") + " lane " + (coord + lane) + " len " + len);
		int a0 = alongX ? from.getX() : from.getZ();
		int a1 = a0 + (random.nextBoolean() ? len : -len);
		int h = groundY(world, from);
		int min = Math.min(a0, a1);
		int max = Math.max(a0, a1);
		for (int a = min; a <= max; a++)
		{
			int x = alongX ? a : coord + lane;
			int z = alongX ? coord + lane : a;
			h = paveColumn(world, random, x, z, h);
		}
	}

	private static void generateOne(World world, Random random, BlockPos from, BlockPos to)
	{
		boolean alongX = Math.abs(to.getX() - from.getX()) >= Math.abs(to.getZ() - from.getZ());
		int axis = alongX ? AXIS_X : AXIS_Z;
		int coord = alongX ? from.getZ() : from.getX();
		int a0 = alongX ? from.getX() : from.getZ();
		int a1 = alongX ? to.getX() : to.getZ();
		int pad = 64 + random.nextInt(97); // dead ends both sides
		int min = Math.min(a0, a1) - pad;
		int max = Math.max(a0, a1) + pad;
		if (!passesParallel(axis, coord))
			return;
		placed.add(new int[]{ axis, coord });
		int h = groundY(world, from);
		for (int a = min; a <= max; a++)
		{
			int x = alongX ? a : coord;
			int z = alongX ? coord : a;
			h = paveColumn(world, random, x, z, h);
		}
	}

	private static boolean passesParallel(int axis, int coord)
	{
		for (int[] r : placed)
			if (r[0] == axis && Math.abs(r[1] - coord) < PARALLEL_MIN)
				return false;
		return true;
	}

	private static int groundY(World world, int x, int z)
	{
		int h = world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getHeightValue(x & 15, z & 15);
		return (h > 0) ? h : 0;
	}

	private static int groundY(World world, BlockPos p)
	{
		return groundY(world, p.getX(), p.getZ());
	}

	// one column across the road; returns the new road level
	private static int paveColumn(World world, Random random, int x, int z, int roadH)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return roadH;
		int ground = gy - 1; // surface block level
		if (ground > roadH + 4)
		{
			// hill: drill a tunnel, passage is 5 blocks high
			int ty = ground - 5;
			for (int dy = 1; dy <= 4; dy++)
				for (int dx = -5; dx <= 5; dx++)
					world.setBlockToAir(new BlockPos(x + dx, ty + dy, z));
			paveSurface(world, random, x, z, ty, true);
			return ty;
		}
		if (ground < roadH - 3)
		{
			paveSurface(world, random, x, z, roadH, false); // bridge deck
			return roadH;
		}
		paveSurface(world, random, x, z, ground, false);
		return ground;
	}

	// lane deck: black concrete lanes, white edge solid lines, white centre
	// dashes; rails at +-6 and weeds at +-7 (skipped inside tunnels)
	private static void paveSurface(World world, Random random, int x, int z, int y, boolean tunnel)
	{
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
			if (white)
				setRoad(world, x + dx, y, z, 0);
			else if (!tunnel && random.nextInt(12) == 0) // pothole / weed patch
			{
				if (random.nextBoolean())
					setWeeds(world, random, x + dx, y, z);
				else
					world.setBlockToAir(new BlockPos(x + dx, y, z));
			}
			else
				setRoad(world, x + dx, y, z, 15);
		}
		if (tunnel)
			return;
		// railings (broken here and there) and shoulder weeds
		for (int sx = -6; sx <= 6; sx += 12)
			if (random.nextInt(4) != 0)
				world.setBlockState(new BlockPos(x + sx, y, z), Blocks.IRON_BARS.getDefaultState());
		for (int sx = -7; sx <= 7; sx += 14)
			if (random.nextInt(5) < 4)
				setWeeds(world, random, x + sx, y, z);
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
}