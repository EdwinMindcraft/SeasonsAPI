package mod.mindcraft.seasons.colorizer;

import mod.mindcraft.seasons.WorldInterface;
import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import mod.mindcraft.seasons.api.utils.ColorizerUtils;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

public class LeavesGrassUtils {
	
	private static WorldInterface worldInterface = ((WorldInterface)SeasonsAPI.instance.getWorldInterface());
	
	public static int getLeavesColor(IBlockState iblockstate, IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			
			if (iblockstate.getBlock().equals(Blocks.leaves)) {
				BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)iblockstate.getValue(BlockOldLeaf.VARIANT);
				if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
					return ColorizerFoliage.getFoliageColorPine();
				if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH)
					return ColorizerFoliage.getFoliageColorBirch();
			}
			return ColorizerFoliage.getFoliageColorBasic();
		}
		try {
			if (iblockstate.getBlock().equals(Blocks.leaves))
			{
				BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)iblockstate.getValue(BlockOldLeaf.VARIANT);
				
				if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
				{
					return ColorizerFoliage.getFoliageColorPine();
				}
				
				if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH)
				{
					return ColorizerUtils.mix(ColorizerFoliage.getFoliageColorBirch(), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.25F);
				}
				if (blockplanks$enumtype == BlockPlanks.EnumType.JUNGLE)
				{
                    return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.75F);
				}
			}
			
            return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.25F);        	
		} catch (NullPointerException e) {
			return BiomeColorHelper.getFoliageColorAtPos(world, pos);
		}
	}
	
	public static int getGrassColor(IBlockState iblockstate, IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			if ((iblockstate.getBlock().equals(Blocks.double_plant) || (iblockstate.getBlock().equals(Blocks.tallgrass) && iblockstate.getValue(BlockTallGrass.TYPE).equals(EnumType.DEAD_BUSH))))
				return -1;
			return ColorizerGrass.getGrassColor(0.5D, 1.0D);
		}
		try {
			return ColorizerUtils.mix(BiomeColorHelper.getGrassColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new GrassSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.5F);        	
		} catch (NullPointerException e) {
			return BiomeColorHelper.getGrassColorAtPos(world, pos);
		}
	}
}