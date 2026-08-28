package dev.vanilladev.wasteland.world.biome;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.ModHelper;
import dev.vanilladev.wasteland.WastelandBiomes;
import dev.vanilladev.wasteland.world.WorldChunkManagerWasteland;

public class BiomeGenWastelandBase extends Biome
{
	public static final BiomeProperties height_Wasteland = new BiomeProperties("Wasteland").setBaseHeight(0.1F).setHeightVariation(0.05F).setWaterColor(0x338533);
	public static final BiomeProperties height_WastelandCity = new BiomeProperties("Wasteland City").setBaseHeight(0.09F).setHeightVariation(0.01F).setWaterColor(0x338533);
	public static final BiomeProperties height_WastelandMountains = new BiomeProperties("Wasteland Mountains").setBaseHeight(1.0F).setHeightVariation(0.5F).setWaterColor(0x338533);
	
	private static int lastID = 0;
	//public static Biome[] wastelandBiomes = new Biome[16];
	
	public BiomeGenWastelandBase(String par2Name, BiomeProperties biomeHeight)
	{
		super(biomeHeight);
		
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.decorator = new BiomeDecoratorWasteland();
		
		lastID++;
		
		this.loadBiome();
	}
	
	public static void load()
	{
		MinecraftForge.TERRAIN_GEN_BUS.register(new WastelandLakeBlocker());
		
		Biome apocalypse = new BiomeGenApocalypse("Wasteland", height_Wasteland);
		Biome apocMountains = new BiomeGenMountains("Wasteland Mountains", height_WastelandMountains);
		Biome apocForest = new BiomeGenForest("Wasteland Forest", height_Wasteland);
		Biome apocCity = new BiomeGenForest("Wasteland City", height_WastelandCity);
		
		WastelandBiomes.setBiomeColor(apocalypse, 0x00E0BD69);
		WastelandBiomes.setBiomeColor(apocMountains, 0x009C7C13);
		WastelandBiomes.setBiomeColor(apocForest, 0x00A4B34F);
		WastelandBiomes.setBiomeColor(apocCity, 0x008F98B3);
		
		apocalypse.setRegistryName(new ResourceLocation(ModHelper.ModInfo.modid, "wasteland"));
		apocMountains.setRegistryName(new ResourceLocation(ModHelper.ModInfo.modid, "wasteland_mountains"));
		apocForest.setRegistryName(new ResourceLocation(ModHelper.ModInfo.modid, "wasteland_forest"));
		apocCity.setRegistryName(new ResourceLocation(ModHelper.ModInfo.modid, "wasteland_city"));
		
		ForgeRegistries.BIOMES.registerAll(apocalypse, apocMountains, apocForest, apocCity);
		
		BiomeDictionary.addTypes(apocalypse, Type.WASTELAND);
		BiomeDictionary.addTypes(apocMountains, Type.WASTELAND, Type.MOUNTAIN);
		BiomeDictionary.addTypes(apocForest, Type.WASTELAND, Type.FOREST);
		BiomeDictionary.addTypes(apocCity, Type.WASTELAND, Type.DEAD);
		
		BiomeManager.addSpawnBiome(apocalypse);
		BiomeManager.addSpawnBiome(apocMountains);
		BiomeManager.addSpawnBiome(apocForest);
		BiomeManager.addSpawnBiome(apocCity);
		
		WastelandBiomes.apocalypse = apocalypse;
		WastelandBiomes.mountains = apocMountains;
		WastelandBiomes.forest = apocForest;
		WastelandBiomes.city = apocCity;
		
		WastelandBiomes.spawnInBiomes.add(apocalypse);
		WastelandBiomes.spawnInBiomes.add(apocMountains);
		WastelandBiomes.spawnInBiomes.add(apocForest);
	}
	
	public static class WastelandLakeBlocker
	{
		@SubscribeEvent
		public void onDecorate(DecorateBiomeEvent.Decorate event)
		{
			if ((event.getType() == DecorateBiomeEvent.Decorate.EventType.LAKE_WATER || event.getType() == DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)
				&& event.getWorld().getBiomeProvider().getClass().getName().equals(WorldChunkManagerWasteland.class.getName()))
			{
				event.setResult(Event.Result.DENY);
			}
		}
	}
	
	public BiomeGenWastelandBase setTopBlock(Block block)
	{
		this.topBlock = block.getDefaultState();
		return this;
	}
	
	public BiomeGenWastelandBase setFillerBlock(Block block)
	{
		this.fillerBlock = block.getDefaultState();
		return this;
	}
	
	public void loadBiome()
	{
		this.decorator.deadBushPerChunk = 5;
		this.decorator.flowersPerChunk = -999;
		this.decorator.grassPerChunk = -999;
		this.decorator.treesPerChunk = -999;
		
		this.setTopBlock(ModConfig.getSurfaceBlock());
		this.setFillerBlock(Blocks.STONE);
	}
}