package mod.mindcraft.seasons;

import java.io.File;

import mod.mindcraft.seasons.api.init.SeasonPotion;
import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.init.SeasonsCFG;
import mod.mindcraft.seasons.api.interfaces.IBlockTemperatureRegistry;
import mod.mindcraft.seasons.colorizer.LeavesGrassUtils;
import mod.mindcraft.seasons.commands.CommandSeason;
import mod.mindcraft.seasons.commands.CommandTemperatureReload;
import mod.mindcraft.seasons.custom.CustomTemperatureReader;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid="seasonsapi", version="1.1.3b", acceptedMinecraftVersions="1.9", modLanguage="java", guiFactory="mod.mindcraft.seasons.ConfigFactory", canBeDeactivated=false)
public class Seasons {
	
	
	@Instance("seasonsapi")
	public static Seasons instance;
	
	public static CustomTemperatureReader tempReader;
	
	public static boolean enabled = true;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		SeasonsAPI.instance.setCfg(new SeasonsCFG(e.getSuggestedConfigurationFile()));
		SeasonsAPI.instance.setWorldInterface(new WorldInterface());
		SeasonsAPI.instance.setBlockTemperatureRegistry(new BlockTemperatureRegistry());
		this.addTemperatures();
		tempReader = new CustomTemperatureReader(new File(e.getModConfigurationDirectory() + "\\SeasonsAPI\\CustomTemperature.txt"));
		MinecraftForge.EVENT_BUS.register(new WorldHandler());
		SeasonPotion.register();
	}
	
	public void addTemperatures() {
		IBlockTemperatureRegistry registry = SeasonsAPI.instance.getBlockTemperatureRegistry();
		registry.addTemperatureToBlock(Blocks.TORCH.getDefaultState(), 50F, true, false);
		registry.addTemperatureToBlock(Blocks.LIT_PUMPKIN.getDefaultState(), 50F, true, false);
		registry.addTemperatureToBlock(Blocks.LIT_FURNACE.getDefaultState(), 50F, true, false);
		registry.addTemperatureToBlock(Blocks.LAVA.getDefaultState(), 500F, true, false);
		registry.addTemperatureToBlock(Blocks.FLOWING_LAVA.getDefaultState(), 500F, true, false);
		registry.addTemperatureToBlock(Blocks.FIRE.getDefaultState(), 200F, true, false);
		registry.addTemperatureToBlock(Blocks.ICE.getDefaultState(), -10F, true, false);
		registry.addTemperatureToBlock(Blocks.PACKED_ICE.getDefaultState(), -20F, true, false);
		registry.addTemperatureToBlock(Blocks.SNOW.getDefaultState(), -5F, true, false);
		registry.addTemperatureToBlock(Blocks.WATER.getDefaultState(), 20F, true, false);
		registry.addTemperatureToBlock(Blocks.FLOWING_WATER.getDefaultState(), 20F, true, false);
	}
	
	@EventHandler
	public void postInit (FMLPostInitializationEvent e) {
		tempReader.readTemperaturesFromFile();
		if (e.getSide() == Side.CLIENT)
			LeavesGrassUtils.registerColors();
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		e.registerServerCommand(new CommandSeason());
		e.registerServerCommand(new CommandTemperatureReload());
	}
}
