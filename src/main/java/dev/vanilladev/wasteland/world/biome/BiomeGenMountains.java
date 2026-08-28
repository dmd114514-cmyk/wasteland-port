package dev.vanilladev.wasteland.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import dev.vanilladev.wasteland.ModConfig;
import net.minecraft.entity.monster.EntityZombie;

public class BiomeGenMountains extends BiomeGenWastelandBase
{
	public BiomeGenMountains(String par2Name, BiomeProperties par3BiomeHeight)
	{
		super(par2Name, par3BiomeHeight);
		
		//this.theBiomeDecorator = new BiomeDecoratorWasteland();
		if (ModConfig.dayZombies)
		{
			this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombie.class, 10, 0, 2)); // weight, maxG, minG
		}
	}
}