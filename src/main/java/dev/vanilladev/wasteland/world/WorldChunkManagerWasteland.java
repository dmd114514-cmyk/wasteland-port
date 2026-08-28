package dev.vanilladev.wasteland.world;

import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import dev.vanilladev.wasteland.WastelandBiomes;

public class WorldChunkManagerWasteland extends BiomeProvider
{
	public WorldChunkManagerWasteland(World par1World)
	{
		super(par1World.getWorldInfo());
	}
	
	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return WastelandBiomes.spawnInBiomes;
	}
}