package dev.vanilladev.wasteland;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class GameRegisterer
{
	public static void registerBiome(Biome biome, Type... types)
		{ BiomeDictionary.addTypes(biome, types); }
}