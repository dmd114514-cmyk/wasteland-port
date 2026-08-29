package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.vanilladev.wasteland.ruin.Building;
import dev.vanilladev.wasteland.utils.Vector;

// roadside features for the world main road (opt-in via ModConfig):
//  - cities enabled: >= 5 guaranteed waypoint cities along the whole polyline,
//    each sitting just off the deck so the road itself stays intact
//  - cities disabled: scattered single ruins pop up along both sides of the
//    road (small/medium buildings, one per ~5 road chunks so it looks random)
public class RoadSide
{
	private static final int ROAD_CITIES = 5;       // guaranteed waypoint count
	private static final int CITY_OFFSET_MIN = 100; // city center distance off the lane
	private static final int CITY_OFFSET_MAX = 220;
	private static final int BUILD_OFFSET_MIN = 34;  // single-building lane offset
	private static final int BUILD_OFFSET_MAX = 110;
	private static final int SPAWN_CLEAR = 1500;     // waypoint cities avoid this radius around world spawn
	private static final List<Long> placedCities = new ArrayList<Long>(); // waypoint city centers (XZ keys)

	// roll the polyline and drop ROAD_CITIES waypoint cities, one per ~2400
	// blocks, offset perpendicular to the road so the deck is never covered;
	// called once from paveMainRoad after the whole road has been laid
	public static void placeRoadCities(World world, Random random)
	{
		List<int[]> segs = RoadGenerator.segments();
		if (segs.isEmpty())
			return;
		long total = 0;
		for (int[] s : segs)
			total += Math.abs(s[3] - s[2]);
		long step = total / ROAD_CITIES;
		if (step <= 0)
			return;
		long pos = step / 2;
		int placed = 0;
		for (int i = 0; i < ROAD_CITIES && pos < total; i++)
		{
			int[] at = pointOnPolyline(segs, pos);
			if (at == null)
				break;
			// unwrap the polyline point: for an X-axis segment the lane is the
			// Z coordinate and the walk distance is X; for a Z-axis segment it
			// is the other way round
			int x = at[0] == RoadGenerator.AXIS_X ? at[2] : at[1];
			int z = at[0] == RoadGenerator.AXIS_X ? at[1] : at[2];
			int side = random.nextBoolean() ? 1 : -1;
			int off = CITY_OFFSET_MIN + random.nextInt(CITY_OFFSET_MAX - CITY_OFFSET_MIN + 1);
			if (at[0] == RoadGenerator.AXIS_X)
				z += side * off;
			else
				x += side * off;
			// keep the waypoint clear of the world spawn area; walk further up
			// the line until a usable spot is found
			int guard = 0;
			while ((long) x * x + (long) z * z < (long) SPAWN_CLEAR * SPAWN_CLEAR && guard++ < 4)
			{
				pos += step;
				if (pos >= total)
					break;
				at = pointOnPolyline(segs, pos);
				if (at == null)
					break;
				x = at[0] == RoadGenerator.AXIS_X ? at[2] : at[1];
				z = at[0] == RoadGenerator.AXIS_X ? at[1] : at[2];
				if (at[0] == RoadGenerator.AXIS_X)
					z += (random.nextBoolean() ? 1 : -1) * off;
				else
					x += (random.nextBoolean() ? 1 : -1) * off;
			}
			if ((long) x * x + (long) z * z < (long) SPAWN_CLEAR * SPAWN_CLEAR)
				continue; // still near spawn - skip this waypoint
			RuinedCity city = new RuinedCity(world, new Vector(x, 0, z), new ArrayList<Vector>(), random);
			city.generate(world, random);
			placedCities.add(((long) x << 32) | (z & 0xFFFFFFFFL));
			placed++;
			System.out.println("Road city #" + (i + 1) + " at X:" + x + " Z:" + z);
			pos += step;
		}
		System.out.println("Road cities placed: " + placed);
	}

	// scatter one building beside this chunk's part of the road (only when
	// cities are disabled); called from paveChunk after the deck is laid
	public static void placeRoadBuildings(World world, Random random, int cx, int cz)
	{
		List<int[]> segs = RoadGenerator.segments();
		if (segs.isEmpty())
			return;
		// never drop a single ruin inside a waypoint city: the city already
		// built its own plain and buildings there
		if (!placedCities.isEmpty())
		{
			for (Long k : placedCities)
			{
				int pcx = (int) (k >> 32), pcz = (int) (k & 0xFFFFFFFFL);
				int dx = cx * 16 + 8 - pcx, dz = cz * 16 + 8 - pcz;
				if (dx * dx + dz * dz < 90 * 90)
					return;
			}
		}
		// only segments that actually cross this chunk qualify
		List<int[]> here = new ArrayList<int[]>();
		for (int[] s : segs)
		{
			int axis = s[0], lane = s[1], a0 = s[2], a1 = s[3];
			boolean okay = false;
			if (axis == RoadGenerator.AXIS_X)
			{
				if ((lane >> 4) == cz)
				{
					int lo = Math.max(a0, cx * 16), hi = Math.min(a1, cx * 16 + 15);
					if (hi > lo)
						okay = true;
				}
			}
			else
			{
				if ((lane >> 4) == cx)
				{
					int lo = Math.max(a0, cz * 16), hi = Math.min(a1, cz * 16 + 15);
					if (hi > lo)
						okay = true;
				}
			}
			if (okay)
				here.add(s);
		}
		if (here.isEmpty())
			return;
		int[] s = here.get(random.nextInt(here.size()));
		int axis = s[0], lane = s[1], a0 = s[2], a1 = s[3];
		// columns of this segment that fall inside the current chunk
		int ca0 = Math.max(a0, (axis == RoadGenerator.AXIS_X ? cx * 16 : cz * 16));
		int ca1 = Math.min(a1, (axis == RoadGenerator.AXIS_X ? cx * 16 + 15 : cz * 16 + 15));
		if (random.nextInt(5) != 0) // ~1 building per 5 road chunks - scattered
			return;
		int a = ca0 + random.nextInt(ca1 - ca0 + 1);
		int side = random.nextBoolean() ? 1 : -1;
		int off = BUILD_OFFSET_MIN + random.nextInt(BUILD_OFFSET_MAX - BUILD_OFFSET_MIN + 1);
		int x, z;
		if (axis == RoadGenerator.AXIS_X)
		{
			x = a;
			z = lane + side * off;
		}
		else
		{
			x = lane + side * off;
			z = a;
		}
		placeBuilding(world, random, x, z);
	}

	private static void placeBuilding(World world, Random random, int x, int z)
	{
		int[] pool = { 2, 3, 5, 8, 9, 10, 11, 12, 13, 14 };
		Building b = Building.create(pool[random.nextInt(pool.length)]);
		if (b == null)
			return;
		int rot = random.nextInt(4);
		int w = ((rot & 1) == 0) ? b.width : b.length;
		int l = ((rot & 1) == 0) ? b.length : b.width;
		// foundation = most common ground under the footprint (same rule as
		// cities) so the ruin sits on the actual roadside terrain
		int[] hist = new int[257];
		for (int px = x - w / 2 - 1; px <= x + w / 2 + 1; px++)
		{
			for (int pz = z - l / 2 - 1; pz <= z + l / 2 + 1; pz++)
			{
				int g = world.getChunkFromBlockCoords(new BlockPos(px, 0, pz)).getHeightValue(px & 15, pz & 15);
				if (g > 0 && g < 257)
					hist[g]++;
			}
		}
		int maxY = 0, best = -1;
		for (int y = 1; y < 257; y++)
		{
			if (hist[y] > best)
			{
				best = hist[y];
				maxY = y;
			}
		}
		if (maxY <= 0)
			return;
		// fill low ground up to the foundation so the ruin never floats (same
		// rule as cities: gravel under the whole footprint)
		for (int px = x - w / 2; px < x - w / 2 + w; px++)
		{
			for (int pz = z - l / 2; pz < z - l / 2 + l; pz++)
			{
				int g = world.getChunkFromBlockCoords(new BlockPos(px, 0, pz)).getHeightValue(px & 15, pz & 15);
				for (int y = g; y < maxY - 1; y++)
					world.setBlockState(new BlockPos(px, y, pz), Blocks.GRAVEL.getDefaultState());
			}
		}
		b.generate(world, random, new Vector(x - w / 2, maxY - 1, z - l / 2), rot);
	}

	// walk the polyline to the column at distance dist from the start
	private static int[] pointOnPolyline(List<int[]> segs, long dist)
	{
		for (int[] s : segs)
		{
			int axis = s[0], lane = s[1], a0 = s[2], a1 = s[3];
			long len = Math.abs(a1 - a0);
			if (dist <= len)
				return new int[]{ axis, lane, a0 + (int) (a1 >= a0 ? dist : -dist) };
			dist -= len;
		}
		return null;
	}
}