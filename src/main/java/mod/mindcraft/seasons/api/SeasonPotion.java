package mod.mindcraft.seasons.api;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class SeasonPotion extends Potion {

	public SeasonPotion(ResourceLocation location, boolean badEffect, int potionColor) {
		super(location, badEffect, potionColor);
	}
}
