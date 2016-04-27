package mod.mindcraft.seasons;

import java.util.HashMap;

import mod.mindcraft.seasons.api.IBlockTemperatureRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockTemperatureRegistry implements IBlockTemperatureRegistry {
	
	private static final HashMap<IBlockState, Float> temperatures = new HashMap<IBlockState, Float>();
	private static final HashMap<Block, Boolean> ignoredStates = new HashMap<Block, Boolean>();
	
	@Override
	public void addTemperatureToBlock(IBlockState state, float temperature, boolean ignoreState) {
		if (!hasTemperature(state)) {
			temperatures.put(state, temperature);
			ignoredStates.put(state.getBlock(), ignoreState);			
		}
	}

	@Override
	public void removeTemperatureFromBlock(IBlockState state) {
		if (hasTemperature(state)) {
			temperatures.remove(state);
			ignoredStates.remove(state);
		}
	}

	@Override
	public boolean hasTemperature(IBlockState state) {
		if (ignoredStates.containsKey(state.getBlock()) && ignoredStates.get(state.getBlock()))
			return true;
		return temperatures.containsKey(state);
	}

	@Override
	public float getTemperatureForBlock(IBlockState state) {
		if (state == null)
			return Integer.MIN_VALUE;
		if (hasTemperature(state)) {
			if (ignoredStates.get(state.getBlock()))
				return temperatures.get(state.getBlock().getDefaultState());
			return temperatures.get(state);
		}
		return Integer.MIN_VALUE;
	}

}
