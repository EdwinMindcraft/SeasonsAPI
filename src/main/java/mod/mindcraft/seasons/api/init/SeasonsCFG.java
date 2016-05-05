package mod.mindcraft.seasons.api.init;

import java.io.File;
import java.util.ArrayList;

import mod.mindcraft.seasons.api.enums.EnumSeason;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.StringUtils;

public class SeasonsCFG extends Configuration {
	
	public int seasonLenght;
	public boolean enableAdvancedTemperatureGen;
	public boolean enableHardcoreTemperature;
	public int hypothermiaStart;
	public int hypothermiaLevelDiff;
	public int burntStart;
	public int burntDiff;
	public boolean enableTempDebug;
	
	public SeasonCategory spring;
	public SeasonCategory summer;
	public SeasonCategory autumn;
	public SeasonCategory winter;	
	
	public boolean morningSeasonSet;
	
	public float leatherTemperature;
	public float ironTemperature;
	public float goldTemperature;
	public float diamondTemperature;
	public float chainTemperature;
	
	public int temperatureSpreadDistance;
	
	public ScreenCoordinates extTempCoord;
	public ScreenCoordinates intTempCoord;
	public ScreenCoordinates seasonCoord;
	public ScreenCoordinates timeCoord;
	
	public double[] armorMul;
	
	public SeasonsCFG(File file) {
		super(file);
		reload();
	}
	
	private float getArmorTemperature(String name, float def) {
		return getFloat(name + " Armor Temperature", "armors."+name.toLowerCase(), def, -100, 100, "How much temperature does the " + name.toLowerCase() + " armor add/remove ?");
	}
	
	private ScreenCoordinates getCoordinates(String category, float xPos, float yPos) {
		return new ScreenCoordinates(getFloat("X", category, xPos, 0, 1F, "Position on the X axis"), getFloat("Y", category, yPos, 0, 1F, "Position on the Y axis"), getBoolean("Visible", category, true, "Is this object visible ?"), getBoolean("Invert", category, false, "Is the text starting on the right ?"));
	}
	
	public void reload() {
		load();
		seasonLenght = getInt("Season Lenght", "seasons", 7, 1, 100, "Lenght of a season in minecraft days");
		enableAdvancedTemperatureGen = getBoolean("Enable Advanced Temperature Gen", "advanced", false, "Unless you want to wait 10 minutes per world launch do not enable this (note that block update temperature still uses the advanced)");
		enableHardcoreTemperature = getBoolean("Enable Harcore Temperature", "temperature", false, "Harcore Temperature will make temperature a little bit more dangerous");
		hypothermiaStart = getInt("Hypothermia Start", "temperature", 0, -1000, 1000, "At which temperature does hypothermia starts - Hardcore Mode");
		hypothermiaLevelDiff = getInt("Hypothermia Level Difference", "temperature", 5, 1, 1000, "Temperature between hypothermia levels - Hardcore Mode");
		burntStart = getInt("Burnt Start", "temperature", 80, -1000, 1000, "At which temperature does burnt starts - Hardcore Mode");
		burntDiff = getInt("Burnt Level Difference", "temperature", 100, 1, 1000, "Temperature between burnt levels - Hardcore Mode");
		enableTempDebug = getBoolean("Enable Debug", "advanced", true, "Enable the temperature display in the debug screen");
		
		spring = new SeasonCategory(this, "spring", 2.0F, 0.75F, false, new int[] {0x55ff00});
		summer = new SeasonCategory(this, "summer", 1.0F, 0.00F, false, new int[] {0xffff00});
		autumn = new SeasonCategory(this, "autumn", 3.0F, -0.1F, false, new int[] {0xff0000, 0xff8000, 0xffff00});
		winter = new SeasonCategory(this, "winter", 1.0F, -0.5F, false, new int[] {0xff5500});
		
		morningSeasonSet = getBoolean("Morning Season", "advanced", true, "Does the seasons command place you in the morning ?");
		armorMul = get("armors", "Armor Multiplier", new double[]{0.25F, 0.25F, 0.25F, 0.25F}, "Armor multiplier",  0, 1, true, 4).getDoubleList();
		leatherTemperature = getArmorTemperature("Leather", -10);
		ironTemperature = getArmorTemperature("Iron", 20);
		goldTemperature = getArmorTemperature("Gold", 10);
		chainTemperature = getArmorTemperature("Chain", 0);
		diamondTemperature = getArmorTemperature("Diamond", 10);
		
		temperatureSpreadDistance = getInt("Temperature Spread Distance", "advanced", 5, 0, 15, "Distance over which the temperature spreads");
		
		extTempCoord = getCoordinates("rendering.exttemperature", 0F, 0F);
		intTempCoord = getCoordinates("rendering.inttemperature", 0F, 0.04F);
		seasonCoord = getCoordinates("rendering.seasons", 0F, 0.08F);
		timeCoord = getCoordinates("rendering.time", 0F, 0.12F);
		
		save();
	}
	
	public float getRainFall(EnumSeason season) {
		switch (season) {
		case SPRING: return spring.rainfall;
		case SUMMER: return summer.rainfall;
		case AUTUMN: return autumn.rainfall;
		case WINTER: return winter.rainfall;
		default:
			return 0;
		}
	}
	
	public static class ScreenCoordinates {
		
		public float x, y;
		public boolean display, invert;
		public ScreenCoordinates(float x, float y, boolean display, boolean invert) {
			this.x = x;
			this.y = y;
			this.display = display;
			this.invert = invert;
		}
	}
	
	public static class SeasonCategory {
		
		public float rainfall, growthMultiplier;
		public boolean useUniformLeaves;
		public int[] colors;
		
		public SeasonCategory(SeasonsCFG cfgFile, String name, float rainfall, float growthMultiplier, boolean useUniformLeaves, int[] leavesColors) {
			this.rainfall = cfgFile.getFloat(StringUtils.capitalize(name.toLowerCase()) + " Rainfall", "seasons." + name.toLowerCase(), rainfall, 0F, 10F, "Rainfall in " + name.toLowerCase());
			this.growthMultiplier = cfgFile.getFloat(StringUtils.capitalize(name.toLowerCase()) + " Growth Multiplier", "seasons." + name.toLowerCase(), growthMultiplier, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
			this.useUniformLeaves = cfgFile.getBoolean("Use Uniform Leaves", "seasons." + name.toLowerCase(), useUniformLeaves, "Enable the uniform leaves");
			ArrayList<String> colors = new ArrayList<String>();
			for (int i : leavesColors) {
				colors.add("#" + Integer.toHexString(i).toUpperCase());
			}
			int num = 0;
			String[] colors2 = new String[colors.size()];
			for (String i : colors) {
				colors2[num] = i;
				num++;
			}
			String[] newColors = cfgFile.get("seasons." + name, "Leaves Colors", colors2).getStringList();
			ArrayList<Integer> colorInts = new ArrayList<Integer>();
			for (String c : newColors) {
				try{
					colorInts.add(Integer.decode(c));
				} catch (NumberFormatException e) {}
			}
			num = 0;
			this.colors = new int[colorInts.size()];
			for (Integer i : colorInts) {
				this.colors[num] = i;
				num++;
			}
		}
	}
}
