package dev.vanilladev.wasteland.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.utils.Vector;
import dev.vanilladev.wasteland.world.WastelandWorldData;

public class CityGenerator implements IWorldGenerator
{
	public static List<Vector> cityLocation;
	public static int cityNum;
	private boolean generating;
	private boolean loadedWorld;
	// session-scoped dedup of built city centers (XZ key), immune to list resets
	public static java.util.Set<Long> builtCities;
	
	public CityGenerator()
	{
		GameRegistry.registerWorldGenerator(this, 10);
		cityLocation = new ArrayList<Vector>();
		cityNum = 0;
		generating = false;
		loadedWorld = false;
		builtCities = new java.util.HashSet<Long>();
	}

	public IWorldGenerator toIWorldGenerator()
	{
		IWorldGenerator generator = (IWorldGenerator) this;
		
		return generator;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		if (world.provider.getDimension() == 0 && world.getBiome(new BlockPos(chunkX*16, 0, chunkZ*16)) == WastelandBiomes.city && this.loadedWorld)
		{
			generateCity(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
	
	
	public void generateCity(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) 
	{
		
		Vector currentLoc = new Vector(chunkX * 16, world.getHeight((chunkX * 16), (chunkZ * 16)),  (chunkZ * 16));
		
		if (checkDist(currentLoc, ModConfig.minCityDistance*16) && !this.generating && !world.isRemote)
		{
			generating = true;

			List<Vector> chunks = new ArrayList<Vector>();
			chunks.add(currentLoc);
			addConnectedBiomeChunks(chunks, currentLoc, world);
			Vector center = getCenterChunk(chunks, world);
			
			
			if (chunks.size() > 8)
			{
				long key = ((long)center.X << 32) | (center.Z & 0xFFFFFFFFL);
				if (!builtCities.add(key))
				{
					System.out.println("City debug: dedup, skipping center X:" + center.X + " Z:" + center.Z);
					this.generating = false;
					return;
				}
				// keep cities off the world spawn: the city biome is scattered all
				// over the map, so the first patch that gets populated used to sit
				// right next to 0,0 and a city appeared beside spawn in every new
				// world; push the center outward along the longer axis until it
				// clears the spawn radius (cities now turn up where you explore)
				Vector spawnV = new Vector(0, 0, 0); // world origin = spawn area; keep every city clear of it
				int guard = 0;
				while (Vector.VtoVlength(center, spawnV) < ModConfig.minCitySpawnDistance && guard++ < 24)
				{
					int dx = center.X - spawnV.X, dz = center.Z - spawnV.Z;
					if (Math.abs(dx) >= Math.abs(dz))
						center.X += dx >= 0 ? 512 : -512;
					else
						center.Z += dz >= 0 ? 512 : -512;
				}
				center.Y = getWorldHeight(world, center.X, center.Z);
				System.out.println("Generating City at X:" + String.valueOf(center.X) + " Z:" + String.valueOf(center.Z));
				cityLocation.add(center);
				cityNum++;
				
				RuinedCity city = new RuinedCity(world, center, chunks, random);
				city.generate(world, random);
			}
			
			this.generating = false;
		}
		
	}


	private Vector getCenterChunk(List<Vector> chunks, World world) 
	{
		int maxX = chunks.get(0).X;
		int minX = maxX;
		int maxZ = chunks.get(0).Z;
		int minZ = maxZ;
		
		for (int i = 1; i < chunks.size(); i++)
		{
			maxX = (chunks.get(i).X > maxX) ? chunks.get(i).X : maxX;
			minX = (chunks.get(i).X < minX) ? chunks.get(i).X : minX;
			maxZ = (chunks.get(i).Z > maxZ) ? chunks.get(i).Z : maxZ;
			minZ = (chunks.get(i).Z < minZ) ? chunks.get(i).Z : minZ;
		}
		
		int cX = ((int)(((maxX - minX)/2) + minX)) & 0xF0;
		int cZ = ((int)(((maxZ - minZ)/2) + minZ)) & 0xF0;
		int h = getWorldHeight(world, cX, cZ);
		return new Vector(cX, h, cZ);
	}

	private void addConnectedBiomeChunks(List<Vector> chunks, Vector position, World world) 
	{
		int biomeID = Biome.getIdForBiome(world.getBiome(new BlockPos(position.X, 0, position.Z)));
		// iterative flood-fill (BFS queue) - the recursive version overflowed the
		// stack on large city biome patches
		java.util.ArrayDeque<Vector> queue = new java.util.ArrayDeque<Vector>();
		queue.add(position);
		while (!queue.isEmpty())
		{
			Vector current = queue.poll();
			for (int i = 0; i < 4; i++)
			{
				Vector next = chooseChunk(i, current);
				if (Biome.getIdForBiome(world.getBiome(new BlockPos(next.X, 0, next.Z))) == biomeID && !containsXZ(chunks, next))
				{
					chunks.add(next);
					queue.add(next);
				}
			}
		}
	}
	
	private static boolean containsXZ(List<Vector> chunks, Vector v)
	{
		for (Vector c : chunks)
		{
			if (c.equalsXZ(v)) return true;
		}
		return false;
	}

	private Vector chooseChunk(int i, Vector position)
	{
		Vector pos;
		
		if (i == 0)
		{
			pos = new Vector(position.X + 16, position.Y, position.Z);
		}
		else if (i == 1)
		{
			pos = new Vector(position.X - 16, position.Y, position.Z);
		}
		else if (i == 2)
		{
			pos = new Vector(position.X, position.Y, position.Z + 16);
		}
		else
		{
			pos = new Vector(position.X, position.Y, position.Z - 16);
		}
		
		return pos;
	}
	
	private boolean checkDist(Vector current, double distance)
	{
		for (int i = 0; i < cityLocation.size(); i++)
		{
			if (Vector.VtoVlength(current, cityLocation.get(i)) < distance)
			{
				return false;
			}
		}
		return true;
	}
	
	public void resetData()
	{
		this.generating = false;
		this.cityNum = 0;
		this.cityLocation.clear();
		this.builtCities.clear();
		this.loadedWorld = true;
	}
	
	public static int getWorldHeight(World world, int x, int z)
	{
		int worldHeight = world.getHeight(x, z);
		if (worldHeight == 0)
		{
			world.getChunkProvider().provideChunk(x >> 4, z >> 4);
			worldHeight = world.getHeight(x, z);
		}
		
		if (worldHeight == 0) 
		{
			System.out.println("World height still 0");
		}
		return worldHeight;
	}

	public void loadData(List<Vector> villageLoc, int size) 
	{
		this.cityLocation = villageLoc;
		this.cityNum = size;
		this.builtCities.clear();
		for (Vector v : this.cityLocation)
			this.builtCities.add(((long)v.X << 32) | (v.Z & 0xFFFFFFFFL));
		this.loadedWorld = true;
	}
	
	public void saveData(WastelandWorldData data)
	{
		data.saveCityData(cityLocation);
	}

}