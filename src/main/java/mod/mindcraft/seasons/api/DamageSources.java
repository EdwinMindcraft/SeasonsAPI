package mod.mindcraft.seasons.api;

import net.minecraft.util.DamageSource;

public class DamageSources {
	public static final DamageSource hypothermia = new DamageSource("hypothermia").setDamageBypassesArmor();
	public static final DamageSource burnt = new DamageSource("burnt").setFireDamage().setDamageBypassesArmor();
}
