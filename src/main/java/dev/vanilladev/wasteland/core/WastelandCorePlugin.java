package dev.vanilladev.wasteland.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"dev.vanilladev.wasteland.core", "dev.vanilladev.wasteland.mixin"})
public class WastelandCorePlugin implements IFMLLoadingPlugin, IEarlyMixinLoader
{
	@Override
	public String[] getASMTransformerClass() { return null; }

	@Override
	public String getModContainerClass() { return null; }

	@Override
	public String getSetupClass() { return null; }

	@Override
	public void injectData(Map<String, Object> data) { }

	@Override
	public String getAccessTransformerClass() { return null; }

	@Override
	public List<String> getMixinConfigs()
	{
		return Arrays.asList("mixins.wasteland.json");
	}
}