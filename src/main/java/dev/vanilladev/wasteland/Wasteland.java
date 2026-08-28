package dev.vanilladev.wasteland;

import java.io.File;

import net.minecraft.world.WorldType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import dev.vanilladev.wasteland.city.CityGenerator;
import dev.vanilladev.wasteland.ruin.RuinVillageGenerator;
import dev.vanilladev.wasteland.world.WastelandWorldData;
import dev.vanilladev.wasteland.world.WorldTypeWasteland;
import dev.vanilladev.wasteland.world.biome.BiomeGenWastelandBase;

@Mod(modid=ModHelper.ModInfo.modid, name=ModHelper.ModInfo.name, version=ModHelper.ModInfo.version, useMetadata = true)
public class Wasteland
{
	public static WorldType wastelandWorldType;
	public static RuinVillageGenerator villageGenerator;
	public static CityGenerator cityGenerator;
	
	@Instance(value = ModHelper.ModInfo.modid)
	public static Wasteland instance;
	
	@SidedProxy(clientSide = "dev.vanilladev.wasteland.ClientProxy", serverSide = "dev.vanilladev.wasteland.CommonProxy")
	public static CommonProxy proxy;
	
	public static WastelandEventHandler eventHandler = new WastelandEventHandler();
	public static WastelandWorldData worldData = new WastelandWorldData();
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(eventHandler);
		
		Configuration config = new Configuration(new File("config/Wasteland/TerrainGen.cfg"));
		Configuration ruinConfig = new Configuration(new File("config/Wasteland/ChestLoot.cfg"));
		
		ModConfig.load(config);
		RuinConfig.load(ruinConfig);
		
		proxy.preInit();
		
		BiomeGenWastelandBase.load();
		
		villageGenerator = new RuinVillageGenerator();
		cityGenerator = new CityGenerator();
		eventHandler.initialize(villageGenerator, cityGenerator, worldData);
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		wastelandWorldType = new WorldTypeWasteland("wasteland");
		WorldTypeWasteland.genInfo.createDefault();
		
		proxy.init();
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
	}
	
	@EventHandler
	public static void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new GetBiomesCommand());
	}
}