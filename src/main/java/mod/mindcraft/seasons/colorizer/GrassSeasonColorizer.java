package mod.mindcraft.seasons.colorizer;

import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class GrassSeasonColorizer implements
		ISeasonColorizer {

	@Override
	public int getSpringColor() {
		return 0x009900;
	}

	@Override
	public int getSpringColor(IBlockState state, BlockPos pos) {
		return getSpringColor();
	}

	@Override
	public int getSummerColor() {
		return 0xffff00;
	}

	@Override
	public int getSummerColor(IBlockState state, BlockPos pos) {
		return getSummerColor();
	}

	@Override
	public int getAutumnColor() {
		return 0xff5500;
	}

	@Override
	public int getAutumnColor(IBlockState state, BlockPos pos) {
		return getAutumnColor();
	}

	@Override
	public int getWinterColor() {
		return 0xffffff;
	}

	@Override
	public int getWinterColor(IBlockState state, BlockPos pos) {
		return getWinterColor();
	}

}
