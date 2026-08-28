package dev.vanilladev.wasteland.ruin;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;

public class RuinLightposts extends Ruin implements IWorldGenerator
{
	private RuinGenHelper genHelper = new RuinGenHelper();
	
	public RuinLightposts(String par1Name)
	{
		super(par1Name);
	}
	
	@Override
	protected boolean generate(World world, Random random, int xCoord, int yCoord, int zCoord)
	{
		this.genHelper.setWorld(world);
		
		Material material0 = world.getBlockState(new BlockPos(xCoord, yCoord + 1, zCoord)).getMaterial();
		Material material1 = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)).getMaterial();
		Material material2 = world.getBlockState(new BlockPos(xCoord + 1, yCoord, zCoord)).getMaterial();
		Material material3 = world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getMaterial();
		Material material4 = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord + 1)).getMaterial();
		Material material5 = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getMaterial();
		
		if ((world.getBlockState(new BlockPos(xCoord, yCoord - 1, zCoord)).getBlock() == Blocks.DIRT) && (!material0.isSolid()) && (!material1.isSolid()) && (!material4.isSolid()) && (!material2.isSolid()) && (!material3.isSolid()) && (!material5.isSolid()))
		{
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord, yCoord + 1, zCoord, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord, yCoord + 2, zCoord, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord, yCoord + 3, zCoord, Blocks.OAK_FENCE);
			
			int direction = random.nextInt(4);
			if (direction == 0)
			{
				genHelper.setBlock(xCoord - 1, yCoord + 3, zCoord, Blocks.OAK_FENCE);
				genHelper.setBlock(xCoord - 1, yCoord + 2, zCoord, Blocks.GLOWSTONE);
			}
			if (direction == 1)
			{
				genHelper.setBlock(xCoord + 1, yCoord + 3, zCoord, Blocks.OAK_FENCE);
				genHelper.setBlock(xCoord + 1, yCoord + 2, zCoord, Blocks.GLOWSTONE);
			}
			if (direction == 2)
			{
				genHelper.setBlock(xCoord, yCoord + 3, zCoord - 1, Blocks.OAK_FENCE);
				genHelper.setBlock(xCoord, yCoord + 2, zCoord - 1, Blocks.GLOWSTONE);
			}
			if (direction == 3)
			{
				genHelper.setBlock(xCoord, yCoord + 3, zCoord + 1, Blocks.OAK_FENCE);
				genHelper.setBlock(xCoord, yCoord + 2, zCoord + 1, Blocks.GLOWSTONE);
			}
			return true;
		}
		return false;
	}
}