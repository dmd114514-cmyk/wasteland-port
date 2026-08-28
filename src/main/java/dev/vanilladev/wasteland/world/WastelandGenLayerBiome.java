package dev.vanilladev.wasteland.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.WastelandBiomes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class WastelandGenLayerBiome extends GenLayer
{
    private List<BiomeEntry> biomes = new ArrayList<BiomeEntry>();

    public WastelandGenLayerBiome(long p_i2122_1_, GenLayer p_i2122_3_, WorldType p_i2122_4_)
    {
        super(p_i2122_1_);
        
        this.parent = p_i2122_3_;

        if (ModConfig.spawnCities)
        {
	        for (int i = 0; i < 1; i++)
	        {
	        	 biomes.add(new BiomeEntry(WastelandBiomes.city, ModConfig.cityWeight));
	        }
        }
        
        for (int i = 0; i < 10; i++)
        {
	        biomes.add(new BiomeEntry(WastelandBiomes.forest, 10));
	        biomes.add(new BiomeEntry(WastelandBiomes.mountains, 10));
        }
        
        for (int i = 0; i < 40; i++)
        {
	        biomes.add(new BiomeEntry(WastelandBiomes.apocalypse, 10));
        }
    }
    
    public int[] getInts(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_)
    {
        int[] aint = this.parent.getInts(p_75904_1_, p_75904_2_, p_75904_3_, p_75904_4_);
        int[] aint1 = IntCache.getIntCache(p_75904_3_ * p_75904_4_);

        for (int i1 = 0; i1 < p_75904_4_; ++i1)
        {
            for (int j1 = 0; j1 < p_75904_3_; ++j1)
            {
                this.initChunkSeed((long)(j1 + p_75904_1_), (long)(i1 + p_75904_2_));
                int k1 = aint[j1 + i1 * p_75904_3_];
                int l1 = (k1 & 3840) >> 8;
                k1 &= -3841;

                aint1[j1 + i1 * p_75904_3_] = Biome.getIdForBiome(((BiomeEntry)WeightedRandom.getRandomItem(this.biomes, (int)(this.nextLong(WeightedRandom.getTotalWeight(this.biomes) / 10) * 10))).biome);
            }
        }

        return aint1;
    }
    
    private static int pickBiome(List<BiomeEntry> biomes)
    {
    	Random rand = new Random();
    	int totalWeight = 0;
    	for (int i = 0; i < biomes.size(); i++)
    	{
    		totalWeight = totalWeight + biomes.get(i).itemWeight;
    	}
    	int num = rand.nextInt(totalWeight);
    	int sum = 0;
    	for (int i = 0; i < biomes.size(); i++)
    	{
    		sum = sum + biomes.get(1).itemWeight;
    		if (sum > num)
    		{
    			return Biome.getIdForBiome(biomes.get(i).biome);
    		}
    	}
    	return pickBiome(biomes);
    }
}