package mod.mindcraft.seasons.api;

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
	public boolean seasonAlwaysVisible;
	public boolean useUniformLeavesInAutumn;
	public float summerRainfall;
	public float springRainfall;
	public float autumnRainfall;
	public float winterRainfall;
	public boolean displayTemperature;
	public boolean displayTemperatureRight;
	public boolean displaySeasonRight;
	
	public boolean morningSeasonSet;
	
	public float leatherTemperature;
	public float ironTemperature;
	public float goldTemperature;
	public float diamondTemperature;
	public float chainTemperature;
	
	public int temperatureSpreadDistance;
	
	public SeasonsCFG(File file) {
		super(file);
		reload();
	}
	
	private float getArmorTemperature(String name, float def) {
		return getFloat(name + " Armor Temperature", "armors", def, -100, 100, "How much temperature does the " + name.toLowerCase() + " armor add/remove ?");
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
		summerGrowthMultiplier = getFloat("Summer Growth Multiplier", "summer", 0F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		springGrowthMultiplier = getFloat("Spring Growth Multiplier", "spring", 0.75F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		autumnGrowthMultiplier = getFloat("Autumn Growth Multiplier", "autumn", -0.1F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		winterGrowthMultiplier = getFloat("Winter Growth Multiplier", "winter", -0.5F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		seasonAlwaysVisible = getBoolean("Season Always visible", "advanced", true, "Enable the temperature display on screen");
		displayTemperature = getBoolean("Display Temperature", "advanced", true, "Enable the temperature display on screen");
		displayTemperatureRight = getBoolean("Display Temperature Right", "advanced", false, "Display the temperature on the right side of the screen");
		displaySeasonRight = getBoolean("Display Season Right", "advanced", false, "Display the season on the right side of the screen");
		summerRainfall = getFloat("Summer Rainfall", "summer", 0.25F, 0F, 10F, "Rainfall in summer");
		springRainfall = getFloat("Spring Rainfall", "spring", 2F, 0F, 10F, "Rainfall in spring");
		autumnRainfall = getFloat("Autumn Rainfall", "autumn", 3F, 0F, 10F, "Rainfall in autumn");
		winterRainfall = getFloat("Winter Rainfall", "winter", 1F, 0F, 10F, "Rainfall in winter");
		
		useUniformLeavesInAutumn = getBoolean("Use Uniform Leaves", "autumn", false, "Enable this to use the uniform-red leaves in autumn");
		
		morningSeasonSet = getBoolean("Morning Season", "advanced", true, "Does the seasons command place you in the morning ?");
		
		leatherTemperature = getArmorTemperature("Leather", 10);
		ironTemperature = getArmorTemperature("Iron", -20);
		goldTemperature = getArmorTemperature("Gold", -10);
		chainTemperature = getArmorTemperature("Chain", 0);
		diamondTemperature = getArmorTemperature("Diamond", -10);
		
		temperatureSpreadDistance = getInt("Temperature Spread Distance", "advanced", 5, 0, 15, "Distance over which the temperature spreads");
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
}
