package mod.mindcraft.seasons.api;

import mod.mindcraft.seasons.api.enums.EnumSeason;
import net.minecraft.block.state.IBlockState;

public interface ISeasonColorizer {
	
	public int getSpringColor();
	public int getSpringColor(IBlockState state);
	
	public int getSummerColor();
	public int getSummerColor(IBlockState state);
	
	public int getAutumnColor();
	public int getAutumnColor(IBlockState state);
	
	public int getWinterColor();
	public int getWinterColor(IBlockState state);
		
	public static class Wrapper {
		
		ISeasonColorizer colorizer;
		
		public Wrapper(ISeasonColorizer colorizer) {
			this.colorizer = colorizer;
		}
		
		public int getColor(IBlockState state, long time) {
			int seasonMiddle = SeasonsAPI.instance.getCfg().seasonLenght * 12000;
			try {
				EnumSeason season = SeasonsAPI.instance.getWorldInterface().getSeason();
				EnumSeason other;
				if (time % (seasonMiddle*2) > seasonMiddle) {
					other = season.next();
				} else {
					other = season.prev();
				}
				float current = (((float)time + (float)seasonMiddle) % ((float)seasonMiddle * 2)) / ((float)seasonMiddle * 2);
				//System.out.println(current);
				int cColor = getColor(season, state);
				int oColor = getColor(other, state);
//				System.out.println("Percentage : " + current + ", Color : " + new Color(ColorizerUtils.mix(cColor, oColor, current)));
//				System.out.println("Main Color : " + new Color(cColor).toString());
//				System.out.println("Secondary Color : " + new Color(oColor).toString());
				return ColorizerUtils.mix(cColor, oColor, current);
			} catch (NullPointerException e) {return 0xffffff;}
		}
		
		private int getColor(EnumSeason season, IBlockState state) {
			switch (season) {
			case SPRING: return colorizer.getSpringColor(state);
			case SUMMER: return colorizer.getSummerColor(state);
			case AUTUMN: return colorizer.getAutumnColor(state);
			case WINTER: return colorizer.getWinterColor(state);
			default:
				return 0xffffff;
			}
		}
		
	}
	
}
