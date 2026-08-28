package dev.vanilladev.wasteland.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import dev.vanilladev.wasteland.ModConfig;
import net.minecraft.entity.monster.EntityZombie;

public class BiomeGenApocalypse extends BiomeGenWastelandBase
{
	public BiomeGenApocalypse(String par2Name, BiomeProperties par3BiomeHeight)
	{
		super(par2Name, par3BiomeHeight);
		if (ModConfig.dayZombies)
		{
			this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombie.class, 100, 0, 2));
		}
	}
}