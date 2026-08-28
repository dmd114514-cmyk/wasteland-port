package dev.vanilladev.wasteland;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dev.vanilladev.wasteland.gui.BiomesGui;

public class GetBiomesCommand extends CommandBase
{
	private int minSize = 200;
	
	public GetBiomesCommand()
	{
		super();
	}
	
	@Override
	public String getName()
	{
		return "biomes";
	}
	
	@Override
	public String getUsage(ICommandSender iCommandSender)
	{
		return "/biomes <range> (min range is " + String.valueOf(this.minSize) + ")";
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] var)
	{
		if (iCommandSender instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) iCommandSender;
			World world = player.getEntityWorld();
			
			if (var.length > 0)
			{
				int range = Math.max(Integer.parseInt(var[0]), minSize);
				int interval = (int) (range / minSize);
				
				int[][] biomeMap = new int[minSize][minSize];
				Random rand = new Random();
				player.sendMessage(new TextComponentString("Creating map..."));
				List<Biome> biomes = new ArrayList<Biome>();
				Biome currentBiome;
				float count = 0;
				for (int j = 0; j < minSize; j++)
				{
					for (int i = 0; i < minSize; i++)
					{
						currentBiome = world.getBiome(new BlockPos((int) (player.posX - (minSize * interval / 2) + i * interval), 0, (int) (player.posZ - (minSize * interval / 2) + j * interval)));
						
						if (!biomes.contains(currentBiome))
						{
							biomes.add(currentBiome);
						}
						biomeMap[j][i] = getBiomeColour(currentBiome) + 0xFF000000;
					}
					if (count / minSize > 0.1)
					{
						player.sendMessage(new TextComponentString(String.valueOf((int) (100 * j / minSize)) + "%"));
						count = 0;
					}
					count++;
				}
				
				if (FMLCommonHandler.instance().getSide().isClient())
				{
					openBiomesGui(biomeMap, biomes);
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void openBiomesGui(int[][] biomeMap, List<Biome> biomes)
	{
		Minecraft.getMinecraft().displayGuiScreen(new BiomesGui(biomeMap, 200, biomes));
	}
	
	private int getBiomeColour(Biome biomeGenBase)
	{
		if (biomeGenBase != null)
		{
			return (WastelandBiomes.getBiomeColor(biomeGenBase) & 0x00FFFFFF);
		}
		else
		{
			return 0x00000000;
		}
	}
}