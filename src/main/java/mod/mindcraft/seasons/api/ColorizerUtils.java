package mod.mindcraft.seasons.api;

import java.awt.Color;


public class ColorizerUtils {
	
	public static int mix(int color1, int color2, float percentage) {
		int cR = new Color(color1).getRed();
		int cG = new Color(color1).getGreen();
		int cB = new Color(color1).getBlue();
		int oR = new Color(color2).getRed();
		int oG = new Color(color2).getGreen();
		int oB = new Color(color2).getBlue();
		
		int newR = (int) (((float)cR * (float)percentage) + ((float)oR * (1F - (float)percentage))); 
		int newG = (int) (((float)cG * (float)percentage) + ((float)oG * (1F - (float)percentage))); 
		int newB = (int) (((float)cB * (float)percentage) + ((float)oB * (1F - (float)percentage))); 
		return new Color(newR, newG, newB).hashCode();
	}

}
