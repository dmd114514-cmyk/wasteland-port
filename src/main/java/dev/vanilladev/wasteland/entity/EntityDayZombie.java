package dev.vanilladev.wasteland.entity;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityDayZombie extends EntityZombie
{
	public EntityDayZombie(World world)
	{
		super(world);
	}
	
	@Override
	protected boolean isValidLightLevel()
	{
		if ((((int)(this.world.getWorldTime()/12000)) & 1) == 1) //if night-time
		{
			return false;
		}
		
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		
		if ((this.world.getLightFor(EnumSkyBlock.SKY, new BlockPos(i, j, k)) > this.rand.nextInt(32)) || !this.world.canSeeSky(new BlockPos(i, j, k)))
		{
			return false;
		}
		else
		{
			int i1 = this.world.getSkylightSubtracted();
			this.world.setSkylightSubtracted(10);
			int l = this.world.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(i, j, k));
			this.world.setSkylightSubtracted(i1);
			
			return l <= this.rand.nextInt(8);
		}
	}
}