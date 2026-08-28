package dev.vanilladev.wasteland.client;

import dev.vanilladev.wasteland.ModHelper;
import net.minecraft.item.Item;

public class ItemRuinIcon extends Item
{
	public ItemRuinIcon(String textureName)
	{
		this.setUnlocalizedName(ModHelper.ModInfo.modid.toLowerCase() + ".ruin.icon." + textureName);
		this.setRegistryName("ruin_icon_" + textureName);
	}
}