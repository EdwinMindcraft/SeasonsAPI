package mod.mindcraft.seasons;

import java.util.List;
import java.util.Set;

import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.google.common.collect.Lists;

public class ConfigFactory implements IModGuiFactory {
	
	public static class ConfigScreen extends GuiConfig {

		public ConfigScreen(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), "seasonsapi", false, false, "seasonsapi.config.name");
		}
		
		public static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = Lists.newArrayList();
			
			list.add(new DummyConfigElement.DummyCategoryElement("temperature", "seasonsapi.config.temperature", ConfigTemperature.class));
			list.add(new DummyConfigElement.DummyCategoryElement("seasons", "seasonsapi.config.seasons", ConfigSeasons.class));
			list.add(new DummyConfigElement.DummyCategoryElement("advanced", "seasonsapi.config.advanced", ConfigAdvanced.class));
			
			return list;
		}
		
		public static class ConfigTemperature extends CategoryEntry{

			public ConfigTemperature(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
            @Override
            protected GuiScreen buildChildScreen()
            {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(SeasonsAPI.instance.getCfg().getCategory("temperature"))).getChildElements(),
                        this.owningScreen.modID, "temperature", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.getCfg().toString()));
            }
			
		}
		
		public static class ConfigSeasons extends CategoryEntry{

			public ConfigSeasons(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
            @Override
            protected GuiScreen buildChildScreen()
            {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(SeasonsAPI.instance.getCfg().getCategory("seasons"))).getChildElements(),
                        this.owningScreen.modID, "seasons", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.getCfg().toString()));
            }
			
		}
		
		public static class ConfigAdvanced extends CategoryEntry{

			public ConfigAdvanced(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
            @Override
            protected GuiScreen buildChildScreen()
            {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(SeasonsAPI.instance.getCfg().getCategory("advanced"))).getChildElements(),
                        this.owningScreen.modID, "advanced", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.getCfg().toString()));
            }
			
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
