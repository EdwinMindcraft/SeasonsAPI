package mod.mindcraft.seasons.api.interfaces;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.enums.EnumSeason;
import mod.mindcraft.seasons.api.utils.ColorizerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public interface ISeasonColorizer {
	
	public int getSpringColor();
	public int getSpringColor(IBlockState state, BlockPos pos);
	
	public int getSummerColor();
	public int getSummerColor(IBlockState state, BlockPos pos);
	
	public int getAutumnColor();
	public int getAutumnColor(IBlockState state, BlockPos pos);
	
	public int getWinterColor();
	public int getWinterColor(IBlockState state, BlockPos pos);
		
	public static class Wrapper {
		
		ISeasonColorizer colorizer;
		
		public Wrapper(ISeasonColorizer colorizer) {
			this.colorizer = colorizer;
		}
		
		public int getColor(IBlockState state, BlockPos pos, long time) {
			int seasonMiddle = SeasonsAPI.instance.getCfg().seasonLenght * 12000;
			try {
				EnumSeason season = SeasonsAPI.instance.getWorldInterface().getSeason();
				EnumSeason other;
				boolean prev = false;
				if (time % (seasonMiddle*2) > seasonMiddle) {
					other = season.next();
				} else {
					other = season.prev();
					prev = true;
				}
				float current = (((float)time + (float)seasonMiddle) % ((float)seasonMiddle * 2)) / ((float)seasonMiddle * 2);
				//System.out.println(current);
				int cColor = getColor(prev ? season : other, state, pos);
				int oColor = getColor(prev ? other : season, state, pos);
//				System.out.println("Percentage : " + current + ", Color : " + new Color(ColorizerUtils.mix(cColor, oColor, current)));
//				System.out.println("Main Color : " + new Color(cColor).toString());
//				System.out.println("Secondary Color : " + new Color(oColor).toString());
				return ColorizerUtils.mix(cColor, oColor, current);
			} catch (NullPointerException e) {return 0xffffff;}
		}
		
		private int getColor(EnumSeason season, IBlockState state, BlockPos pos) {
			switch (season) {
			case SPRING: return colorizer.getSpringColor(state, pos);
			case SUMMER: return colorizer.getSummerColor(state, pos);
			case AUTUMN: return colorizer.getAutumnColor(state, pos);
			case WINTER: return colorizer.getWinterColor(state, pos);
			default:
				return 0xffffff;
			}
		}
		
	}
	
}
