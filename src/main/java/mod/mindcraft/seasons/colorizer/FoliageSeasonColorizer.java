package mod.mindcraft.seasons.colorizer;

import java.util.Random;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FoliageSeasonColorizer implements
		ISeasonColorizer {

	@Override
	public int getSpringColor() {
		return 0x55ff00;
	}

	@Override
	public int getSpringColor(IBlockState state, BlockPos pos, World world) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSpringColor();
	}

	@Override
	public int getSummerColor() {
		return 0xffff00;
	}

	@Override
	public int getSummerColor(IBlockState state, BlockPos pos, World world) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getSummerColor();
	}

	@Override
	public int getAutumnColor() {
		return 0xff0000;
	}

	@Override
	public int getAutumnColor(IBlockState state, BlockPos pos, World world) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.BIRCH))
			return 0xff9300;
		if (!SeasonsAPI.instance.getCfg().useUniformLeavesInAutumn) {
			for (int i = -2; i <= 2; i++) {
				for (int j = 2; j >= -2; j--) {
					for (int k = -2; k <= 2; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							pos = pos.add(i, j, k);
							break;
						}
					}
				}
			}
			while (world.getBlockState(pos.up()).getBlock() instanceof BlockLog)
				pos = pos.up();
			Random rand = new Random(pos.toString().hashCode());
			switch (rand.nextInt(3)) {
			case 0: return getAutumnColor();
			case 1: return 0xff8000;
			case 2: return 0xffff00;
			}			
		}
		return getAutumnColor();
	}

	@Override
	public int getWinterColor() {
		return 0xff5500;
	}

	@Override
	public int getWinterColor(IBlockState state, BlockPos pos, World world) {
		if (state.getBlock().equals(Blocks.leaves) && state.getValue(BlockOldLeaf.VARIANT).equals(BlockPlanks.EnumType.SPRUCE))
			return 0;
		return getWinterColor();
	}

}
