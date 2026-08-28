package dev.vanilladev.wasteland;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.biome.Biome;

public class WastelandBiomes
{
	public static Biome apocalypse;
	public static Biome mountains;
	public static Biome forest;
	public static Biome city;
	
	public static List<Biome> spawnInBiomes = new ArrayList<Biome>();
	
	private static Map<Biome, Integer> biomeColors = new HashMap<Biome, Integer>();
	
	public static void setBiomeColor(Biome biome, int color)
	{
		if (biome != null)
		{
			biomeColors.put(biome, color);
		}
	}
	
	public static int getBiomeColor(Biome biome)
	{
		Integer color = biomeColors.get(biome);
		return color == null ? 0 : color;
	}
}