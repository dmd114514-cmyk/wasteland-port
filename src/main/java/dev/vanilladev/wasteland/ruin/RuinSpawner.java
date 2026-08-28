package dev.vanilladev.wasteland.ruin;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.IWorldGenerator;
import dev.vanilladev.wasteland.ModHelper;
import dev.vanilladev.wasteland.ruin.Ruin.LootStack;
import dev.vanilladev.wasteland.utils.CustomItemStack;

public class RuinSpawner extends Ruin implements IWorldGenerator
{
	private RuinGenHelper genHelper = new RuinGenHelper();
	
	public RuinSpawner(String name)
	{
		super(name);
	}
	
	protected boolean generate(World world, Random random, int xCoord, int yCoord, int zCoord)
	{
		/*	  	  A X+
		 *		  |
		 *  Z- <-- --> Z+
		 *	  	  |
		 *	  	  V X-
		 */
		
		if(world.getHeight(xCoord + 4, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord - 1) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord + 0) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord + 1) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord + 2) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord + 3) == yCoord &&
		   world.getHeight(xCoord + 4, zCoord + 4) == yCoord &&
		   
		   world.getHeight(xCoord + 3, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord + 3, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord + 2, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord + 2, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord + 1, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord + 1, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord + 0, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord + 0, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord - 1, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord - 1, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord - 2, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord - 2, zCoord + 5) == yCoord &&
		  
		   world.getHeight(xCoord - 3, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord - 3, zCoord + 5) == yCoord &&
		   
		   world.getHeight(xCoord - 4, zCoord - 2) == yCoord &&
		   world.getHeight(xCoord - 4, zCoord + 5) == yCoord)
		{
			System.out.println("Generating Succesfully at: " + ModHelper.getCoordAsString(xCoord, yCoord, zCoord));
			
			genHelper.setWorld(world);
			
			// Layer 2 Generation
			
			yCoord--;
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 4, Blocks.CHEST);
			
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(xCoord + 4, yCoord, zCoord + 4));
			LootStack loot = this.setItems(random);
			CustomItemStack.placeLoot(random, chest, CustomItemStack.getLootItems(random, loot.items, loot.minNum, loot.maxNum, loot.repeat));
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 3, Blocks.PLANKS);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 3, Blocks.AIR);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 2, Blocks.STONE_STAIRS);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.PLANKS);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 3, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 4, Blocks.NETHERRACK);
			genHelper.setBlock(xCoord - 2, yCoord + 1, zCoord + 4, Blocks.FIRE);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 4, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 5, Blocks.COBBLESTONE);
			
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 2, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 0, Blocks.PLANKS);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 0, Blocks.COBBLESTONE);
			
			// Layer 3 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 3, world.getHeight(xCoord + 3, zCoord - 4), zCoord - 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 2, world.getHeight(xCoord - 2, zCoord - 4), zCoord - 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 4, yCoord, zCoord - 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 5, world.getHeight(xCoord - 5, zCoord - 5), zCoord - 5, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			// Layer 4 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.STONE_STAIRS);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 5, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			
			// Layer 5 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 1, Blocks.COBBLESTONE);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 5, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 5, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			
			// Layer 6 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.TORCH);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			// Layer 7 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			// Layer 8 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);			
			
			// Layer 8 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			
			// Layer 9 Generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord + 5, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 5, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 4, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 3, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 1, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 3, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 4, Blocks.STONEBRICK);
			genHelper.setBlock(xCoord + 0, yCoord, zCoord + 5, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 4, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 3, Blocks.STONEBRICK, 2);
			
			genHelper.setBlock(xCoord - 3, yCoord, zCoord - 2, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 0, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 1, Blocks.STONEBRICK, 2);
			genHelper.setBlock(xCoord - 3, yCoord, zCoord + 2, Blocks.STONEBRICK, 2);
			
			return true;
		}
		
		return false;
	}
	
	private static ItemStack getLootItem(Random rand, ItemStack[] items)
	{
		int i = rand.nextInt(items.length);
		
		ItemStack itemStack = new ItemStack(items[i].getItem(), 1);
		
		if(itemStack.getItem() == Items.ROTTEN_FLESH) return new ItemStack(itemStack.getItem(), rand.nextInt(4) + 2);
		else if(itemStack.getItem() == Items.BUCKET) return new ItemStack(itemStack.getItem(), rand.nextInt(3) + 1);
		else if(itemStack.getItem() == Items.SPIDER_EYE) return new ItemStack(itemStack.getItem(), rand.nextInt(5) + 1);
		else if(itemStack.getItem() == Items.RECORD_CHIRP) return new ItemStack(itemStack.getItem(), 1);
		else if(itemStack.getItem() == Items.NAME_TAG) return new ItemStack(itemStack.getItem(), rand.nextInt(1) + 1);
		else if(itemStack.getItem() == Items.POTATO) return new ItemStack(itemStack.getItem(), rand.nextInt(2) + 1);
		else if(itemStack.getItem() == Items.CARROT) return new ItemStack(itemStack.getItem(), rand.nextInt(2) + 1);
		else if(itemStack.getItem() == Items.FEATHER) return new ItemStack(itemStack.getItem(), rand.nextInt(9) + 4);
		else if(itemStack.getItem() == Items.LEATHER_CHESTPLATE) return new ItemStack(itemStack.getItem(), 1, itemStack.getMaxDamage() - rand.nextInt(itemStack.getMaxDamage() / 2));
		else return itemStack;
	}
}
