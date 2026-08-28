package dev.vanilladev.wasteland.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dev.vanilladev.wasteland.client.GuiCreateWastelandWorld;
import dev.vanilladev.wasteland.world.gen.WastelandGeneratorInfo;

public class WorldTypeWasteland extends WorldType
{
	public static WastelandGeneratorInfo genInfo = new WastelandGeneratorInfo();
	
	public WorldTypeWasteland(String name)
	{
		super(name);
	}
	
	@Override
	public BiomeProvider getBiomeProvider(World world)
		{ return new WorldChunkManagerWasteland(world); }
	
	@Override
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
		{ return new ChunkProviderWasteland(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions); }
	
	@Override
	public boolean isCustomizable()
	{
		// customize page removed: Wasteland options live in the config file
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void onCustomizeButton(Minecraft instance, GuiCreateWorld guiCreateWorld)
	{
		instance.displayGuiScreen(new GuiCreateWastelandWorld(guiCreateWorld, guiCreateWorld.chunkProviderSettingsJson, genInfo));
	}
	
	@Override
	public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer, ChunkGeneratorSettings chunkSettings)
	{
		GenLayer ret = new WastelandGenLayerBiome(200L, parentLayer, this);
		ret = GenLayerZoom.magnify(1000L, ret, 2);
		ret = new GenLayerBiomeEdge(1000L, ret);
		return ret;
	}
}