package mod.mindcraft.seasons.colorizer;

import java.util.Random;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
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
		if (!SeasonsAPI.instance.getCfg().spring.useUniformLeaves || SeasonsAPI.instance.getCfg().spring.colors.length == 0) {
			if (SeasonsAPI.instance.getCfg().spring.colors.length == 1)
				return SeasonsAPI.instance.getCfg().spring.colors[0];
			if (world.getBlockState(pos).getBlock().equals(Blocks.leaves) || world.getBlockState(pos).getBlock().equals(Blocks.leaves))
				return getLeavesColor(state, pos, world, SeasonsAPI.instance.getCfg().spring.colors);
			return getVineColor(state, pos, world, SeasonsAPI.instance.getCfg().spring.colors);
		}
		return getSpringColor();
	}

	@Override
	public int getSummerColor() {
		return 0xffff00;
	}

	@Override
	public int getSummerColor(IBlockState state, BlockPos pos, World world) {
		if (!SeasonsAPI.instance.getCfg().summer.useUniformLeaves || SeasonsAPI.instance.getCfg().summer.colors.length == 0) {
			if (SeasonsAPI.instance.getCfg().summer.colors.length == 1)
				return SeasonsAPI.instance.getCfg().summer.colors[0];
			if (world.getBlockState(pos).getBlock().equals(Blocks.leaves) || world.getBlockState(pos).getBlock().equals(Blocks.leaves))
				return getLeavesColor(state, pos, world, SeasonsAPI.instance.getCfg().summer.colors);
			return getVineColor(state, pos, world, SeasonsAPI.instance.getCfg().summer.colors);
		}
		return getSummerColor();
	}

	@Override
	public int getAutumnColor() {
		return 0xff0000;
	}

	@Override
	public int getAutumnColor(IBlockState state, BlockPos pos, World world) {
		if (!SeasonsAPI.instance.getCfg().autumn.useUniformLeaves || SeasonsAPI.instance.getCfg().autumn.colors.length == 0) {
			if (SeasonsAPI.instance.getCfg().autumn.colors.length == 1)
				return SeasonsAPI.instance.getCfg().autumn.colors[0];
			if (world.getBlockState(pos).getBlock().equals(Blocks.leaves) || world.getBlockState(pos).getBlock().equals(Blocks.leaves))
				return getLeavesColor(state, pos, world, SeasonsAPI.instance.getCfg().autumn.colors);
			return getVineColor(state, pos, world, SeasonsAPI.instance.getCfg().autumn.colors);
		}
		return getAutumnColor();
	}
		
	@Override
	public int getWinterColor() {
		return 0xff5500;
	}

	@Override
	public int getWinterColor(IBlockState state, BlockPos pos, World world) {
		if (!SeasonsAPI.instance.getCfg().winter.useUniformLeaves || SeasonsAPI.instance.getCfg().winter.colors.length == 0) {
			if (SeasonsAPI.instance.getCfg().winter.colors.length == 1)
				return SeasonsAPI.instance.getCfg().winter.colors[0];
			if (world.getBlockState(pos).getBlock().equals(Blocks.leaves) || world.getBlockState(pos).getBlock().equals(Blocks.leaves))
				return getLeavesColor(state, pos, world, SeasonsAPI.instance.getCfg().winter.colors);
			return getVineColor(state, pos, world, SeasonsAPI.instance.getCfg().winter.colors);
		}
		return getWinterColor();
	}
	
	private int getLeavesColor(IBlockState state, BlockPos pos, World world, int[] colors) {
		EnumType type = (state.getBlock().equals(Blocks.leaves) ? state.getValue(BlockOldLeaf.VARIANT) : state.getValue(BlockNewLeaf.VARIANT));
		boolean found = false;
		//Jungle check
		if (type.equals(EnumType.JUNGLE)) {
			for (int i = -7; i <= 7; i++) {
				for (int j = 8; j >= -8; j--) {
					for (int k = -7; k <= 7; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							int check = 0;
							BlockPos finalCheckPos = pos.add(i, j, k);
							EnumType logType = (world.getBlockState(pos.add(i, j, k)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i, j, k)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i, j, k)).getValue(BlockNewLog.VARIANT));
							if (check < 2 && world.getBlockState(pos.add(i + 1, j, k)).getBlock().equals(Blocks.log) &&
									(world.getBlockState(pos.add(i + 1, j, k)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i + 1, j, k)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i + 1, j, k)).getValue(BlockNewLog.VARIANT)).equals(EnumType.JUNGLE)) {
								check++;
								finalCheckPos = finalCheckPos.add(1, 0, 0);
							}
							if (check < 2 && world.getBlockState(pos.add(i - 1, j, k)).getBlock().equals(Blocks.log) &&
									(world.getBlockState(pos.add(i - 1, j, k)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i - 1, j, k)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i - 1, j, k)).getValue(BlockNewLog.VARIANT)).equals(EnumType.JUNGLE)) {
								check++;
								finalCheckPos = finalCheckPos.add(-1, 0, 0);									
							}
							if (check < 2 && world.getBlockState(pos.add(i, j, k + 1)).getBlock().equals(Blocks.log) &&
									(world.getBlockState(pos.add(i, j, k + 1)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i, j, k + 1)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i, j, k + 1)).getValue(BlockNewLog.VARIANT)).equals(EnumType.JUNGLE)) {
								check++;
								finalCheckPos = finalCheckPos.add(0, 0, 1);									
							}
							if (check < 2 && world.getBlockState(pos.add(i, j, k - 1)).getBlock().equals(Blocks.log) &&
									(world.getBlockState(pos.add(i, j, k - 1)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i, j, k - 1)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i, j, k - 1)).getValue(BlockNewLog.VARIANT)).equals(EnumType.JUNGLE)) {
								check++;
								finalCheckPos = finalCheckPos.add(0, 0, -1);									
							}
							if (check >= 2 &&
									world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog &&
									logType.equals(type) &&
									world.getBlockState(pos.add(i, j, k)).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y) &&
									world.getBlockState(finalCheckPos).getBlock() instanceof BlockLog &&
									(world.getBlockState(finalCheckPos).getBlock().equals(Blocks.log) ? world.getBlockState(finalCheckPos).getValue(BlockOldLog.VARIANT) : world.getBlockState(finalCheckPos).getValue(BlockNewLog.VARIANT)).equals(type) &&
									world.getBlockState(finalCheckPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
									pos = pos.add(i, j, k);
									found = true;
									break;
							}
						}
					}
				}
			}			
		}
		for (int i = -1; i <= 1; i++) {
			for (int j = 2; j >= -2; j--) {
				for (int k = -1; k <= 1; k++) {
					BlockPos newPos = pos.add(i, j, k);
					if (world.getBlockState(newPos).getBlock() instanceof BlockLog &&
							(world.getBlockState(newPos).getBlock().equals(Blocks.log)
									? world.getBlockState(newPos).getValue(BlockOldLog.VARIANT)
											: world.getBlockState(newPos).getValue(BlockNewLog.VARIANT))
											.equals(type)
											&& world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)
											) {
						pos = newPos;
						found = true;
						break;
					}
			}
			}			
		}
		if (!found) {
			for (int i = -2; i <= 2; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -2; k <= 2; k++) {
						BlockPos newPos = pos.add(i, j, k);
						if (world.getBlockState(newPos).getBlock() instanceof BlockLog &&
								(world.getBlockState(newPos).getBlock().equals(Blocks.log)
										? world.getBlockState(newPos).getValue(BlockOldLog.VARIANT)
												: world.getBlockState(newPos).getValue(BlockNewLog.VARIANT))
												.equals(type)
												&& world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)
												) {
							pos = newPos;
							found = true;
							break;
						}
					}
				}
			}
		}
		if (!found) {
			for (int i = -4; i <= 4; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -4; k <= 4; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							EnumType logType = (world.getBlockState(pos.add(i, j, k)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i, j, k)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i, j, k)).getValue(BlockNewLog.VARIANT));
							if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog &&
									logType.equals(type) &&
									world.getBlockState(pos.add(i, j, k)).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
									pos = pos.add(i, j, k);
									found = true;
									break;
							}
						}
					}
				}
			}
		}
		if (!found) {
			for (int i = -7; i <= 7; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -7; k <= 7; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							EnumType logType = (world.getBlockState(pos.add(i, j, k)).getBlock().equals(Blocks.log) ? world.getBlockState(pos.add(i, j, k)).getValue(BlockOldLog.VARIANT) : world.getBlockState(pos.add(i, j, k)).getValue(BlockNewLog.VARIANT));
							if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog &&
									logType.equals(type) &&
									world.getBlockState(pos.add(i, j, k)).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
									pos = pos.add(i, j, k);
									found = true;
									break;
							}
						}
					}
				}
			}
		}
		while (world.getBlockState(pos.up()).getBlock() instanceof BlockLog) {
			pos = pos.up();
		}
		Random rand = new Random(pos.toString().hashCode());
		return colors[rand.nextInt(colors.length)];
	}
	
	private int getVineColor(IBlockState state, BlockPos pos, World world, int[] colors) {
		boolean found = false;
		for (int i = -1; i <= 1; i++) {
			for (int j = 2; j >= -2; j--) {
				for (int k = -1; k <= 1; k++) {
					BlockPos newPos = pos.add(i, j, k);
					if (world.getBlockState(newPos).getBlock() instanceof BlockLog && world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
						pos = newPos;
						found = true;
						break;
					}
			}
			}			
		}
		if (!found) {
			for (int i = -2; i <= 2; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -2; k <= 2; k++) {
						BlockPos newPos = pos.add(i, j, k);
						if (world.getBlockState(newPos).getBlock() instanceof BlockLog && world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
							pos = newPos;
							found = true;
							break;
						}
					}
				}
			}
		}
		if (!found) {
			for (int i = -4; i <= 4; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -4; k <= 4; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							BlockPos newPos = pos.add(i, j, k);
							if (world.getBlockState(newPos).getBlock() instanceof BlockLog && world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
									pos = newPos;
									found = true;
									break;
							}
						}
					}
				}
			}
		}
		if (!found) {
			for (int i = -7; i <= 7; i++) {
				for (int j = 4; j >= -4; j--) {
					for (int k = -7; k <= 7; k++) {
						if (world.getBlockState(pos.add(i, j, k)).getBlock() instanceof BlockLog) {
							BlockPos newPos = pos.add(i, j, k);
							if (world.getBlockState(newPos).getBlock() instanceof BlockLog && world.getBlockState(newPos).getValue(BlockLog.LOG_AXIS).equals(EnumAxis.Y)) {
									pos = newPos;
									found = true;
									break;
							}
						}
					}
				}
			}
		}
		while (world.getBlockState(pos.up()).getBlock() instanceof BlockLog) {
			pos = pos.up();
		}
		Random rand = new Random(pos.toString().hashCode());
		return colors[rand.nextInt(colors.length)];
	}

}
