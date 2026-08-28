package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Long decayed highway. The main road (generated first so it always wins the
// parallel check) connects the new city to the nearest already-generated one
// (passing two cities); a random long dead-end stretch is added after. Road
// level is fixed at the starting city ground; any lower ground is bridged
// directly (no extra conditions), ground >= 6 above is tunnelled (5-high),
// the rest is laid on the terrain. Surface: black concrete lanes, white edge
// lines + centre dashes, on a one-block polished-andesite foundation; iron-bar
// railings raised one block above the deck (on two andesite blocks below);
// bridge decks get one 3x3 pillar each side (+-5) every 5 blocks down to the
// ground. No weeds outside the railings.
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
		BlockPos dest = nearestCity(from);
		if (dest != null)
		{
			generateOne(world, random, from, dest); // main road first - it must connect cities
			System.out.println("Road gen: city " + from.getX() + "," + from.getZ() + " -> " + dest.getX() + "," + dest.getZ());
		}
		generateSideRoad(world, random, from); // long dead-end stretch, after the main link
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

	// walk the road; the deck height is fixed at the starting ground, every
	// column decides itself: lower ground -> bridge, >= +6 -> tunnel, else lay on ground
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

	// one column; 0 = on ground, 1 = bridge, 2 = tunnel
	private static int paveColumn(World world, Random random, int x, int z, int base, boolean alongX)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return 0;
		int ground = gy - 1; // surface block level
		if (ground < base)
		{
			// any ground lower than the deck: bridge directly
			paveSurface(world, random, x, z, base, false, alongX);
			return 1;
		}
		if (ground > base + 5)
		{
			// hill >= 6 above the deck: drill a 5-high tunnel
			int ty = ground - 5;
			for (int dy = 1; dy <= 4; dy++)
				for (int dx = -5; dx <= 5; dx++)
				{
					int px = alongX ? x : x + dx;
					int pz = alongX ? z + dx : z;
					world.setBlockToAir(new BlockPos(px, ty + dy, pz));
				}
			paveSurface(world, random, x, z, ty, true, alongX);
			return 2;
		}
		paveSurface(world, random, x, z, ground, false, alongX);
		return 0;
	}

	// lane deck on a polished-andesite foundation: black lanes, white edge
	// lines + centre dashes; railings raised one block (on two andesite blocks
	// below, aligned with the foundation); no weeds outside the railings
	private static void paveSurface(World world, Random random, int x, int z, int y, boolean tunnel, boolean alongX)
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
			else if (!tunnel && random.nextInt(12) == 0) // pothole / weed patch
			{
				if (random.nextBoolean())
					setWeeds(world, random, px, y + 1, pz);
				else
					world.setBlockToAir(new BlockPos(px, y, pz));
			}
			else
				setRoad(world, px, y, pz, 15);
		}
		if (tunnel)
			return;
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