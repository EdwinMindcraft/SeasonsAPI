package mod.mindcraft.seasons.api;

import java.io.File;

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
	
	public SeasonsCFG(File file) {
		super(file);
		load();
		seasonLenght = getInt("Season Lenght", "seasons", 7, 1, 100, "Lenght of a season in minecraft days");
		enableAdvancedTemperatureGen = getBoolean("Enable Advanced Temperature Gen", "advanced", false, "Unless you want to wait 10 minutes per world launch do not enable this (note that block update temperature still uses the advanced)");
		enableHardcoreTemperature = getBoolean("Enable Harcore Temperature", "temperature", false, "Harcore Temperature will make temperature a little bit more dangerous");
		hypothermiaStart = getInt("Hypothermia Start", "temperature", 0, -1000, 1000, "At which temperature does hypothermia starts - Hardcore Mode");
		hypothermiaLevelDiff = getInt("Hypothermia Level Difference", "temperature", 5, 1, 1000, "Temperature between hypothermia levels - Hardcore Mode");
		burntStart = getInt("Burnt Start", "temperature", 80, -1000, 1000, "At which temperature does burnt starts - Hardcore Mode");
		burntDiff = getInt("Burnt Level Difference", "temperature", 100, 1, 1000, "Temperature between burnt levels - Hardcore Mode");
		enableTempDebug = getBoolean("Enable Debug", "advanced", true, "Enable the temperature display in the debug screen");
		summerGrowthMultiplier = getFloat("Summer Growth Multiplier", "seasons", 0F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		springGrowthMultiplier = getFloat("Spring Growth Multiplier", "seasons", 0.75F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		autumnGrowthMultiplier = getFloat("Autumn Growth Multiplier", "seasons", -0.1F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		winterGrowthMultiplier = getFloat("Winter Growth Multiplier", "seasons", -0.5F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		seasonAlwaysVisible = getBoolean("Season Always visible", "advanced", true, "Enable the temperature display on screen");
		save();
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
		summerGrowthMultiplier = getFloat("Summer Growth Multiplier", "seasons", 0F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		springGrowthMultiplier = getFloat("Spring Growth Multiplier", "seasons", 0.75F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		autumnGrowthMultiplier = getFloat("Autumn Growth Multiplier", "seasons", -0.1F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		winterGrowthMultiplier = getFloat("Winter Growth Multiplier", "seasons", -0.5F, -10F, 10F, "Bonus growth - 0 means vanilla - negative is decay");
		seasonAlwaysVisible = getBoolean("Season Always visible", "advanced", true, "Enable the temperature display on screen");
		save();
	}
}
