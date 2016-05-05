package mod.mindcraft.seasons.api.utils;

import java.awt.Color;

import net.minecraft.util.math.MathHelper;


public class ColorizerUtils {
	
	public static int mix(int color1, int color2, float percentage) {
		int cR = new Color(color1).getRed();
		int cG = new Color(color1).getGreen();
		int cB = new Color(color1).getBlue();
		int oR = new Color(color2).getRed();
		int oG = new Color(color2).getGreen();
		int oB = new Color(color2).getBlue();
		
		int newR = MathHelper.clamp_int((int) (((float)cR * (float)percentage) + ((float)oR * (1F - (float)percentage))), 0, 255); 
		int newG = MathHelper.clamp_int((int) (((float)cG * (float)percentage) + ((float)oG * (1F - (float)percentage))), 0, 255); 
		int newB = MathHelper.clamp_int((int) (((float)cB * (float)percentage) + ((float)oB * (1F - (float)percentage))), 0, 255); 
		return new Color(newR, newG, newB).hashCode();
	}

}
