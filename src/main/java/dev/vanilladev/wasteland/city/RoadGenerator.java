package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Decayed highways between cities. registerCity() is called when a city is
// built; generate(from) then lays the main link to the nearest city (lane
// offset outside both city cores, so the road never cuts a city plain) plus a
// long dead-end stretch. The deck height starts at the source city ground and
// each column reads the terrain: gentle up-and-down is laid on the ground (the
// road keeps the relief, not a monotone flat deck), deep dips are bridged
// (3x3 twin pillars every 5 blocks), steep hills (>= 6 above the deck) are
// tunnelled straight through - a 4-block polished-andesite wall on each side
// and an elliptical arch over the passage, like a real highway tunnel.
public class RoadGenerator
{
	private static final int AXIS_X = 0;
	private static final int AXIS_Z = 1;
	private static final int PARALLEL_MIN = 160; // 10 chunks same-axis spacing
	private static final int CITY_RADIUS = 80;   // city core half width + margin
	private static final int SLOPE_MAX = 5;      // gentle relief band above the deck
	private static final int TUNNEL_H = 4;       // wall height above the deck

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
			generateOne(world, random, from, dest); // main link first - it must connect cities
			System.out.println("Road gen: city " + from.getX() + "," + from.getZ() + " -> " + dest.getX() + "," + dest.getZ());
		}
		generateSideRoad(world, random, from); // long dead-end stretch after the link
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

	private static void generateSideRoad(World world, Random random, BlockPos from)
	{
		int len = 400 + random.nextInt(401); // long
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

	// walk the road; the deck starts at the source city ground and every column
	// decides itself: gentle ground -> lay on it (relief kept), deep dip ->
	// bridge at deck height, steep hill -> tunnel. Piles per-road stats.
	private static void pave(World world, Random random, boolean alongX, int lane, int a0, int a1)
	{
		int base = groundY(world, alongX ? a0 : lane, alongX ? lane : a0);
		if (base <= 0)
			base = 64;
		int cols = 0, bridges = 0, tunnels = 0, pillarSets = 0;
		for (int a = a0; a <= a1; a++)
		{
			int x = alongX ? a : lane;
			int z = alongX ? lane : a;
			int type = paveColumn(world, random, x, z, base, alongX);
			cols++;
			if (type == 1)
			{
				bridges++;
				// one pillar set (left + right) every 5 blocks of the road
				if ((a - a0) % 5 == 0)
				{
					pillars(world, x, z, base, alongX);
					pillarSets++;
				}
			}
			else if (type == 2)
				tunnels++;
		}
		System.out.println("Road paved: cols=" + cols + " bridge=" + bridges + " tunnel=" + tunnels + " pillarSets=" + pillarSets);
	}

	// one pillar set: two 3x3 pillars at +-5, from the deck down to the ground
	private static void pillars(World world, int x, int z, int base, boolean alongX)
	{
		for (int side = -1; side <= 1; side += 2)
		{
			int gy = groundY(world, alongX ? x : x + 5 * side, alongX ? z + 5 * side : z);
			for (int dd = -1; dd <= 1; dd++)
			{
				for (int de = -1; de <= 1; de++)
				{
					int px = alongX ? x + dd : x + 5 * side;
					int pz = alongX ? z + 5 * side : z + de;
					for (int y = base - 1; y > gy - 1; y--)
						if (y > 0)
							world.setBlockState(new BlockPos(px, y, pz), Blocks.CONCRETE.getStateFromMeta(15));
				}
			}
		}
	}

	// one column; 0 = on ground (relief kept), 1 = bridge, 2 = tunnel
	private static int paveColumn(World world, Random random, int x, int z, int base, boolean alongX)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return 0;
		int ground = gy - 1; // surface block level
		if (ground < base)
		{
			// any ground lower than the deck: bridge directly
			paveSurface(world, random, x, z, base, 0, alongX);
			return 1;
		}
		if (ground > base + SLOPE_MAX)
		{
			// steep hill: no climbing, tunnel straight through
			paveTunnel(world, random, x, z, base, alongX);
			return 2;
		}
		paveSurface(world, random, x, z, ground, 0, alongX); // gentle relief: lay on it
		return 0;
	}

	// elliptical tunnel: 4-block walls at +-6 and an arch roof, the passage and
	// roof lined with polished andesite (same material as the foundation)
	private static void paveTunnel(World world, Random random, int x, int z, int base, boolean alongX)
	{
		// arch roof height per lateral offset: 4 (wall top) at the edges rising
		// to 7 at the centre via a half-ellipse of half-width 6
		for (int dx = -6; dx <= 6; dx++)
		{
			int h = TUNNEL_H + (int) Math.round(3.0 * Math.sqrt(1.0 - dx * dx / 36.0));
			for (int dy = 1; dy < h; dy++)
			{
				int px = alongX ? x : x + dx;
				int pz = alongX ? z + dx : z;
				world.setBlockToAir(new BlockPos(px, base + dy, pz)); // passage air
			}
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			world.setBlockState(new BlockPos(px, base + h, pz), Blocks.STONE.getStateFromMeta(6)); // arch roof
		}
		// 4-block walls at +-6 (roof cells above them are already placed)
		for (int sx = -6; sx <= 6; sx += 12)
			for (int dy = 1; dy <= TUNNEL_H; dy++)
			{
				int px = alongX ? x : x + sx;
				int pz = alongX ? z + sx : z;
				world.setBlockState(new BlockPos(px, base + dy, pz), Blocks.STONE.getStateFromMeta(6));
			}
		paveSurface(world, random, x, z, base, 1, alongX); // deck at the base level
	}

	// lane deck on a polished-andesite foundation: black lanes, white edge
	// lines + centre dashes; railings raised one block (on two andesite blocks
	// below, aligned with the foundation). Tunnel mode: no railings, no potholes
	// - the inside stays clean, the walls take over.
	private static void paveSurface(World world, Random random, int x, int z, int y, int mode, boolean alongX)
	{
		// polished-andesite foundation under the whole deck
		for (int dx = -5; dx <= 5; dx++)
		{
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			world.setBlockState(new BlockPos(px, y - 1, pz), Blocks.STONE.getStateFromMeta(6));
		}
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
			else if (mode == 0 && random.nextInt(12) == 0) // pothole / weed patch
			{
				if (random.nextBoolean())
					setWeeds(world, random, px, y + 1, pz);
				else
					world.setBlockToAir(new BlockPos(px, y, pz));
			}
			else
				setRoad(world, px, y, pz, 15);
		}
		if (mode == 1)
			return; // no railings inside a tunnel
		// railings raised one block, two andesite blocks underneath; no outer weeds
		for (int sx = -6; sx <= 6; sx += 12)
		{
			int px = alongX ? x : x + sx;
			int pz = alongX ? z + sx : z;
			world.setBlockState(new BlockPos(px, y - 1, pz), Blocks.STONE.getStateFromMeta(6));
			if (random.nextInt(8) != 0)
			{
				world.setBlockState(new BlockPos(px, y, pz), Blocks.STONE.getStateFromMeta(6));
				world.setBlockState(new BlockPos(px, y + 1, pz), Blocks.IRON_BARS.getDefaultState());
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