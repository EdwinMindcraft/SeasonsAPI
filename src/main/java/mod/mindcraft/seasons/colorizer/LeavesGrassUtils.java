package mod.mindcraft.seasons.colorizer;

import mod.mindcraft.seasons.WorldInterface;
import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import mod.mindcraft.seasons.api.utils.ColorizerUtils;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

public class LeavesGrassUtils {
	
	private static WorldInterface worldInterface = ((WorldInterface)SeasonsAPI.instance.getWorldInterface());
	
	public static int getLeavesColor(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
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
                    return ColorizerUtils.mix(ColorizerFoliage.getFoliageColorBirch(), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getWorldTime()), 0.25F);
                }
                if (blockplanks$enumtype == BlockPlanks.EnumType.JUNGLE)
                {
                    return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getWorldTime()), 0.75F);
                }
            }

            return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getWorldTime()), 0.25F);        	
        } catch (NullPointerException e) {
        	return BiomeColorHelper.getFoliageColorAtPos(world, pos);
        }
 	}
	
	public static int getGrassColor(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);
        try {
            return ColorizerUtils.mix(BiomeColorHelper.getGrassColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new GrassSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getWorldTime()), 0.75F);        	
        } catch (NullPointerException e) {
        	return BiomeColorHelper.getGrassColorAtPos(world, pos);
        }
 	}
}