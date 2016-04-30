package mod.mindcraft.seasons.api.init;

import java.io.File;

import mod.mindcraft.seasons.api.enums.EnumSeason;
import net.minecraftforge.common.config.Configuration;

public class SeasonsCFG extends Configuration {
	
	public int seasonLenght;
	public boolean enableAdvancedTemperatureGen;
	public boolean enableHardcoreTemperature;
	public int hypothermiaStart;
	public int hypothermiaLevelDiff;
	public int burntStart;
	public int burntDiff;
	public boolean enableTempDebug;
	public float summerGrowthMultiplier;
	public float springGrowthMultiplier;
	public float autumnGrowthMultiplier;
	public float winterGrowthMultiplier;
	public boolean useUniformLeavesInAutumn;
	public float summerRainfall;
	public float springRainfall;
	public float autumnRainfall;
	public float winterRainfall;
	
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
		summerGrowthMultiplier = getFloat("Summer Growth Multiplier", "seasons.summer", 0F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		springGrowthMultiplier = getFloat("Spring Growth Multiplier", "seasons.spring", 0.75F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		autumnGrowthMultiplier = getFloat("Autumn Growth Multiplier", "seasons.autumn", -0.1F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		winterGrowthMultiplier = getFloat("Winter Growth Multiplier", "seasons.winter", -0.5F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		summerRainfall = getFloat("Summer Rainfall", "seasons.summer", 0.25F, 0F, 10F, "Rainfall in summer");
		springRainfall = getFloat("Spring Rainfall", "seasons.spring", 2F, 0F, 10F, "Rainfall in spring");
		autumnRainfall = getFloat("Autumn Rainfall", "seasons.autumn", 3F, 0F, 10F, "Rainfall in autumn");
		winterRainfall = getFloat("Winter Rainfall", "seasons.winter", 1F, 0F, 10F, "Rainfall in winter");
		
		useUniformLeavesInAutumn = getBoolean("Use Uniform Leaves", "seasons.autumn", false, "Enable this to use the uniform-red leaves in autumn");
		
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
		case SPRING: return springRainfall;
		case SUMMER: return summerRainfall;
		case AUTUMN: return autumnRainfall;
		case WINTER: return winterRainfall;
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
}
