package mod.mindcraft.seasons;

import mod.mindcraft.seasons.api.SeasonPotion;
import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import mod.mindcraft.seasons.api.interfaces.IBlockTemperatureRegistry;
import mod.mindcraft.seasons.commands.CommandSeason;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid="seasonsapi", version="1.0.3", acceptedMinecraftVersions="1.8.9", modLanguage="java", guiFactory="mod.mindcraft.seasons.ConfigFactory", canBeDeactivated=false)
public class Seasons {
	
	
	@Instance("seasonsapi")
	public static Seasons instance;
	
	public static final Potion HYPOTHERMIA = new SeasonPotion(new ResourceLocation("seasonsapi", "hyporthermia"), true, 0x00ffff).setPotionName("seasons:hypothermia");
	public static final Potion BURNT = new SeasonPotion(new ResourceLocation("seasonsapi", "burnt"), true, 0x990000).setPotionName("seasons:burnt");
	
	public static SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SeasonsAPI.instance.setCfg(new SeasonsCFG(e.getSuggestedConfigurationFile()));
		SeasonsAPI.instance.setWorldInterface(new WorldInterface());
		SeasonsAPI.instance.setBlockTemperatureRegistry(new BlockTemperatureRegistry());
		this.addTemperatures();
		
		MinecraftForge.EVENT_BUS.register(new WorldHandler());
	}
	
	private void addTemperatures() {
		IBlockTemperatureRegistry registry = SeasonsAPI.instance.getBlockTemperatureRegistry();
		registry.addTemperatureToBlock(Blocks.torch.getDefaultState(), 50F, true);
		registry.addTemperatureToBlock(Blocks.lit_pumpkin.getDefaultState(), 50F, true);
		registry.addTemperatureToBlock(Blocks.lit_furnace.getDefaultState(), 50F, true);
		registry.addTemperatureToBlock(Blocks.lava.getDefaultState(), 500F, true);
		registry.addTemperatureToBlock(Blocks.flowing_lava.getDefaultState(), 500F, true);
		registry.addTemperatureToBlock(Blocks.fire.getDefaultState(), 200F, true);
		registry.addTemperatureToBlock(Blocks.ice.getDefaultState(), -10F, true);
		registry.addTemperatureToBlock(Blocks.packed_ice.getDefaultState(), -20F, true);
		registry.addTemperatureToBlock(Blocks.snow.getDefaultState(), -5F, true);
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandSeason());
	}
}
