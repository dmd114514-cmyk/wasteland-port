package dev.vanilladev.wasteland.ruin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.RuinConfig;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.ruin.Building.LootStack;
import dev.vanilladev.wasteland.ruin.code.BuildingCode;
import dev.vanilladev.wasteland.utils.CustomItemStack;
import dev.vanilladev.wasteland.utils.Vector;
import dev.vanilladev.wasteland.world.WastelandWorldData;

public class RuinVillageGenerator implements IWorldGenerator
{
	public static List<Vector> villageLocation;
	public static int villageNum;
	private boolean generating;
	private boolean loadedWorld;
	
	public RuinVillageGenerator()
	{
		GameRegistry.registerWorldGenerator(this, 10);
		villageLocation = new ArrayList<Vector>();
		villageNum = 0;
		generating = false;
		loadedWorld = false;
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
		if (world.provider.getDimension() == 0 && world.getBiome(new BlockPos(chunkX*16, 0, chunkZ*16)) == WastelandBiomes.apocalypse && this.loadedWorld)
		{
			generateVillage(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
	
	
	public void generateVillage(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) 
	{
		
		Vector currentLoc = new Vector(chunkX * 16, world.getHeight((chunkX * 16), (chunkZ * 16)),  (chunkZ * 16));
		
		if (checkDist(currentLoc, ModConfig.minVillageDistance*16) && !this.generating && !world.isRemote)
		{
			generating = true;
			int villageSize = random.nextInt(3);
			int villageDim = (villageSize+3)*16;
			int[][] heightMap = getTerrainLevelMap(world, currentLoc, villageDim);
			
			if (checkLevelVariation(heightMap[0], 6) && checkCenter(currentLoc, 3, 1, world))
			{
				System.out.println("Generating Village at X:" + String.valueOf(chunkX * 16) + " Z:" + String.valueOf(chunkZ * 16));
				villageLocation.add(currentLoc);
				villageNum++;
				
				RuinedVillage village = new RuinedVillage(world, chunkX * 16, chunkZ * 16, villageDim, villageSize, random);
				village.generate(world, random);
			}
			
			this.generating = false;
		}
		
	}

	public static void spawnBunker(Vector pos, World world) 
	{
		byte[] blocks = BuildingCode.Bunker.BLOCKS;
		byte[] data = BuildingCode.Bunker.DATA;
		int count = 0;
		int worldHeight = getWorldHeight(world, pos.X - 1, pos.Z - 5);
		Random random = new Random();
				
		int cX = (int)((float)BuildingCode.Bunker.WIDTH/2.0f);
		int cZ = (int)((float)BuildingCode.Bunker.LENGTH/2.0f);
		
		System.out.println(pos.toCustomString());
		for (int k = 0; k < BuildingCode.Bunker.HEIGHT ; k++)
		{
			for (int j = 0; j < BuildingCode.Bunker.LENGTH ; j++)
			{
				for (int i = 0; i < BuildingCode.Bunker.WIDTH ; i++)
				{
					if (blocks[count] == 54)
					{
						world.setBlockState(new BlockPos(pos.X - cX + i, pos.Y + k, pos.Z - cZ + j), Block.getBlockById(blocks[count]).getStateFromMeta(data[count]));
						TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(pos.X - cX + i, pos.Y + k, pos.Z - cZ + j));
						LootStack loot = new LootStack(RuinConfig.getLoot(RuinConfig.startLoot), RuinConfig.startLootMax, RuinConfig.startLootMin, RuinConfig.startLootRepeat);
						CustomItemStack.placeLoot(random, chest, CustomItemStack.getLootItems(random, loot.items, loot.minNum, loot.maxNum, loot.repeat));
					}
					else if (blocks[count] == 98)
					{
						int meta = random.nextInt(10);
						meta = (meta > 6) ? random.nextInt(5) : 0;
						meta = (meta > 2) ? random.nextInt(2) + 1 : meta;
						world.setBlockState(new BlockPos(pos.X - cX + i, pos.Y + k, pos.Z - cZ + j), Block.getBlockById(blocks[count]).getStateFromMeta(meta));
					}
					else
					{
						world.setBlockState(new BlockPos(pos.X - cX + i, pos.Y + k, pos.Z - cZ + j), Block.getBlockById(blocks[count]).getStateFromMeta(data[count]));
					}
					
					count++;
				}
			}
		}
		
		world.setBlockState(new BlockPos(pos.X - 1, pos.Y + 1, pos.Z - 4), Blocks.AIR.getDefaultState());
		world.setBlockState(new BlockPos(pos.X - 1, pos.Y + 2, pos.Z - 4), Blocks.AIR.getDefaultState());
		for (int i = pos.Y + 1; i < worldHeight; i++)
		{
			world.setBlockState(new BlockPos(pos.X - 1, i, pos.Z - 5), Blocks.LADDER.getStateFromMeta(3));
		}
		world.setBlockState(new BlockPos(pos.X - 1, worldHeight, pos.Z - 5), Blocks.AIR.getStateFromMeta(3));
		world.setBlockState(new BlockPos(pos.X - 1, worldHeight+1, pos.Z - 5), Blocks.AIR.getStateFromMeta(3));
		world.setBlockState(new BlockPos(pos.X - 1, worldHeight+2, pos.Z - 5), Blocks.AIR.getStateFromMeta(3));

		// bed inside the bunker (the original blueprint has none)
		BlockPos bedFoot = new BlockPos(pos.X, pos.Y + 1, pos.Z);
		world.setBlockState(bedFoot, Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH)
				.withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT).withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)));
		world.setBlockState(bedFoot.north(), Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH)
				.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD).withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)));
	}

	private boolean checkCenter(Vector currentLoc, int rad, int maxVar, World world) 
	{
		int max = getWorldHeight(world, currentLoc.X, currentLoc.Z);
		int min = max;
		int h;
		for (int i = 0; i < (rad*2) + 1; i ++)
		{
			for (int j = 0; j < (rad*2) + 1; j ++)
			{
				h = getWorldHeight(world, currentLoc.X - rad + i, currentLoc.Z - rad + j);
				max = (h > max) ? h : max;
				min = (h < min) ? h : min;
			}
		}
		return (max - min) <= maxVar;
	}

	private boolean checkDist(Vector current, double distance)
	{
		for (int i = 0; i < villageLocation.size(); i++)
		{
			if (Vector.VtoVlength(current, villageLocation.get(i)) < distance)
			{
				return false;
			}
		}
		return true;
	}
	
	public void resetData()
	{
		this.generating = false;
		this.villageNum = 0;
		this.villageLocation.clear();
		this.loadedWorld = true;
	}
	
	private boolean checkLevelVariation(int[] heightMap, int maxBlockVariation)
	{
		int maxHeight = heightMap[0];
		int minHeight = maxHeight;
		
		for (int i = 1; i < heightMap.length; i++)
		{
			if (heightMap[i] > maxHeight)
			{
				maxHeight = heightMap[i];
			}
			if (heightMap[i] < minHeight)
			{
				minHeight = heightMap[i];
			}
		}
		return ((maxHeight-minHeight) <= maxBlockVariation);
	}
	
	private int[][] getTerrainLevelMap(World world, Vector position, int dim) 
	{
		int villageDim = dim/2;
		
		int[][] heightMap = new int[2][villageDim*villageDim];
		boolean flag;
		for (int i = 0; i < villageDim; i++)
		{
			for (int j = 0; j < villageDim; j++)
			{
				flag = true;
				for (byte tries = 0; (tries < 3) && flag; tries ++)
				{
					int height = getWorldHeight(world, position.X + (j * 2) - (villageDim), position.Z + (i * 2) - (villageDim));
					if (height != 0)
					{
						heightMap[0][(i*villageDim) + j] = height;
						heightMap[1][(i*villageDim) + j] = Block.getIdFromBlock(world.getBlockState(new BlockPos(position.X + (j * 2) - (villageDim), height, position.Z + (i * 2) - (villageDim))).getBlock());
						flag = false;
					}
					
				}
			}
		}
		return heightMap;
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
		this.villageLocation = villageLoc;
		this.villageNum = size;
		this.loadedWorld = true;
	}
	
	public void saveData(WastelandWorldData data)
	{
		data.saveVillageData(villageLocation);
	}

}