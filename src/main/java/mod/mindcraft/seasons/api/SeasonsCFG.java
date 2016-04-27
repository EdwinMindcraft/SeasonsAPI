package mod.mindcraft.seasons.api;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class SeasonsCFG extends Configuration {
	
	public int seasonLenght;
	public boolean enableTotalTimeSet;
	public boolean enableAdvancedTemperatureGen;
	public boolean enableHardcoreTemperature;
	public int hypothermiaStart;
	public int hypothermiaLevelDiff;
	
	public SeasonsCFG(File file) {
		super(file);
		load();
		seasonLenght = getInt("Season Lenght", "seasons", 7, 1, 100, "Lenght of a season in minecraft days");
		enableTotalTimeSet = getBoolean("Enable Total Time Set", "advanced", true, "Will /time set command set the total world time (required to switch seasons by time set)");
		enableAdvancedTemperatureGen = getBoolean("Enable Advanced Temperature Gen", "advanced", false, "Unless you want to wait 10 minutes per world launch do not enable this (note that block update temperature still uses the advanced)");
		enableHardcoreTemperature = getBoolean("Enable Harcore Temperature", "temperature", true, "Harcore Temperature will make temperature a little bit more dangerous");
		hypothermiaStart = getInt("Hypothermia Start", "temperature", 0, -1000, 1000, "At which temperature does hypothermia starts");
		hypothermiaLevelDiff = getInt("Hypothermia Level Difference", "temperature", 5, 1, 1000, "Temperature between hypothermia levels");
		save();
	}
}
