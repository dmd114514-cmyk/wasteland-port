package dev.vanilladev.wasteland.entity;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dev.vanilladev.wasteland.ModHelper;

@SideOnly(Side.CLIENT)
public class RenderDayZombie extends RenderBiped<EntityDayZombie>
{
	private static final ResourceLocation zombieTextures = new ResourceLocation("wlm", "textures/zombie.png");
	
	public RenderDayZombie(RenderManager renderManagerIn)
	{
		super(renderManagerIn, new ModelZombie(), 0.5F);
		this.addLayer(new LayerBipedArmor(this));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityDayZombie entity)
	{
		return zombieTextures;
	}
}