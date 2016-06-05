package mod.mindcraft.seasons.colorizer;

import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.common.block.BlockBOPLeaves;
import biomesoplenty.common.enums.BOPTrees;
import mod.mindcraft.seasons.Seasons;
import mod.mindcraft.seasons.WorldInterface;
import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.ISeasonColorizer;
import mod.mindcraft.seasons.api.utils.ColorizerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.RegistryDelegate;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class LeavesGrassUtils {
	
	private static WorldInterface worldInterface = ((WorldInterface)SeasonsAPI.instance.getWorldInterface());
	
	@SuppressWarnings("unchecked")
	public static int getLeavesColor(IBlockState iblockstate, IBlockAccess world, BlockPos pos, int originalColor) {
		if (world == null || pos == null) {
			return originalColor;
		}
		try {
			if (iblockstate.getBlock().equals(Blocks.LEAVES))
			{
				BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)iblockstate.getValue(BlockOldLeaf.VARIANT);
				
				if (blockplanks$enumtype == BlockPlanks.EnumType.SPRUCE)
				{
					return ColorizerFoliage.getFoliageColorPine();
				}
				if (blockplanks$enumtype == BlockPlanks.EnumType.JUNGLE)
				{
                    return ColorizerUtils.mix(originalColor, new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.33F);
				}
			}
			else if (Loader.isModLoaded("BiomesOPlenty")) {
				if (iblockstate.getBlock().equals(BOPBlocks.leaves_0) || iblockstate.getBlock().equals(BOPBlocks.leaves_2)) {
					return originalColor;
				}
				else if (iblockstate.getBlock().equals(BOPBlocks.leaves_1)) {
					BOPTrees treeType = iblockstate.getValue(BlockBOPLeaves.paging.getVariantProperty(1));
					if (!treeType.equals(BOPTrees.FIR)) {
						return originalColor;
					}
				}
				else if (iblockstate.getBlock().equals(BOPBlocks.leaves_3)) {
					BOPTrees treeType = iblockstate.getValue(BlockBOPLeaves.paging.getVariantProperty(3));
					if (treeType.equals(BOPTrees.HELLBARK) || treeType.equals(BOPTrees.JACARANDA)) {
						return originalColor;
					}
				}
			}
            return ColorizerUtils.mix(originalColor, new ISeasonColorizer.Wrapper(new FoliageSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.25F);        	
		} catch (NullPointerException e) {
			return originalColor;
		}
	}
	
	public static int getGrassColor(IBlockState iblockstate, IBlockAccess world, BlockPos pos, int originalColor) {
		if (world == null || pos == null) {
			return originalColor;
		}
		try {
			return ColorizerUtils.mix(originalColor, new ISeasonColorizer.Wrapper(new GrassSeasonColorizer()).getColor(iblockstate, pos, worldInterface.getWorld().getWorldTime(), worldInterface.getWorld()), 0.5F);        	
		} catch (NullPointerException e) {
			return originalColor;
		}
	}
	
	public static void registerColors() {
		BlockColors colors = Minecraft.getMinecraft().getBlockColors();
		Map<RegistryDelegate<Block>, IBlockColor> map = ReflectionHelper.getPrivateValue(BlockColors.class, colors, "blockColorMap");
		ArrayList<Block> leavesBlock = new ArrayList<Block>();
		ArrayList<Block> grassBlock = new ArrayList<Block>();
		Logger logger = LogManager.getLogger("Color Modifier");
		logger.info("Applying Vanilla Color Modification...");
		leavesBlock.add(Blocks.LEAVES);
		leavesBlock.add(Blocks.LEAVES2);
		leavesBlock.add(Blocks.VINE);
		grassBlock.add(Blocks.DOUBLE_PLANT);
		grassBlock.add(Blocks.GRASS);
		grassBlock.add(Blocks.TALLGRASS);
		if (Loader.isModLoaded("BiomesOPlenty")) {
			logger.info("Applying Biomes O\' Plenty Color Modification...");
			leavesBlock.add(BOPBlocks.leaves_0);
			leavesBlock.add(BOPBlocks.leaves_1);
			leavesBlock.add(BOPBlocks.leaves_2);
			leavesBlock.add(BOPBlocks.leaves_3);
			leavesBlock.add(BOPBlocks.leaves_4);
			leavesBlock.add(BOPBlocks.leaves_5);
			leavesBlock.add(BOPBlocks.leaves_6);
			
			grassBlock.add(BOPBlocks.plant_0);
			grassBlock.add(BOPBlocks.plant_1);
			grassBlock.add(BOPBlocks.double_plant);
			grassBlock.add(BOPBlocks.grass);
			grassBlock.add(BOPBlocks.ivy);
		}
		for(Block b : leavesBlock) {
			IBlockColor color = map.get(b.delegate);
			if(color != null)
				colors.registerBlockColorHandler(getFoliageColor(color), b);
		}
		for(Block b : grassBlock) {
			IBlockColor color = map.get(b.delegate);
			if(color != null)
				colors.registerBlockColorHandler(getGrassColor(color), b);
		}
	}
	
	private static IBlockColor getFoliageColor (final IBlockColor original) {
		return new IBlockColor() {
			
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess p_186720_2_, BlockPos pos, int tintIndex) {
				return Seasons.enabled ? LeavesGrassUtils.getLeavesColor(state, p_186720_2_, pos, original.colorMultiplier(state, p_186720_2_, pos, tintIndex)) : original.colorMultiplier(state, p_186720_2_, pos, tintIndex);
			}
		};
	}
	
	private static IBlockColor getGrassColor (final IBlockColor original) {
		return new IBlockColor() {
			
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess p_186720_2_, BlockPos pos, int tintIndex) {
				return Seasons.enabled ? LeavesGrassUtils.getGrassColor(state, p_186720_2_, pos, original.colorMultiplier(state, p_186720_2_, pos, tintIndex)) : original.colorMultiplier(state, p_186720_2_, pos, tintIndex);
			}
		};
	}
}