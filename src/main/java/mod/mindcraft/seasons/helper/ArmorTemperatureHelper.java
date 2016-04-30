package mod.mindcraft.seasons.helper;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.init.SeasonsCFG;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ArmorTemperatureHelper {
	
	public static float getArmorTemperature(ItemStack stack, float currentTemp) {
		ItemArmor armor = (ItemArmor) stack.getItem();
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		float mod = currentTemp > 0 ? 1 : -1;
		switch (armor.getArmorMaterial()) {
		case CHAIN: return (float) (cfg.chainTemperature * cfg.armorMul[armor.getEquipmentSlot().getIndex()] * mod + currentTemp);
		case DIAMOND: return (float) (cfg.diamondTemperature * cfg.armorMul[armor.getEquipmentSlot().getIndex()] * mod + currentTemp);
		case GOLD: return (float) (cfg.goldTemperature * cfg.armorMul[armor.getEquipmentSlot().getIndex()] * mod + currentTemp);
		case IRON: return (float) (cfg.ironTemperature * cfg.armorMul[armor.getEquipmentSlot().getIndex()] * mod + currentTemp);
		case LEATHER: return (float) (cfg.leatherTemperature * cfg.armorMul[armor.getEquipmentSlot().getIndex()] * mod + currentTemp);
		default:
			return 0;
		}
	}
	
}
