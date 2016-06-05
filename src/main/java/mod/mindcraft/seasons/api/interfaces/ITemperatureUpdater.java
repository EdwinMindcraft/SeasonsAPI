package mod.mindcraft.seasons.api.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureUpdater {
	
	public boolean requiresTemperatureUpdate(World worldIn, IBlockState state, BlockPos pos);
	
}
