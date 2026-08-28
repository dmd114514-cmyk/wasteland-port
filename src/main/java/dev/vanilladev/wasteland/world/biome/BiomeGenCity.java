package dev.vanilladev.wasteland.world.biome;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.ChunkPrimer;
import dev.vanilladev.wasteland.ModConfig;
import net.minecraft.entity.monster.EntityZombie;

public class BiomeGenCity extends BiomeGenWastelandBase
{
	public BiomeGenCity(String par2Name, BiomeProperties par3BiomeHeight)
	{
		super(par2Name, par3BiomeHeight);
		if (ModConfig.dayZombies)
		{
			this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityZombie.class, 100, 0, 2));
		}
	}

	// the city biome is one big flat plain: stone column to y=61, filler at
	// 62, grass surface at 63 - no hills, no dry riverbeds, no floating lots
	@Override
	public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer primer, int x, int z, double noiseVal)
	{
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int y = 0; y < 62; y++)
					primer.setBlockState(i, y, j, Blocks.STONE.getDefaultState());
				primer.setBlockState(i, 62, j, this.fillerBlock);
				primer.setBlockState(i, 63, j, this.topBlock);
			}
		}
	}
}