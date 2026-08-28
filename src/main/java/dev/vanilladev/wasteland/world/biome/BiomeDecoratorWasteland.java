package dev.vanilladev.wasteland.world.biome;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenerator;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.ruin.RuinRuined;
import dev.vanilladev.wasteland.ruin.RuinRuinedCiv1;
import dev.vanilladev.wasteland.ruin.RuinSurvivorTent;
import dev.vanilladev.wasteland.ruin.RuinTreeHouse;
import dev.vanilladev.wasteland.ruin.RuinVillageGenerator;
import dev.vanilladev.wasteland.utils.Vector;
import dev.vanilladev.wasteland.world.gen.WorldGenRandomFire;
import dev.vanilladev.wasteland.world.gen.WorldGenRandomRubble;
import dev.vanilladev.wasteland.world.gen.WorldGenWastelandBigTree;

public class BiomeDecoratorWasteland extends BiomeDecorator
{
	public int firePerChunk;
	public int rubblePerChunk;
	public int deadTreePerChunk;
	public WorldGenerator randomFireGen;
	public WorldGenerator randomRubbleGen;
	public WorldGenerator deadTreeGen;
	public RuinTreeHouse treeHouse;
	public RuinSurvivorTent tent;
	public RuinRuined temple;
	public RuinRuinedCiv1 house;
	
	public BiomeDecoratorWasteland()
	{
		super();
		
		this.randomFireGen = new WorldGenRandomFire();
		this.randomRubbleGen = new WorldGenRandomRubble();
		this.deadTreeGen = new WorldGenWastelandBigTree(true);
		this.treeHouse = new RuinTreeHouse("treeHouse");
		this.tent = new RuinSurvivorTent("tent");
		this.temple = new RuinRuined("temple");
		this.house = new RuinRuinedCiv1("house");
		
		this.firePerChunk = ModConfig.randomFirePerChunk;
		this.rubblePerChunk = 1;
		this.deadTreePerChunk = 1;
		
		this.flowersPerChunk = -999;
		this.grassPerChunk = -999;
		this.deadBushPerChunk = 5;
		this.treesPerChunk = -999;
		this.generateFalls = false; // 1.12.2 equivalent of original generateLakes=false: no springs/lakes; ruins' wells stay
	}
	
	@Override
	protected void genDecorations(Biome biome, World world, Random random)
	{
		super.genDecorations(biome, world, random);
		
		if (biome == WastelandBiomes.apocalypse)
		{
			decorateWasteland(world, random);
		}
		else if (biome == WastelandBiomes.mountains)
		{
			decorateMountains(world, random);
		}
		else if (biome == WastelandBiomes.forest)
		{
			decorateForest(world, random);
		}
	}
	
	private void decorateWasteland(World world, Random rand)
	{
		int x, y, z;
		boolean doGen = true;
		for(int i = 0; doGen && i < firePerChunk; i++)
		{
			x = chunkPos.getX() + rand.nextInt(16) + 8;
			z = chunkPos.getZ() + rand.nextInt(16) + 8;
			this.randomFireGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
		
		doGen = rand.nextInt(ModConfig.wastelandRuinRarirty*3) == 0;
		if (doGen)
		{
			Vector pos = new Vector(chunkPos.getX() + rand.nextInt(16), 0, chunkPos.getZ() + rand.nextInt(16));
			for (int i = 0; i < RuinVillageGenerator.villageNum; i ++)
			{
				doGen = doGen && (Vector.VtoVlengthXZ(pos, RuinVillageGenerator.villageLocation.get(i)) > 48);
			}
			if(doGen)
			{
				this.house.generate(world, rand, pos.X, world.getHeight(pos.X, pos.Z)-1, pos.Z);
			}
		}
		
		doGen = rand.nextInt(ModConfig.wastelandRuinRarirty) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16) + 8;
			z = chunkPos.getZ() + rand.nextInt(16) + 8;
			this.randomRubbleGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
		
		doGen = rand.nextInt(ModConfig.wastelandTreeSpawnRate*15) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16) + 8;
			z = chunkPos.getZ() + rand.nextInt(16) + 8;
			this.deadTreeGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
	}
	
	private void decorateMountains(World world, Random rand)
	{
		int x,y,z;
		boolean doGen = rand.nextInt(ModConfig.mountainRuinRarity*2) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16);
			z = chunkPos.getZ() + rand.nextInt(16);
			this.temple.generate(world, rand, x, world.getHeight(x, z), z);
		}
		
		doGen = rand.nextInt(ModConfig.mountainRuinRarity*3) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16) + 8;
			z = chunkPos.getZ() + rand.nextInt(16) + 8;
			this.randomRubbleGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
		
		doGen = rand.nextInt(ModConfig.wastelandTreeSpawnRate*25) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16);
			z = chunkPos.getZ() + rand.nextInt(16);
			this.deadTreeGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
	}
	
	private void decorateForest(World world, Random rand)
	{
		int x, y, z;
		boolean doGen = rand.nextInt(ModConfig.forestRuinRarity) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16) + 8;
			z = chunkPos.getZ() + rand.nextInt(16) + 8;
			this.randomRubbleGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
		
		doGen = rand.nextInt(ModConfig.forestRuinRarity*3) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16);
			z = chunkPos.getZ() + rand.nextInt(16);
			this.tent.generate(world, rand, x, world.getHeight(x, z) - 1, z);
		}
		
		doGen = rand.nextInt(ModConfig.forestRuinRarity*2) == 0;
		if(doGen)
		{
			x = chunkPos.getX() + rand.nextInt(16);
			z = chunkPos.getZ() + rand.nextInt(16);
			this.treeHouse.generate(world, rand, x, world.getHeight(x, z) - 1, z);
		}
		
		doGen = rand.nextInt(ModConfig.wastelandTreeSpawnRate) == 0;
		int treesPerChunk = 1;
		for(int i = 0; doGen && i < treesPerChunk; i++)
		{
			x = chunkPos.getX() + rand.nextInt(16);
			z = chunkPos.getZ() + rand.nextInt(16);
			this.deadTreeGen.generate(world, rand, new BlockPos(x, world.getHeight(x, z), z));
		}
	}
}