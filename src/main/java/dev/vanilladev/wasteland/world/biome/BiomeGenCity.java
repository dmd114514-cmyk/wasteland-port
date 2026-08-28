package dev.vanilladev.wasteland.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.entity.EntityDayZombie;

public class BiomeGenCity extends BiomeGenWastelandBase
{
	public BiomeGenCity(String par2Name, BiomeProperties par3BiomeHeight)
	{
		super(par2Name, par3BiomeHeight);
		if (ModConfig.dayZombies)
		{
			this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityDayZombie.class, 100, 0, 2));
		}
	}
}