package dev.vanilladev.wasteland.mixin;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Day zombies are plain vanilla EntityZombie. No refmap is generated in this
// project, so all injected names are the runtime srg names (mcp names like
// shouldBurnInDay from the Sun-Proof-Zombies sample only work with a refmap,
// which needs the MixinGradle plugin). Redirects/injects on srg names resolve
// by literal match at runtime.
@Mixin(EntityZombie.class)
public abstract class MixinEntityZombie
{
	// shouldBurnInDay=false equivalent (srg): the daylight-burn branch of
	// onLivingUpdate (func_70636_d) only runs when World.isDaytime()
	// (func_72935_r) is true - redirect it to false so zombies never burn.
	@Redirect(method = "func_70636_d", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_72935_r()Z"))
	private boolean wastelandNoDayBurn(World world)
	{
		return false;
	}

	// always spawnable (day and night); func_70814_o is isValidLightLevel in
	// 1.12 (verified against srg_to_snapshot_20171003-1.12.tsrg - the old
	// 1.7 name func_70652_k would silently inject a dead method)
	@Inject(method = "func_70814_o", at = @At("HEAD"), cancellable = true)
	private void wastelandSpawnAnyTime(CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(true);
	}
}