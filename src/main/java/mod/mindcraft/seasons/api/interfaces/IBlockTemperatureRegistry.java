package mod.mindcraft.seasons.api.interfaces;

import net.minecraft.block.state.IBlockState;

public interface IBlockTemperatureRegistry {
	
	public boolean addTemperatureToBlock(IBlockState state, float temperature, boolean ignoreState, boolean override);
	public void removeTemperatureFromBlock(IBlockState state);
	public boolean hasTemperature(IBlockState state);
	public void clear();
	/**
	 * Temperature for a block<BR>Will return {@link Integer.MIN_VALUE} if temperature isn't affected
	 * 
	 * @param state the current state if the block
	 * @return temperature
	 */
	public float getTemperatureForBlock(IBlockState state);
}
