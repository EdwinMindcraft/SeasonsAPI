package mod.mindcraft.seasons.colorizer;

import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class FoliageSeasonColorizer implements
		ISeasonColorizer {

	@Override
	public int getSpringColor() {
		return 0x009900;
	}

	@Override
	public int getSpringColor(IBlockState state) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSpringColor();
	}

	@Override
	public int getSummerColor() {
		return 0x55ff00;
	}

	@Override
	public int getSummerColor(IBlockState state) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSummerColor();
	}

	@Override
	public int getAutumnColor() {
		return 0xff0000;
	}

	@Override
	public int getAutumnColor(IBlockState state) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getAutumnColor();
	}

	@Override
	public int getWinterColor() {
		return 0xff5500;
	}

	@Override
	public int getWinterColor(IBlockState state) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getWinterColor();
	}

}
