package dev.vanilladev.wasteland.world.gen;

import java.util.Random;

import dev.vanilladev.wasteland.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRandomRubble extends WorldGenerator
{
	public WorldGenRandomRubble()
	{
		super(true);
	}
	
	public boolean generate(World world, Random random, BlockPos pos)
	{
			byte byte0 = 3;
			int l = random.nextInt(2) + 2;
			int i1 = random.nextInt(2) + 2;
			boolean flag = false;
			boolean flag1 = false;
			boolean flag2 = false;
			byte byte1 = 75;
			Material material = world.getBlockState(pos.up()).getMaterial();
			Material material1 = world.getBlockState(pos).getMaterial();
			Material material2 = world.getBlockState(pos.east()).getMaterial();
			Material material3 = world.getBlockState(pos.west()).getMaterial();
			Material material4 = world.getBlockState(pos.south()).getMaterial();
			Material material5 = world.getBlockState(pos.north()).getMaterial();
			if ((world.getBlockState(pos.down()).getBlock().equals(ModConfig.getSurfaceBlock())) && (!material.isSolid()) && (!material1.isSolid()) && (!material4.isSolid()) && (!material2.isSolid()) && (!material3.isSolid()) && (!material5.isSolid()) && (world.getBlockState(pos).getBlock() == Blocks.AIR) && (world.getBlockState(pos.up()).getBlock() == Blocks.AIR))
			{
				
				for (int j1 = 0; j1 < byte1; j1++)
				{
					int k1 = pos.getX() + random.nextInt(8);
					int l1 = pos.getY() - 1 + random.nextInt(4);
					int i2 = pos.getZ() + random.nextInt(8);
					Material material6 = world.getBlockState(new BlockPos(k1, l1 - 1, i2)).getMaterial();
					if ((world.getBlockState(new BlockPos(k1, l1, i2)).getBlock() == Blocks.AIR) && (material6.isSolid()))
					{
						Block j2 = Blocks.COBBLESTONE;
						int k2 = random.nextInt(31);
						if (k2 < 10)
							j2 = Blocks.COBBLESTONE;
						else if (k2 >= 10 && k2 < 20)
							j2 = Blocks.MOSSY_COBBLESTONE;
						else if (k2 >= 20 && k2 < 30)
							j2 = Blocks.PLANKS;
						else
							j2 = Blocks.BRICK_BLOCK;
						
						world.setBlockState(new BlockPos(k1, l1, i2), j2.getDefaultState());
					}
				}
				return true;
			}
			return false;
	}
}