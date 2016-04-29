package mod.mindcraft.seasons.colorizer;

import java.util.Random;

import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class FoliageSeasonColorizer implements
		ISeasonColorizer {

	@Override
	public int getSpringColor() {
		return 0x55ff00;
	}

	@Override
	public int getSpringColor(IBlockState state, BlockPos pos) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSpringColor();
	}

	@Override
	public int getSummerColor() {
		return 0xffff00;
	}

	@Override
	public int getSummerColor(IBlockState state, BlockPos pos) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSummerColor();
	}

	@Override
	public int getAutumnColor() {
		return 0xff0000;
	}

	@Override
	public int getAutumnColor(IBlockState state, BlockPos pos) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.BIRCH))
			return 0xff9300;
		Random rand = new Random(pos.toString().hashCode());
		switch (rand.nextInt(3)) {
		case 0: return getAutumnColor();
		case 1: return 0xff5000;
		case 2: return 0xff2500;
		}
		return getAutumnColor();
	}

	@Override
	public int getWinterColor() {
		return 0xff5500;
	}

	@Override
	public int getWinterColor(IBlockState state, BlockPos pos) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getWinterColor();
	}

}
