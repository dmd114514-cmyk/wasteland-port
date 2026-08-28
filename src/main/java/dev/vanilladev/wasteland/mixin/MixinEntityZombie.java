package dev.vanilladev.wasteland.mixin;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Day zombies now are plain vanilla EntityZombie; this redirect keeps them
// from burning in daylight - the 1.12 equivalent of shouldBurnInDay=false
@Mixin(EntityZombie.class)
public abstract class MixinEntityZombie
{
	@Redirect(method = "func_70636_d", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_72935_r()Z"))
	private boolean wastelandNoDayBurn(World world)
	{
		return false;
	}
}