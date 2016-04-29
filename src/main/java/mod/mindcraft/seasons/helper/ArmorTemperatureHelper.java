package mod.mindcraft.seasons.helper;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorTemperatureHelper {
	
	public static float getArmorTemperature(ItemStack stack, float currentTemp) {
		ItemArmor armor = (ItemArmor) stack.getItem();
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		switch (armor.getArmorMaterial()) {
		case CHAIN: return cfg.chainTemperature + currentTemp;
		case DIAMOND: return cfg.diamondTemperature + currentTemp;
		case GOLD: return cfg.goldTemperature + currentTemp;
		case IRON: return cfg.ironTemperature + currentTemp;
		case LEATHER: return cfg.leatherTemperature + currentTemp;
		default:
			return 0;
		}
	}
	
}
