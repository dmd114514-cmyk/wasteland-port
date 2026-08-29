package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// World main highway: one long polyline (10000+ blocks) that is allowed to
// turn - 3 axis-aligned segments (X -> Z -> X or Z -> X -> Z) chained at two
// elbow corners, crossing the world origin. The deck rides at a fixed height
// (64) end to end with zero relief: any ground above it is tunnelled straight
// through (4-block polished-andesite walls + an elliptical arch that springs
// from the wall tops, so roof and walls meet without a gap), any ground below
// is bridged (3x3 twin pillars every 5 blocks down to the ground), only
// exact-level ground is laid directly. Both ends of the road are broken-off
// stumps: the hillside is cut away and the last columns degrade into a ragged
// rubble heap (lanes eaten away, debris scatter). The road is paved per chunk
// from ChunkProviderWasteland.populate (no worldgen stall), and the first city
// to spawn force-loads the whole road line so the entire 10000-block road
// appears at once; a city plain that happens to sit on the road takes over its
// own section during flattening, so road and city never interfere.
public class RoadGenerator
{
	private static final int MAIN_LEN = 5000;      // one-way half length
	private static final int DECK_Y = 64;          // fixed deck height (zero relief)
	private static final int TUNNEL_H = 4;         // wall height above the deck
	private static final int BROKEN_TAIL = 30;     // broken-off length at each road end
	private static final int AXIS_X = 0;           // segment runs along X (lane is Z)
	private static final int AXIS_Z = 1;           // segment runs along Z (lane is X)

	// main road segments (axis, lane, a0, a1); defined once by ensure()
	private static final List<int[]> mainSegs = new ArrayList<int[]>();
	private static boolean mainDefined;
	// cumulative paving stats; progress line every 512 columns
	private static int cols, bridges, tunnels, pillarSets, nextReport = 512;
	// chunks already paved (idempotent: pre-warming + populate must not double-count)
	private static final java.util.HashSet<Long> pavedChunks = new java.util.HashSet<Long>();

	public static void ensure(World world, Random random)
	{
		if (mainDefined)
			return;
		mainDefined = true;
		boolean mainX = random.nextBoolean();          // first segment along X or Z
		int turn = -MAIN_LEN / 2 + random.nextInt(MAIN_LEN / 2 + 1); // turn axis coord
		int turnB = 500 + random.nextInt(2501);        // perpendicular leg length
		// three segments chained at the elbows (X->Z->X or Z->X->Z), lane 0
		// through the world origin, total length = 2*MAIN_LEN + turnB (~10000+)
		if (mainX)
		{
			mainSegs.add(new int[]{ AXIS_X, 0, -MAIN_LEN, turn });         // X: lane Z=0
			mainSegs.add(new int[]{ AXIS_Z, turn, 0, turnB });             // Z: lane X=turn
			mainSegs.add(new int[]{ AXIS_X, turnB, turn, MAIN_LEN });      // X: lane Z=turnB
		}
		else
		{
			mainSegs.add(new int[]{ AXIS_Z, 0, -MAIN_LEN, turn });         // Z: lane X=0
			mainSegs.add(new int[]{ AXIS_X, turn, 0, turnB });             // X: lane Z=turn
			mainSegs.add(new int[]{ AXIS_Z, turnB, turn, MAIN_LEN });      // Z: lane X=turnB
		}
		System.out.println("Road main: " + (mainX ? "x" : "z") + "-axis turn at "
				+ (mainX ? "x=" : "z=") + turn + " lane 0 from -" + MAIN_LEN + " to " + MAIN_LEN);
	}

	// pave the whole main road at once, called by the first city that spawns
	// (before the city plain is laid down). Chunks are force-loaded first so
	// blocks can be written, then paved directly - a freshly generated chunk is
	// not guaranteed to run populate, so the road must not depend on it.
	public static void paveMainRoad(World world, Random random)
	{
		if (!mainDefined)
			return;
		for (int[] s : mainSegs)
		{
			int axis = s[0], lane = s[1], a0 = s[2], a1 = s[3];
			int ca0 = a0 >> 4, ca1 = a1 >> 4, cl = lane >> 4;
			for (int ca = ca0; ca <= ca1; ca++)
			{
				int cx = axis == AXIS_X ? ca : cl;
				int cz = axis == AXIS_X ? cl : ca;
				world.getChunkFromBlockCoords(new BlockPos(cx << 4, 0, cz << 4)); // ensure writable
				paveChunk(world, random, cx, cz);
			}
		}
		System.out.println("Road paved total: cols=" + cols + " bridge=" + bridges
				+ " tunnel=" + tunnels + " pillarSets=" + pillarSets);
	}

	// per-chunk paving hook from ChunkProviderWasteland.populate: paves this
	// chunk's part of every segment that crosses it; counters accumulate
	public static void paveChunk(World world, Random random, int cx, int cz)
	{
		ensure(world, random);
		long key = ((long) cx << 32) | (cz & 0xFFFFFFFFL);
		if (!pavedChunks.add(key))
			return; // already paved by the pre-warm pass
		boolean any = false;
		for (int i = 0; i < mainSegs.size(); i++)
		{
			int[] s = mainSegs.get(i);
			int axis = s[0], lane = s[1], a0 = s[2], a1 = s[3];
			int cl = lane >> 4;
			if (axis == AXIS_X)
			{
				if (cz != cl)
					continue;
				int ca0 = Math.max(a0, cx * 16), ca1 = Math.min(a1, cx * 16 + 15);
				for (int a = ca0; a <= ca1; a++)
				{
					int t = paveColumn(world, random, a, lane, true, distEnd(i, a0, a1, a));
					// one pillar set (left + right) every 5 blocks of a bridge
					if (t == 1 && (a - a0) % 5 == 0)
					{
						pillars(world, a, lane, DECK_Y, true);
						pillarSets++;
					}
				}
				any = true;
			}
			else
			{
				if (cx != cl)
					continue;
				int ca0 = Math.max(a0, cz * 16), ca1 = Math.min(a1, cz * 16 + 15);
				for (int a = ca0; a <= ca1; a++)
				{
					int t = paveColumn(world, random, lane, a, false, distEnd(i, a0, a1, a));
					if (t == 1 && (a - a0) % 5 == 0)
					{
						pillars(world, lane, a, DECK_Y, false);
						pillarSets++;
					}
				}
				any = true;
			}
		}
		if (any)
			report();
	}

	// broken-end distance: 0..BROKEN_TAIL-1 near the road start (segment 0) or
	// the road end (last segment), -1 anywhere else
	private static int distEnd(int seg, int a0, int a1, int a)
	{
		if (seg == 0 && a - a0 <= BROKEN_TAIL)
			return a - a0;
		if (seg == mainSegs.size() - 1 && a1 - a <= BROKEN_TAIL)
			return a1 - a;
		return -1;
	}

	// one column; 0 = laid directly / broken end, 1 = bridge, 2 = tunnel
	private static int paveColumn(World world, Random random, int x, int z, boolean alongX, int distEnd)
	{
		int gy = groundY(world, x, z);
		if (gy <= 0)
			return 0;
		int ground = gy - 1; // surface block level
		if (distEnd >= 0)
		{
			// broken-off end: cut the hillside and leave a ragged stump
			paveBrokenEnd(world, random, x, z, alongX, distEnd);
			cols++;
			return 0;
		}
		if (ground < DECK_Y)
		{
			paveSurface(world, random, x, z, DECK_Y, 0, alongX); // dip -> bridge deck
			bridges++;
			return 1;
		}
		if (ground > DECK_Y)
		{
			paveTunnel(world, random, x, z, DECK_Y, alongX); // hill -> drill through
			tunnels++;
			return 2;
		}
		paveSurface(world, random, x, z, DECK_Y, 0, alongX); // exact level
		cols++;
		return 0;
	}

	// elliptical tunnel: 4-block walls at +-6 and an arch roof. The arch
	// springs from the wall tops (roof height 5 at the edges rising to 7 in
	// the centre over an ellipse of half-width 6), so roof and walls meet
	// with no gap; passage and roof are lined with polished andesite
	private static void paveTunnel(World world, Random random, int x, int z, int base, boolean alongX)
	{
		for (int dx = -6; dx <= 6; dx++)
		{
			int h = archH(dx);
			for (int dy = 1; dy < h; dy++)
			{
				int px = alongX ? x : x + dx;
				int pz = alongX ? z + dx : z;
				world.setBlockToAir(new BlockPos(px, base + dy, pz)); // passage air
			}
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			world.setBlockState(new BlockPos(px, base + h, pz), Blocks.STONE.getStateFromMeta(6)); // arch
		}
		// 4-block walls at +-6 (arch cells above them are already placed)
		for (int sx = -6; sx <= 6; sx += 12)
			for (int dy = 1; dy <= TUNNEL_H; dy++)
			{
				int px = alongX ? x : x + sx;
				int pz = alongX ? z + sx : z;
				world.setBlockState(new BlockPos(px, base + dy, pz), Blocks.STONE.getStateFromMeta(6));
			}
		paveSurface(world, random, x, z, base, 1, alongX); // deck at the base level
	}

	// arch roof height per lateral offset: 5 at the wall tops (connected) to 7
	// at the centre via a half-ellipse
	private static int archH(int dx)
	{
		double t = dx / 6.0;
		return 5 + (int) Math.round(2.0 * Math.sqrt(1.0 - t * t));
	}

	// broken-off road end: the hillside above the deck is cut away, the
	// foundation stays with random cracks and the lanes are randomly eaten
	// away (survival chance rises from ~0 at the very end to full at the tail
	// edge); broken cells become debris (gravel / cobble / stone / concrete
	// chunks) and the very end is a rubble heap
	private static void paveBrokenEnd(World world, Random random, int x, int z, boolean alongX, int distEnd)
	{
		// cut any hillside above the deck so the stump sits at deck height
		for (int dx = -5; dx <= 5; dx++)
		{
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			int gy = groundY(world, px, pz);
			for (int dy = DECK_Y + 1; dy <= gy; dy++)
				if (dy > 0 && dy < 256)
					world.setBlockToAir(new BlockPos(px, dy, pz));
		}
		// foundation with random cracks
		for (int dx = -5; dx <= 5; dx++)
		{
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			if (random.nextInt(10) == 0)
				world.setBlockToAir(new BlockPos(px, DECK_Y - 1, pz));
			else
				world.setBlockState(new BlockPos(px, DECK_Y - 1, pz), Blocks.STONE.getStateFromMeta(6));
		}
		double p = (distEnd + 1.0) / BROKEN_TAIL; // 0 at the very end -> 1 at the tail edge
		for (int dx = -5; dx <= 5; dx++)
		{
			int px = alongX ? x : x + dx;
			int pz = alongX ? z + dx : z;
			boolean keep = random.nextDouble() < p;
			if (keep && random.nextInt(5) != 0)
			{
				int lane = Math.abs(dx);
				int meta = (lane == 5 || lane == 0) && random.nextInt(3) != 0 ? 0 : 15;
				world.setBlockState(new BlockPos(px, DECK_Y, pz), Blocks.CONCRETE.getStateFromMeta(meta));
			}
			else
				setDebris(world, random, px, DECK_Y, pz);
		}
		// rubble heap: a piled-up second layer on the very last columns
		if (distEnd < 3)
			for (int dx = -5; dx <= 5; dx++)
			{
				int px = alongX ? x : x + dx;
				int pz = alongX ? z + dx : z;
				if (random.nextInt(3) != 0)
					setDebris(world, random, px, DECK_Y + 1 + random.nextInt(2), pz);
			}
	}

	private static void setDebris(World world, Random random, int x, int y, int z)
	{
		BlockPos p = new BlockPos(x, y, z);
		int r = random.nextInt(10);
		if (r < 4)
			world.setBlockState(p, Blocks.GRAVEL.getDefaultState());
		else if (r < 7)
			world.setBlockState(p, Blocks.COBBLESTONE.getDefaultState());
		else if (r < 9)
			world.setBlockState(p, Blocks.STONE.getDefaultState());
		else
			world.setBlockState(p, Blocks.CONCRETE.getStateFromMeta(random.nextInt(16)));
	}

	// one pillar set: two 3x3 pillars at +-5, from the deck down to the ground
	private static void pillars(World world, int x, int z, int base, boolean alongX)
	{
		for (int side = -1; side <= 1; side += 2)
		{
			int gy = groundY(world, alongX ? x : x + 5 * side, alongX ? z + 5 * side : z);
			if (gy <= 0)
				continue;
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

	// lane deck on a polished-andesite foundation: black lanes, white edge
	// lines + centre dashes; railings raised one block (on two andesite blocks
	// below, aligned with the foundation). Tunnel mode: no railings, no potholes
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
		// railings raised one block, two andesite blocks underneath
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

	private static void report()
	{
		if (cols < nextReport)
			return;
		System.out.println("Road paved: cols=" + cols + " bridge=" + bridges
				+ " tunnel=" + tunnels + " pillarSets=" + pillarSets);
		nextReport += 512;
	}
}