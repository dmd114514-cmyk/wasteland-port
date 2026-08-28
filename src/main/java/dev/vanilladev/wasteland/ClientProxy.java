package dev.vanilladev.wasteland;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dev.vanilladev.wasteland.entity.EntityDayZombie;
import dev.vanilladev.wasteland.entity.RenderDayZombie;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityDayZombie.class, manager -> new RenderDayZombie(manager));
	}
}