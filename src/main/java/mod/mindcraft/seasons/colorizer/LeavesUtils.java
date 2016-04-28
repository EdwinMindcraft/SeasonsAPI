package mod.mindcraft.seasons.colorizer;

import mod.mindcraft.seasons.WorldInterface;
import mod.mindcraft.seasons.api.ColorizerUtils;
import mod.mindcraft.seasons.api.ISeasonColorizer;
import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;

public class LeavesUtils {
	
	private static WorldInterface worldInterface = ((WorldInterface)SeasonsAPI.instance.getWorldInterface());
	
	public static int getLeavesColor(IBlockAccess world, BlockPos pos) {
        IBlockState iblockstate = world.getBlockState(pos);

        if (iblockstate.getBlock().equals(Blocks.leaves))
        {
            BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)iblockstate.getValue(BlockOldLeaf.VARIANT);

            if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
            {
                return ColorizerFoliage.getFoliageColorPine();
            }

            if (blockplanks$enumtype == BlockPlanks.EnumType.BIRCH)
            {
                return ColorizerUtils.mix(ColorizerFoliage.getFoliageColorBirch(), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getTotalWorldTime()), 0.5F);
            }
            if (blockplanks$enumtype == BlockPlanks.EnumType.JUNGLE)
            {
                return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getTotalWorldTime()), 0.75F);
            }
        }

        return ColorizerUtils.mix(BiomeColorHelper.getFoliageColorAtPos(world, pos), new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, worldInterface.getWorld().getTotalWorldTime()), 0.5F);
	}
}