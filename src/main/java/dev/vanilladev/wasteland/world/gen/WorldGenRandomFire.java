package dev.vanilladev.wasteland.world.gen;

import java.util.Random;

import dev.vanilladev.wasteland.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRandomFire extends WorldGenerator
{
	public WorldGenRandomFire()
	{
		super(true);
	}
	
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		if(world.getBlockState(pos).getBlock().equals(ModConfig.getSurfaceBlock()))
		{
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
			world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
			
			return true;
		}
		
		else return false;
	}
}