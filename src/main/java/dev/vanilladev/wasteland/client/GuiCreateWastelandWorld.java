package dev.vanilladev.wasteland.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.text.translation.I18n;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dev.vanilladev.wasteland.world.gen.WastelandGeneratorInfo;

public class GuiCreateWastelandWorld extends GuiScreen
{
	private final GuiCreateWorld createWorldGui;
	private String title;
	private String valueLabel;
	private GuiButton increaseButton;
	private GuiButton decreaseButton;
	private WastelandGeneratorInfo generatorInfo;
	private OptionsList optionsList;

	public GuiCreateWastelandWorld(GuiCreateWorld guiCreateWorld, String settingsJson, WastelandGeneratorInfo info)
	{
		this.createWorldGui = guiCreateWorld;
		this.generatorInfo = info;
	}

	public void initGui()
	{
		this.buttonList.clear();
		this.title = I18n.translateToLocal("createworld.customize.wasteland.title");
		this.valueLabel = I18n.translateToLocal("createworld.customize.wasteland.value");
		this.generatorInfo = new WastelandGeneratorInfo();
		if (this.createWorldGui.chunkProviderSettingsJson.isEmpty())
			this.generatorInfo.createDefault();
		else
			this.generatorInfo.setComplete(this.createWorldGui.chunkProviderSettingsJson);

		this.optionsList = new OptionsList();
		this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translateToLocal("gui.done")));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translateToLocal("gui.cancel")));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 5, this.height - 52, 150, 20, I18n.translateToLocal("createworld.customize.wasteland.default")));
		this.buttonList.add(this.increaseButton = new GuiButton(2, this.width / 2 - 79, this.height - 52, 74, 20, I18n.translateToLocal("createworld.customize.wasteland.rarity.increase")));
		this.buttonList.add(this.decreaseButton = new GuiButton(3, this.width / 2 - 155, this.height - 52, 74, 20, I18n.translateToLocal("createworld.customize.wasteland.rarity.decrease")));
		this.updateButtons();
	}

	private void updateButtons()
	{
		boolean enabled = this.optionsList != null && this.optionsList.selectedSlot >= 0
				&& this.optionsList.selectedSlot < WastelandGeneratorInfo.getCount();
		this.increaseButton.enabled = enabled;
		this.decreaseButton.enabled = enabled;
	}

	protected void actionPerformed(GuiButton button)
	{
		if (button.id == 0)
		{
			this.createWorldGui.chunkProviderSettingsJson = this.generatorInfo.getFinal();
			this.mc.displayGuiScreen(this.createWorldGui);
		}
		else if (button.id == 1)
			this.mc.displayGuiScreen(this.createWorldGui);
		else if (button.id == 2)
		{
			if (this.optionsList.selectedSlot >= 0 && this.optionsList.selectedSlot < WastelandGeneratorInfo.getCount())
				WastelandGeneratorInfo.setValue(this.optionsList.selectedSlot,
						WastelandGeneratorInfo.getValue(this.optionsList.selectedSlot) + 1);
		}
		else if (button.id == 3)
		{
			if (this.optionsList.selectedSlot >= 0 && this.optionsList.selectedSlot < WastelandGeneratorInfo.getCount())
				WastelandGeneratorInfo.setValue(this.optionsList.selectedSlot,
						WastelandGeneratorInfo.getValue(this.optionsList.selectedSlot) - 1);
		}
		else if (button.id == 4)
			this.generatorInfo.createDefault();
		this.updateButtons();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		this.optionsList.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawString(this.fontRenderer, this.valueLabel,
				this.width / 2 + 213 - this.fontRenderer.getStringWidth(this.valueLabel), 32, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@SideOnly(Side.CLIENT)
	class OptionsList extends GuiSlot
	{
		public int selectedSlot = -1;

		public OptionsList()
		{
			super(GuiCreateWastelandWorld.this.mc, GuiCreateWastelandWorld.this.width,
					GuiCreateWastelandWorld.this.height, 43, GuiCreateWastelandWorld.this.height - 60, 24);
		}

		@Override
		protected int getSize()
		{
			return WastelandGeneratorInfo.getCount();
		}

		@Override
		protected void elementClicked(int slotIndex, boolean doubleClick, int mouseX, int mouseY)
		{
			this.selectedSlot = slotIndex;
			GuiCreateWastelandWorld.this.updateButtons();
		}

		@Override
		protected boolean isSelected(int slotIndex)
		{
			return slotIndex == this.selectedSlot;
		}

		@Override
		protected void drawBackground() { }

		@Override
		protected void drawSlot(int entryWidth, int entryHeight, int mouseXIn, int mouseYIn, int slotIndex, int x, float partialTicks)
		{
			if (slotIndex < 0 || slotIndex >= WastelandGeneratorInfo.getCount())
				return;
			String name = I18n.translateToLocal("createworld.customize.wasteland."
					+ WastelandGeneratorInfo.getOptionKey(slotIndex));
			int value = WastelandGeneratorInfo.getValue(slotIndex);
			String valueStr = String.valueOf(value);
			boolean toggle = slotIndex == WastelandGeneratorInfo.OPT_CITY
					|| slotIndex == WastelandGeneratorInfo.OPT_BUNKER
					|| slotIndex == WastelandGeneratorInfo.OPT_DAY_ZOMBIES;
			if (toggle)
				valueStr = (value != 0) ? I18n.translateToLocal("createworld.customize.wasteland.enabled")
						: I18n.translateToLocal("createworld.customize.wasteland.disabled");
			GuiCreateWastelandWorld.this.fontRenderer.drawString(name, x + 2, entryHeight + 6, 16777215);
			GuiCreateWastelandWorld.this.fontRenderer.drawString(valueStr,
					x + 213 - GuiCreateWastelandWorld.this.fontRenderer.getStringWidth(valueStr), entryHeight + 6, 16777215);
		}

		@Override
		protected int getScrollBarX()
		{
			return this.width - 70;
		}
	}
}