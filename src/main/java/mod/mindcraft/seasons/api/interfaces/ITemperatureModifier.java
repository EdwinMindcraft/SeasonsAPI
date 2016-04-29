package mod.mindcraft.seasons.api.interfaces;

import net.minecraft.item.ItemStack;

public interface ITemperatureModifier {
	
	public float getTemperature(ItemStack stack, float externalTemp);
}
