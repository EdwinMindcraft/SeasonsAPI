package mod.mindcraft.seasons.api.interfaces;

import mod.mindcraft.seasons.api.enums.EnumSeason;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public interface IWorldInterface {
	
	/**
	 * @return Season depending on the world time
	 * 
	 */
	public EnumSeason getSeason();
	
	/**
	 * @param time
	 * @return Season depending on the given time
	 */
	public EnumSeason getSeason(long time);
	
	/**
	 * Get the temperature at a certain position
	 * 
	 * @param pos Position
	 * @return temperature
	 */
	public float getTemperature(BlockPos pos);
	
	/**
	 * Get the temperature at a certain position
	 * @param pos Position
	 * @param external is block temp used ?
	 * @return temperature
	 */
	public float getTemperature(BlockPos pos, boolean external);
	
	/**
	 * Get the temperature of a biome
	 * 
	 * @param biome input biome
	 * @return temperature
	 */
	public float getTemperatureForBiome(BiomeGenBase biome);
	
	/**
	 * Get the temperature of a biome depending on the time
	 * 
	 * @param biome input biome
	 * @param time input time
	 * @return temperature
	 *  
	 */
	public float getTemperatureFromTime(BiomeGenBase biome, long time);
	
	/**
	 * Get the temperature of a biome depending on the time
	 * 
	 * @param pos input pos
	 * @param time input time
	 * @return temperature
	 *  
	 */
	public float getTemperatureFromTime(BlockPos pos, long time);
	
	/**
	 * Get the temperature of a biome depending on the world time
	 * 
	 * @param biome input biome
	 * @return temperature
	 *  
	 */
	public float getTemperatureFromTime(BiomeGenBase pos);
	
	/**
	 * Get the temperature of a biome depending on the world time
	 * 
	 * @param pos input pos
	 * @return temperature
	 *  
	 */
	public float getTemperatureFromTime(BlockPos pos);

}
