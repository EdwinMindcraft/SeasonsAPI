package mod.mindcraft.seasons;

import java.util.List;
import java.util.Set;

import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
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
			super(parentScreen, getConfigElements(), "seasonsapi", false, false, I18n.format("seasonsapi.config.name"));
		}
		
		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
		}
		
		public static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = Lists.newArrayList();
			
			list.add(new DummyConfigElement.DummyCategoryElement("seasonstemperature", I18n.format("seasonsapi.config.temperature"), ConfigTemperature.class));
			list.add(new DummyConfigElement.DummyCategoryElement("seasonsseasons", I18n.format("seasonsapi.config.seasons"), ConfigSeasons.class));
			list.add(new DummyConfigElement.DummyCategoryElement("seasonsadvanced", I18n.format("seasonsapi.config.advanced"), ConfigAdvanced.class));
			list.add(new DummyConfigElement.DummyCategoryElement("seasonsarmor", I18n.format("seasonsapi.config.armor"), ConfigArmor.class));
			
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
                        (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("temperature"))).getChildElements(),
                        this.owningScreen.modID, "seasonstemperature", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
            }
			
		}
		
		public static class ConfigArmor extends CategoryEntry{

			public ConfigArmor(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
            @Override
            protected GuiScreen buildChildScreen()
            {
                return new GuiConfig(this.owningScreen,
                        (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("armors"))).getChildElements(),
                        this.owningScreen.modID, "seasonsarmor", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
            }
			
		}
		
		public static class ConfigSeasons extends CategoryEntry{

			public ConfigSeasons(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
            @Override
            protected GuiScreen buildChildScreen()
            {
            	List<IConfigElement> elements = Lists.newArrayList();
            	elements.add(new DummyConfigElement.DummyCategoryElement("seasonsspring", I18n.format("seasonsapi.config.winter"), ConfigSeasonSpring.class));
            	elements.add(new DummyConfigElement.DummyCategoryElement("seasonssummer", I18n.format("seasonsapi.config.summer"), ConfigSeasonSummer.class));
            	elements.add(new DummyConfigElement.DummyCategoryElement("seasonsautumn", I18n.format("seasonsapi.config.autumn"), ConfigSeasonAutumn.class));
            	elements.add(new DummyConfigElement.DummyCategoryElement("seasonswinter", I18n.format("seasonsapi.config.winter"), ConfigSeasonWinter.class));
            	elements.addAll((new ConfigElement(SeasonsAPI.instance.cfg.getCategory("seasons"))).getChildElements());
                return new GuiConfig(this.owningScreen,
                        elements,
                        this.owningScreen.modID,
                        "seasonsseasons",
                        false,
                        false,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
            }
			
    		public static class ConfigSeasonWinter extends CategoryEntry {
    			public ConfigSeasonWinter(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
    				super(owningScreen, owningEntryList, prop);
    			}
    			
                @Override
                protected GuiScreen buildChildScreen()
                {
                    return new GuiConfig(this.owningScreen,
                            (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("winter"))).getChildElements(),
                            this.owningScreen.modID,
                            "seasonswinter",
                            false,
                            false,
                            GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
                }
    		}
    		
    		public static class ConfigSeasonSpring extends CategoryEntry {
    			public ConfigSeasonSpring(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
    				super(owningScreen, owningEntryList, prop);
    			}
    			
                @Override
                protected GuiScreen buildChildScreen()
                {
                    return new GuiConfig(this.owningScreen,
                            (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("spring"))).getChildElements(),
                            this.owningScreen.modID,
                            "seasonsspring",
                            false,
                            false,
                            GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
                }
    		}
    		
    		public static class ConfigSeasonSummer extends CategoryEntry {
    			public ConfigSeasonSummer(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
    				super(owningScreen, owningEntryList, prop);
    			}
    			
                @Override
                protected GuiScreen buildChildScreen()
                {
                    return new GuiConfig(this.owningScreen,
                            (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("summer"))).getChildElements(),
                            this.owningScreen.modID,
                            "seasonssummer",
                            false,
                            false,
                            GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
                }
    		}
    		
    		public static class ConfigSeasonAutumn extends CategoryEntry {
    			public ConfigSeasonAutumn(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
    				super(owningScreen, owningEntryList, prop);
    			}
    			
                @Override
                protected GuiScreen buildChildScreen()
                {
                    return new GuiConfig(this.owningScreen,
                            (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("autumn"))).getChildElements(),
                            this.owningScreen.modID,
                            "seasonsautumn",
                            false,
                            false,
                            GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
                }
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
                        (new ConfigElement(SeasonsAPI.instance.cfg.getCategory("advanced"))).getChildElements(),
                        this.owningScreen.modID, "seasonsadvanced", this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                        this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                        GuiConfig.getAbridgedConfigPath(SeasonsAPI.instance.cfg.toString()));
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
