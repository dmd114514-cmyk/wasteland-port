package dev.vanilladev.wasteland.world;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.ChunkGeneratorSettings.Factory;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld;
import net.minecraftforge.event.terraingen.TerrainGen;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.city.RoadGenerator;

public class ChunkProviderWasteland implements IChunkGenerator
{
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	private Random rand;
	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorPerlin noiseGen4;
	public NoiseGeneratorOctaves noiseGen5;
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;
	private World worldObj;
	private final boolean mapFeaturesEnabled;
	private WorldType terrainType;
	private final double[] heightMap;
	private final float[] biomeWeights;
	private ChunkGeneratorSettings settings;
	private double[] depthBuffer = new double[256];
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenStronghold strongholdGenerator = new MapGenStronghold();
	private MapGenVillage villageGenerator = new MapGenVillage();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private Biome[] biomesForGeneration;
	double[] mainNoiseRegion;
	double[] minLimitRegion;
	double[] maxLimitRegion;
	double[] depthRegion;
	
	{
		this.caveGenerator = TerrainGen.getModdedMapGen(this.caveGenerator, EventType.CAVE);
		this.strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(this.strongholdGenerator, EventType.STRONGHOLD);
		this.villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(this.villageGenerator, EventType.VILLAGE);
		this.mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(this.mineshaftGenerator, EventType.MINESHAFT);
		this.ravineGenerator = TerrainGen.getModdedMapGen(this.ravineGenerator, EventType.RAVINE);
	}
	
	public ChunkProviderWasteland(World par1World, long par2, boolean par4, String par5)
	{
		this.worldObj = par1World;
		this.mapFeaturesEnabled = par4;
		this.terrainType = par1World.getWorldInfo().getTerrainType();
		this.rand = new Random(par2);
		this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseGen4 = new NoiseGeneratorPerlin(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];
		
		for (int i = -2; i <= 2; ++i)
		{
			for (int j = -2; j <= 2; ++j)
			{
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				this.biomeWeights[i + 2 + (j + 2) * 5] = f;
			}
		}
		
		if (par5 != null)
		{
			this.settings = Factory.jsonToFactory(par5).build();
			par1World.setSeaLevel(this.settings.seaLevel);
		}
		
		ContextOverworld context = new ContextOverworld(this.noiseGen1, this.noiseGen2, this.noiseGen3, this.noiseGen4, this.noiseGen5, this.noiseGen6, this.mobSpawnerNoise);
		context = TerrainGen.getModdedNoiseGenerators(par1World, this.rand, context);
		this.noiseGen1 = context.getLPerlin1();
		this.noiseGen2 = context.getLPerlin2();
		this.noiseGen3 = context.getPerlin();
		this.noiseGen4 = context.getHeight();
		this.noiseGen5 = context.getScale();
		this.noiseGen6 = context.getDepth();
		this.mobSpawnerNoise = context.getForest();
	}
	
	public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
	{
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
		this.generateHeightmap(x * 4, 0, z * 4);
		
		for (int i = 0; i < 4; ++i)
		{
			int j = i * 5;
			int k = (i + 1) * 5;
			
			for (int l = 0; l < 4; ++l)
			{
				int i1 = (j + l) * 33;
				int j1 = (j + l + 1) * 33;
				int k1 = (k + l) * 33;
				int l1 = (k + l + 1) * 33;
				
				for (int i2 = 0; i2 < 32; ++i2)
				{
					double d1 = this.heightMap[i1 + i2];
					double d2 = this.heightMap[j1 + i2];
					double d3 = this.heightMap[k1 + i2];
					double d4 = this.heightMap[l1 + i2];
					double d5 = (this.heightMap[i1 + i2 + 1] - d1) * 0.125D;
					double d6 = (this.heightMap[j1 + i2 + 1] - d2) * 0.125D;
					double d7 = (this.heightMap[k1 + i2 + 1] - d3) * 0.125D;
					double d8 = (this.heightMap[l1 + i2 + 1] - d4) * 0.125D;
					
					for (int j2 = 0; j2 < 8; ++j2)
					{
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * 0.25D;
						double d13 = (d4 - d2) * 0.25D;
						
						for (int k2 = 0; k2 < 4; ++k2)
						{
							double d16 = (d11 - d10) * 0.25D;
							double d = d10 - d16;
							
							for (int l2 = 0; l2 < 4; ++l2)
							{
								if ((d += d16) > 0.0D)
								{
									primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2, STONE);
								}
							}
							
							d10 += d12;
							d11 += d13;
						}
						
						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}
	
	public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn)
	{
		if (!ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.worldObj))
		{
			return;
		}
		
		double d0 = 0.03125D;
		this.depthBuffer = this.noiseGen4.getRegion(this.depthBuffer, (double)(x * 16), (double)(z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
		
		for (int i = 0; i < 16; ++i)
		{
			for (int j = 0; j < 16; ++j)
			{
				Biome biome = biomesIn[j + i * 16];
				biome.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.depthBuffer[j + i * 16]);
			}
		}
	}
	
	public Chunk generateChunk(int x, int z)
	{
		this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(x, z, chunkprimer);
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
		
		for (int i = 0; i < this.biomesForGeneration.length; ++i)
		{
			if (this.biomesForGeneration[i] == Biomes.OCEAN || this.biomesForGeneration[i] == Biomes.RIVER)
			{
				this.biomesForGeneration[i] = WastelandBiomes.apocalypse;
			}
		}
		
		this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);
		
		if (this.settings.useCaves)
		{
			this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);
		}
		
		if (this.mapFeaturesEnabled)
		{
			if (this.settings.useMineShafts)
			{
				this.mineshaftGenerator.generate(this.worldObj, x, z, chunkprimer);
			}
			
			if (this.settings.useVillages)
			{
				this.villageGenerator.generate(this.worldObj, x, z, chunkprimer);
			}
			
			if (this.settings.useStrongholds)
			{
				this.strongholdGenerator.generate(this.worldObj, x, z, chunkprimer);
			}
			
			if (this.settings.useRavines)
			{
				this.ravineGenerator.generate(this.worldObj, x, z, chunkprimer);
			}
		}
		
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		byte[] abyte = chunk.getBiomeArray();
		
		for (int i = 0; i < abyte.length; ++i)
		{
			abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
		}
		
		chunk.generateSkylightMap();
		return chunk;
	}
	
	private void generateHeightmap(int par1, int par2, int par3)
	{
		this.depthRegion = this.noiseGen6.generateNoiseOctaves(this.depthRegion, par1, par3, 5, 5, (double)this.settings.depthNoiseScaleX, (double)this.settings.depthNoiseScaleZ, (double)this.settings.depthNoiseScaleExponent);
		float f = this.settings.coordinateScale;
		float f1 = this.settings.heightScale;
		this.mainNoiseRegion = this.noiseGen3.generateNoiseOctaves(this.mainNoiseRegion, par1, par2, par3, 5, 33, 5, (double)(f / this.settings.mainNoiseScaleX), (double)(f1 / this.settings.mainNoiseScaleY), (double)(f / this.settings.mainNoiseScaleZ));
		this.minLimitRegion = this.noiseGen1.generateNoiseOctaves(this.minLimitRegion, par1, par2, par3, 5, 33, 5, (double)f, (double)f1, (double)f);
		this.maxLimitRegion = this.noiseGen2.generateNoiseOctaves(this.maxLimitRegion, par1, par2, par3, 5, 33, 5, (double)f, (double)f1, (double)f);
		int i = 0;
		int j = 0;
		
		for (int k = 0; k < 5; ++k)
		{
			for (int l = 0; l < 5; ++l)
			{
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;
				Biome biome = this.biomesForGeneration[k + 2 + (l + 2) * 10];
				
				for (int k1 = -2; k1 <= 2; ++k1)
				{
					for (int l1 = -2; l1 <= 2; ++l1)
					{
						Biome biome1 = this.biomesForGeneration[k + k1 + 2 + (l + l1 + 2) * 10];
						float f5 = this.settings.biomeDepthOffSet + biome1.getBaseHeight() * this.settings.biomeDepthWeight;
						float f6 = this.settings.biomeScaleOffset + biome1.getHeightVariation() * this.settings.biomeScaleWeight;
						
						if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F)
						{
							f5 = 1.0F + f5 * 2.0F;
							f6 = 1.0F + f6 * 4.0F;
						}
						
						float f7 = this.biomeWeights[k1 + 2 + (l1 + 2) * 5] / (f5 + 2.0F);
						
						if (biome1.getBaseHeight() > biome.getBaseHeight())
						{
							f7 /= 2.0F;
						}
						
						f2 += f6 * f7;
						f3 += f5 * f7;
						f4 += f7;
					}
				}
				
				f2 /= f4;
				f3 /= f4;
				f2 = f2 * 0.9F + 0.1F;
				f3 = (f3 * 4.0F - 1.0F) / 8.0F;
				double d0 = this.depthRegion[j] / 8000.0D;
				
				if (d0 < 0.0D)
				{
					d0 = -d0 * 0.3D;
				}
				
				d0 = d0 * 3.0D - 2.0D;
				
				if (d0 < 0.0D)
				{
					d0 /= 2.0D;
					
					if (d0 < -1.0D)
					{
						d0 = -1.0D;
					}
					
					d0 /= 1.4D;
					d0 /= 2.0D;
				}
				else
				{
					if (d0 > 1.0D)
					{
						d0 = 1.0D;
					}
					
					d0 /= 8.0D;
				}
				
				++j;
				double d1 = (double)f3;
				double d2 = (double)f2;
				d1 += d0 * 0.2D;
				d1 = d1 * (double)this.settings.baseSize / 8.0D;
				double d3 = (double)this.settings.baseSize + d1 * 4.0D;
				
				for (int i1 = 0; i1 < 33; ++i1)
				{
					double d4 = ((double)i1 - d3) * (double)this.settings.stretchY * 128.0D / 256.0D / d2;
					
					if (d4 < 0.0D)
					{
						d4 *= 4.0D;
					}
					
					double d5 = this.minLimitRegion[i] / (double)this.settings.lowerLimitScale;
					double d6 = this.maxLimitRegion[i] / (double)this.settings.upperLimitScale;
					double d7 = (this.mainNoiseRegion[i] / 10.0D + 1.0D) / 2.0D;
					double d8 = MathHelper.clamp(d5, d6, d7) - d4;
					
					if (i1 > 29)
					{
						double d9 = (double)((float)(i1 - 29) / 3.0F);
						d8 = d8 * (1.0D - d9) + -10.0D * d9;
					}
					
					this.heightMap[i] = d8;
					++i;
				}
			}
		}
	}
	
	public void populate(int x, int z)
	{
		BlockFalling.fallInstantly = true;
		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long k = this.rand.nextLong() / 2L * 2L + 1L;
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)x * k + (long)z * l ^ this.worldObj.getSeed());
		// world main highway: pave the chunk's part of the road, then the rest
		RoadGenerator.paveChunk(this.worldObj, this.rand, x, z);
		boolean flag = false;
		ChunkPos chunkpos = new ChunkPos(x, z);
		ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, this.rand, x, z, flag);
		
		if (this.mapFeaturesEnabled)
		{
			if (this.settings.useMineShafts)
			{
				this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}
			
			if (this.settings.useVillages)
			{
				flag = this.villageGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}
			
			if (this.settings.useStrongholds)
			{
				this.strongholdGenerator.generateStructure(this.worldObj, this.rand, chunkpos);
			}
		}
		
		if (this.settings.useDungeons && TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON))
		{
			for (int i1 = 0; i1 < this.settings.dungeonChance; ++i1)
			{
				int j1 = this.rand.nextInt(16) + 8;
				int k1 = this.rand.nextInt(256);
				int l1 = this.rand.nextInt(16) + 8;
				(new WorldGenDungeons()).generate(this.worldObj, this.rand, blockpos.add(j1, k1, l1));
			}
		}
		
		biome.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
		
		if (TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
		{
			WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i + 8, j + 8, 16, 16, this.rand);
		}
		
		blockpos = blockpos.add(8, 0, 8);
		ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, this.rand, x, z, flag);
		BlockFalling.fallInstantly = false;
	}
	
	public boolean generateStructures(Chunk chunkIn, int x, int z)
	{
		return this.mapFeaturesEnabled;
	}
	
	public List getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		Biome biome = this.worldObj.getBiome(pos);
		return biome.getSpawnableList(creatureType);
	}
	
	@Nullable
	@Override
	public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored)
	{
		return null;
	}
	
	public void recreateStructures(Chunk chunkIn, int x, int z)
	{
		if (this.mapFeaturesEnabled)
		{
			if (this.settings.useMineShafts)
			{
				this.mineshaftGenerator.generate(this.worldObj, x, z, null);
			}
			
			if (this.settings.useVillages)
			{
				this.villageGenerator.generate(this.worldObj, x, z, null);
			}
			
			if (this.settings.useStrongholds)
			{
				this.strongholdGenerator.generate(this.worldObj, x, z, null);
			}
		}
	}
	
	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos)
	{
		return false;
	}
}