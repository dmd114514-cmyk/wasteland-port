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
import dev.vanilladev.wasteland.utils.Rectangle;
import dev.vanilladev.wasteland.utils.Vector;

public class RuinSurvivorTent extends Ruin implements IWorldGenerator
{
	private RuinGenHelper genHelper = new RuinGenHelper();
	
	public RuinSurvivorTent(String par1Name)
	{
		super(par1Name);
	}
	
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		this.genHelper.setWorld(world);
		
		Rectangle pos = new Rectangle(new Vector(x - 2, y, z - 3), 5, 6);
		Block biomeBlock = ModConfig.getSurfaceBlock();
		int[] levels = Layout.getLevels(world, pos);
		
		if (Layout.checkLevel(levels, 0))
		{
			int xCoord = x;
			int yCoord = Layout.getAverageLevel(levels) - 1;
			int zCoord = z;
			
			// Layer 1 generation
			
			/*	  	  A X+
			 *		  |
			 *  Z- <-- --> Z+
			 *	  	  |
			 *	  	  V X-
			 */
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, biomeBlock);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, biomeBlock);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 1, biomeBlock);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, biomeBlock);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, biomeBlock);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 2, biomeBlock);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, biomeBlock);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, biomeBlock);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, biomeBlock);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.CHEST);
			
			TileEntityChest chest = (TileEntityChest) world.getTileEntity(new BlockPos(xCoord - 1, yCoord, zCoord));
			LootStack loot = this.setItems(random);
			CustomItemStack.placeLoot(random, chest, CustomItemStack.getLootItems(random, loot.items, loot.minNum, loot.maxNum, loot.repeat));
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, biomeBlock);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, biomeBlock);
			
			genHelper.setBlock(xCoord, yCoord, zCoord - 3, biomeBlock);
			genHelper.setBlock(xCoord, yCoord, zCoord - 2, biomeBlock);
			genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord, yCoord, zCoord + 0, Blocks.PLANKS);
			genHelper.setBlock(xCoord, yCoord - 1, zCoord + 0, Blocks.TNT);
			genHelper.setBlock(xCoord, yCoord, zCoord + 1, Blocks.PLANKS);
			genHelper.setBlock(xCoord, yCoord, zCoord + 2, biomeBlock);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, biomeBlock);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, biomeBlock);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, biomeBlock);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, biomeBlock);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, biomeBlock);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, biomeBlock);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, biomeBlock);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, biomeBlock);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, biomeBlock);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, biomeBlock);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, biomeBlock);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, biomeBlock);
			
			// Layer 2 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.WOOL);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.WOOL);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 1, Blocks.WOOL);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, Blocks.WOOL);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.WOOL);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 2, Blocks.WOOL);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord + 0, Blocks.WOODEN_PRESSURE_PLATE);
			genHelper.setBlock(xCoord, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.WOOL);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, Blocks.WOOL);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.WOOL);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.WOOL);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.WOOL);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.WOOL);
			
			// Layer 3 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 2, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.WOOL);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.WOOL);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.WOOL);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.WOOL);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.WOOL);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.WOOL);
			
			genHelper.setBlock(xCoord, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.WOOL);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.WOOL);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.WOOL);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.WOOL);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.WOOL);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.WOOL);
			
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 2, yCoord, zCoord + 2, Blocks.AIR);
			
			// Layer 4 generation
			
			yCoord++;
			
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord - 1, yCoord, zCoord + 2, Blocks.AIR);
			
			genHelper.setBlock(xCoord, yCoord, zCoord - 3, Blocks.WOOL);
			genHelper.setBlock(xCoord, yCoord, zCoord - 2, Blocks.WOOL);
			genHelper.setBlock(xCoord, yCoord, zCoord - 1, Blocks.WOOL);
			genHelper.setBlock(xCoord, yCoord, zCoord + 0, Blocks.WOOL);
			genHelper.setBlock(xCoord, yCoord, zCoord + 1, Blocks.WOOL);
			genHelper.setBlock(xCoord, yCoord, zCoord + 2, Blocks.WOOL);
			
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 3, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 2, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord - 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 0, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 1, Blocks.AIR);
			genHelper.setBlock(xCoord + 1, yCoord, zCoord + 2, Blocks.AIR);
			
			return true;
		}
		return false;
	}
	
	
}
