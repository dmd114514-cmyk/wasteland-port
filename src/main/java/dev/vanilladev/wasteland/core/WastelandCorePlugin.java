package dev.vanilladev.wasteland.core;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

// Coremod registers the mixin config directly (same pattern as the
// Sun-Proof-Zombies mod): MixinBootstrap.init() + Mixins.addConfiguration()
// in the constructor; method names inside the mixin use mcp names and are
// dynamically remapped at runtime.
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class WastelandCorePlugin implements IFMLLoadingPlugin
{
	public WastelandCorePlugin()
	{
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.wasteland.json");
	}

	@Override
	public String[] getASMTransformerClass() { return new String[0]; }

	@Override
	public String getModContainerClass() { return null; }

	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String, Object> data) { }

	@Override
	public String getAccessTransformerClass() { return null; }
}