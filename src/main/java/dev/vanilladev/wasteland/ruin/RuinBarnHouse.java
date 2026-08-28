package dev.vanilladev.wasteland.ruin;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import dev.vanilladev.wasteland.ModHelper;
import dev.vanilladev.wasteland.utils.CustomItemStack;

public class RuinBarnHouse extends Ruin implements IWorldGenerator
{
	private RuinGenHelper genHelper = new RuinGenHelper();
	
	public RuinBarnHouse(String name)
	{
		super(name);
	}
	
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		genHelper.setWorld(world);
		
		/*	  	  A X+
		 *		  |
		 *  Z- <-- --> Z+
		 *	  	  |
		 *	  	  V X-
		 */
		
		int xCoord = x;
		int yCoord = y;
		int zCoord = z;
		
		Material material1 = world.getBlockState(new BlockPos(xCoord, yCoord, zCoord)).getMaterial();
		
		int[] basePos = new int[] { x, y, z };
		
		if(material1.isSolid() && world.getHeight(xCoord + 1, zCoord + 1) == yCoord && world.getHeight(xCoord, zCoord + 1) == yCoord && world.getHeight(xCoord - 1, zCoord + 1) == yCoord && world.getHeight(xCoord - 1, zCoord) == yCoord && world.getHeight(xCoord - 1, zCoord - 1) == yCoord && world.getHeight(xCoord, zCoord - 1) == yCoord && world.getHeight(xCoord + 1, zCoord - 1) == yCoord)	// Upper Left Corner
		{
			this.genHelper.setWorld(world);
			
			xCoord -= 6;
			zCoord -= 4;
			
			RuinGenHelper.setCube(xCoord, yCoord + 1, zCoord - 1, 11, 9, 14, Blocks.AIR);
			
			// Layer 1 Generation
			
			yCoord--;
			
			for(int i = 0; i < 9; i++)
				{ world.setBlockState(new BlockPos(xCoord, yCoord, zCoord + i), Blocks.COBBLESTONE.getDefaultState()); }
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 2, Blocks.DIRT);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 2, Blocks.DIRT);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 4, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 2, Blocks.DIRT);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 7, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 2, Blocks.DIRT);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 2, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 4, Blocks.DIRT);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 1, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 2, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 3, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 5, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 6, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 7, Blocks.DIRT);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			for(int i = 0; i < 9; i++)
				{ genHelper.setBlock(xCoord + 12, yCoord, zCoord, Blocks.COBBLESTONE); }
			
			// Layer 2 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 1, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 2, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 6, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 8, Blocks.AIR);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 0, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 3, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 6, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 4, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 0, Blocks.OAK_FENCE);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 6, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 4, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 5, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 7, Blocks.AIR);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 8, Blocks.AIR);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 8, Blocks.LOG);
			
			// Layer 3 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 3, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 8, Blocks.LOG);
			
			// Layer 4 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 6, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 8, Blocks.LOG);
			
			 // Layer 5 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.OAK_STAIRS, 5);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 6, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 7, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 6, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 7, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.OAK_STAIRS, 4);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 7, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 7, Blocks.PLANKS);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.OAK_STAIRS, 5);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.LOG);
			
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +10, yCoord, zCoord + 5, Blocks.PLANKS);

			genHelper.setBlock(xCoord +11, yCoord, zCoord + 5, Blocks.PLANKS);
			genHelper.setBlock(xCoord +11, yCoord, zCoord + 6, Blocks.PLANKS);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 7, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 8, Blocks.LOG);
			
			// Layer 6 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 7, Blocks.CHEST);
			
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(xCoord + 1, yCoord, zCoord + 7));
			LootStack loot = this.setItems(random);
			CustomItemStack.placeLoot(random, chest, CustomItemStack.getLootItems(random, loot.items, loot.minNum, loot.maxNum, loot.repeat));
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 8, Blocks.LOG);
			genHelper.setBlock(xCoord + 9, yCoord, zCoord + 9, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 4, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.LOG);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 7, Blocks.LOG);
			
			// Layer 7 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 7, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 6, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 7, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 8, yCoord, zCoord + 8, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 6, Blocks.COBBLESTONE);
			
			// Layer 8 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 7, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			
			// Layer 9 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord +12, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			
			return true;
		}
		return false;
	}
	
	private ItemStack getChestLoot(Random rand, ItemStack[] items)
	{
		int i = rand.nextInt(items.length);
		ItemStack item = new ItemStack(items[i].getItem(), 1);
		
		if(item.getItem() == Items.WHEAT) return new ItemStack(item.getItem(), rand.nextInt(8) + 4);
		else if(item.getItem() == Items.APPLE) return new ItemStack(item.getItem(), rand.nextInt(2) + 1);
		else if(item.getItem() == Items.BREAD) return new ItemStack(item.getItem(), rand.nextInt(2) + 2);
		else return new ItemStack(item.getItem(), 1);
	}
}
