package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Long decayed highway connecting cities. One road is drawn from the new city
// toward the nearest already-generated city (so a road always passes at least
// two cities), plus one random long dead-end stretch per city. Roads run
// alongside the cities (offset outside the built core, so buildings stay),
// are axis-aligned, may cross, and parallel roads stay at least 160 blocks
// apart (10 chunks). Surface: black concrete lanes with white concrete edge
// lines and centre dashes, iron-bar railings and weeds on the shoulders;
// hills are tunnelled (5-high passage), dips are bridged with pillars.
public class RoadGenerator
{
	private static final int AXIS_X = 0;
	private static final int AXIS_Z = 1;
	private static final int PARALLEL_MIN = 160; // 10 chunks
	private static final int CITY_RADIUS = 80;   // city core half width + margin

	private static final List<BlockPos> cities = new ArrayList<BlockPos>();
	private static final List<int[]> placed = new ArrayList<int[]>(); // {axis, coord}

	public static void registerCity(BlockPos c)
	{
		cities.add(c);
	}

	public static void generate(World world, Random random, BlockPos from)
	{
		generateSideRoad(world, random, from); // one long dead-end stretch per city
		BlockPos dest = nearestCity(from);
		if (dest != null)
		{
			generateOne(world, random, from, dest); // main road to an earlier city
			System.out.println("Road gen: city " + from.getX() + "," + from.getZ() + " -> " + dest.getX() + "," + dest.getZ());
		}
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
			if (d > CITY_RADIUS * CITY_RADIUS && d < bestD)
			{
				bestD = d;
				best = c;
			}
		}
		return best;
	}

	// random orientation long stretch, offset outside the city core, one-sided dead end
	private static void generateSideRoad(World world, Random random, BlockPos from)
	{
		int len = 300 + random.nextInt(301);
		boolean alongX = random.nextBoolean();
		int coord = alongX ? from.getZ() : from.getX();
		int lane = coord + (random.nextBoolean() ? 1 : -1) * (CITY_RADIUS + random.nextInt(48));
		if (!passesParallel(alongX ? AXIS_X : AXIS_Z, lane))
			return;
		placed.add(new int[]{ alongX ? AXIS_X : AXIS_Z, lane });
		System.out.println("Road side: " + (alongX ? "x-axis" : "z-axis") + " lane " + lane + " len " + len);
		int a0 = alongX ? from.getX() : from.getZ();
		int a1 = a0 + (random.nextBoolean() ? len : -len);
		pave(world, random, alongX, lane, Math.min(a0, a1), Math.max(a0, a1));
	}

	// main road between two cities, offset outside both cores, dead ends at both far ends
	private static void generateOne(World world, Random random, BlockPos from, BlockPos to)
	{
		boolean alongX = Math.abs(to.getX() - from.getX()) >= Math.abs(to.getZ() - from.getZ());
		int axis = alongX ? AXIS_X : AXIS_Z;
		int base = alongX ? from.getZ() : from.getX();
		int lane = base + (random.nextBoolean() ? 1 : -1) * (CITY_RADIUS + random.nextInt(48));
		if (!passesParallel(axis, lane))
			return;
		placed.add(new int[]{ axis, lane });
		int a0 = alongX ? from.getX() : from.getZ();
		int a1 = alongX ? to.getX() : to.getZ();
		int pad = 64 + random.nextInt(97); // dead ends both sides
		pave(world, random, alongX, lane, Math.min(a0, a1) - pad, Math.max(a0, a1) + pad);
	}

	private static boolean passesParallel(int axis, int coord)
	{
		for (int[] r : placed)
			if (r[0] == axis && Math.abs(r[1] - coord) < PARALLEL_MIN)
				return false;
		return true;
	}

	// walk the road along its axis; the cross section expands perpendicular to it
	private static void pave(World world, Random random, boolean alongX, int lane, int a0, int a1)
	{
		int x = alongX ? a0 : lane;
		int z = alongX ? lane : a0;
		int h = groundY(world, x, z);
		int bridgeSince = -1;
		for (int a = a0; a <= a1; a++)
		{
			x = alongX ? a : lane;
			z = alongX ? lane : a;
			boolean bridged = paveColumn(world, random, x, z, h, alongX);
			if (bridged && bridgeSince < 0)
				bridgeSince = a;
			if (!bridged)
				bridgeSince = -1;
			// bridge pillars every 8 blocks along a bridge span
			if (bridged && (a - bridgeSince) % 8 == 0)
				pillars(world, random, x, z, h, alongX);
			h = columnHeight(world, x, z, h, alongX);
		}
	}

	// track the road level across the span
	private static int columnHeight(World world, int x, int z, int roadH, boolean alongX)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return roadH;
		int ground = gy - 1;
		if (ground > roadH + 4)
			return ground - 5; // in a tunnel now
		if (ground < roadH - 3)
			return roadH; // bridge span keeps its level
		return ground;
	}

	// bridge pillars from the deck down to the ground below
	private static void pillars(World world, Random random, int x, int z, int roadH, boolean alongX)
	{
		for (int dx = -4; dx <= 4; dx += 4)
		{
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			int gy = groundY(world, px, pz);
			for (int y = roadH - 1; y > gy - 1; y--)
				if (y > 0)
					world.setBlockState(new BlockPos(px, y, pz), Blocks.CONCRETE.getStateFromMeta(15));
		}
	}

	// one column across the road; returns true when this column is a bridge deck
	private static boolean paveColumn(World world, Random random, int x, int z, int roadH, boolean alongX)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return false;
		int ground = gy - 1; // surface block level
		if (ground > roadH + 4)
		{
			// hill: drill a tunnel, passage is 5 blocks high
			int ty = ground - 5;
			for (int dy = 1; dy <= 4; dy++)
				for (int dx = -5; dx <= 5; dx++)
				{
					int px = alongX ? x : x + dx;
					int pz = alongX ? z + dx : z;
					world.setBlockToAir(new BlockPos(px, ty + dy, pz));
				}
			paveSurface(world, random, x, z, ty, true, alongX);
			return false;
		}
		if (ground < roadH - 3)
		{
			paveSurface(world, random, x, z, roadH, false, alongX); // bridge deck
			return true;
		}
		paveSurface(world, random, x, z, ground, false, alongX);
		return false;
	}

	// lane deck: black concrete lanes, white edge solid lines, white centre
	// dashes; rails at +-6 and weeds at +-7 (skipped inside tunnels)
	private static void paveSurface(World world, Random random, int x, int z, int y, boolean tunnel, boolean alongX)
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
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			if (white)
				setRoad(world, px, y, pz, 0);
			else if (!tunnel && random.nextInt(12) == 0) // pothole / weed patch
			{
				if (random.nextBoolean())
					setWeeds(world, random, px, y, pz);
				else
					world.setBlockToAir(new BlockPos(px, y, pz));
			}
			else
				setRoad(world, px, y, pz, 15);
		}
		if (tunnel)
			return;
		// railings (broken here and there) and shoulder weeds
		for (int sx = -6; sx <= 6; sx += 12)
			if (random.nextInt(4) != 0)
			{
				int px = alongX ? x : x + sx;
				int pz = alongX ? z + sx : z;
				world.setBlockState(new BlockPos(px, y, pz), Blocks.IRON_BARS.getDefaultState());
			}
		for (int sx = -7; sx <= 7; sx += 14)
			if (random.nextInt(5) < 4)
			{
				int px = alongX ? x : x + sx;
				int pz = alongX ? z + sx : z;
				setWeeds(world, random, px, y, pz);
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