package mod.mindcraft.seasons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.google.common.collect.Lists;

public class ConfigFactory implements IModGuiFactory {
	
	public static class ConfigScreen extends GuiConfig {

		public ConfigScreen(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), "seasonsapi", false, false, I18n.format("seasonsapi.config.name"));
		}
		
		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
		}
		
		public static List<IConfigElement> getConfigElements() {
			ArrayList<String> childs = new ArrayList<String>();
			for (String category : SeasonsAPI.instance.getCfg().getCategoryNames()) {
				if (SeasonsAPI.instance.getCfg().getCategory(category).parent == null)
					childs.add(category);
			}
			List<IConfigElement> list = Lists.newArrayList();
			for (String category : childs) {
				if (category.contains("\\."))
					continue;
				list.add((new ConfigElement(SeasonsAPI.instance.cfg.getCategory(category))));
			}
			return list;
		}
				
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigScreen.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(
			RuntimeOptionCategoryElement element) {
		// TODO Auto-generated method stub
		return null;
	}

}
