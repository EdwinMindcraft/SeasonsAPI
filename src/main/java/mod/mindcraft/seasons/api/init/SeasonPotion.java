package mod.mindcraft.seasons.api.init;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeasonPotion extends Potion {
	
	public static final Potion HYPOTHERMIA = new SeasonPotion(true, 0x00ffff).setPotionName("seasons:hypothermia");
	public static final Potion BURNT = new SeasonPotion(true, 0x990000).setPotionName("seasons:burnt");
	
	public static final PotionType hypothermia = new PotionType(new PotionEffect[]{new PotionEffect(HYPOTHERMIA, 900)});
	public static final PotionType hypothermia_long = new PotionType("hypothermia", new PotionEffect[]{new PotionEffect(HYPOTHERMIA, 1800)});
	public static final PotionType hypothermia_strong = new PotionType("hypothermia", new PotionEffect[]{new PotionEffect(HYPOTHERMIA, 450, 1)});
	public static final PotionType burnt = new PotionType(new PotionEffect[]{new PotionEffect(BURNT, 900)});
	public static final PotionType burnt_long = new PotionType("burnt", new PotionEffect[]{new PotionEffect(BURNT, 1800)});
	public static final PotionType burnt_strong = new PotionType("burnt", new PotionEffect[]{new PotionEffect(BURNT, 450, 1)});
	
	public SeasonPotion(boolean badEffect, int potionColor) {
		super(badEffect, potionColor);
	}
	
	public static void register() {
		GameRegistry.register(HYPOTHERMIA, new ResourceLocation("seasonsapi", "hyporthermia"));
		GameRegistry.register(BURNT, new ResourceLocation("seasonsapi", "burnt"));
		GameRegistry.register(hypothermia, new ResourceLocation("seasonsapi", "hypothermia"));
		GameRegistry.register(hypothermia_long, new ResourceLocation("seasonsapi", "long_hypothermia"));
		GameRegistry.register(hypothermia_strong, new ResourceLocation("seasonsapi", "strong_hypothermia"));
		GameRegistry.register(burnt, new ResourceLocation("seasonsapi", "burnt"));
		GameRegistry.register(burnt_long, new ResourceLocation("seasonsapi", "long_burnt"));
		GameRegistry.register(burnt_strong, new ResourceLocation("seasonsapi", "strong_burnt"));
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(PotionTypes.AWKWARD, new ItemStack(Items.SNOWBALL), hypothermia));
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(PotionTypes.AWKWARD, new ItemStack(Items.FIRE_CHARGE), burnt));
		registerUpgrades(hypothermia, hypothermia_long, hypothermia_strong);
		registerUpgrades(burnt, burnt_long, burnt_strong);
	}
	
	private static BrewingRecipe createBrewingRecipe(PotionType in, ItemStack ingredient, PotionType out) {
		return new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), in), ingredient, (PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), out)));
	}
	
	private static BrewingRecipe createSplashBrewingRecipe(PotionType in, ItemStack ingredient, PotionType out) {
		return new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), in), ingredient, (PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), out)));
	}
	
	private static BrewingRecipe createLingeringBrewingRecipe(PotionType in, ItemStack ingredient, PotionType out) {
		return new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), in), ingredient, (PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), out)));
	}
	
	private static void registerUpgrades(PotionType normal, PotionType extended, PotionType strong) {
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(normal, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(strong, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(normal, new ItemStack(Items.GLOWSTONE_DUST), strong));		
		BrewingRecipeRegistry.addRecipe(createBrewingRecipe(extended, new ItemStack(Items.GLOWSTONE_DUST), strong));	
		BrewingRecipeRegistry.addRecipe(createSplashBrewingRecipe(normal, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createSplashBrewingRecipe(strong, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createSplashBrewingRecipe(normal, new ItemStack(Items.GLOWSTONE_DUST), strong));		
		BrewingRecipeRegistry.addRecipe(createSplashBrewingRecipe(extended, new ItemStack(Items.GLOWSTONE_DUST), strong));	
		BrewingRecipeRegistry.addRecipe(createLingeringBrewingRecipe(normal, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createLingeringBrewingRecipe(strong, new ItemStack(Items.REDSTONE), extended));
		BrewingRecipeRegistry.addRecipe(createLingeringBrewingRecipe(normal, new ItemStack(Items.GLOWSTONE_DUST), strong));	
		BrewingRecipeRegistry.addRecipe(createLingeringBrewingRecipe(extended, new ItemStack(Items.GLOWSTONE_DUST), strong));	
		BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), normal), new ItemStack(Items.GUNPOWDER), (PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), normal))));
		BrewingRecipeRegistry.addRecipe(new BrewingRecipe(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), normal), new ItemStack(Items.DRAGON_BREATH), (PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), normal))));
	}
}
