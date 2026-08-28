package dev.vanilladev.wasteland.ruin;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import dev.vanilladev.wasteland.ModConfig;
import dev.vanilladev.wasteland.ruin.Ruin.LootStack;
import dev.vanilladev.wasteland.utils.CustomItemStack;

public class RuinTreeHouse extends Ruin implements IWorldGenerator
{
	private RuinGenHelper genHelper = new RuinGenHelper();
	
	public RuinTreeHouse(String par1Name)
	{
		super(par1Name);
	}
	
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		this.genHelper.setWorld(world);
		
		if(world.getBlockState(new BlockPos(x, y, z)).getBlock().equals(ModConfig.getSurfaceBlock()))
		{
			/*	  	  A X+
			 *		  |
			 *  Z- <-- --> Z+
			 *	  	  |
			 *	  	  V X-
			 */
			
			int xCoord = x;
			int yCoord = world.getHeight(x, z) - 1;
			int zCoord = z;
			
			// Layer 1 generation
			
			int var1 = world.getHeight(xCoord - 1, zCoord - 1);
			genHelper.setBlock(xCoord - 1, var1, zCoord - 1, Blocks.PLANKS);
			
			// Layer 2 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			
			if(!world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getMaterial().isSolid())
				genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.LADDER, 2);
			
			// Layer 3 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			
			// Layer 4 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			
			// Leyer 5 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord, Blocks.LOG);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.LOG);
			genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.LADDER, 2);
			
			// Layer 6 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.LOG);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.LOG);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 1, Blocks.LADDER, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.LOG);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.LOG);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, Blocks.LOG);
			
			// Layer 7 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, Blocks.LOG);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.CHEST);
			
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(xCoord - 1, yCoord, zCoord + 1));
			LootStack loot = this.setItems(random);
			CustomItemStack.placeLoot(random, chest, CustomItemStack.getLootItems(random, loot.items, loot.minNum, loot.maxNum, loot.repeat));
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.LOG);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.LOG);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.LOG);
			
			// Layer 8 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord, yCoord, zCoord, Blocks.LOG);
			genHelper.setBlock(xCoord, yCoord, zCoord + 1, Blocks.LOG, 8);
			
			return true;
		}
		return false;
	}
}
