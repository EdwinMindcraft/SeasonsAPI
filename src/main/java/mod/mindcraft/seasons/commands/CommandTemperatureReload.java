package mod.mindcraft.seasons.commands;

import mod.mindcraft.seasons.Seasons;
import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandTemperatureReload extends CommandBase {

	@Override
	public String getCommandName() {
		return "reloadtemperature";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "reloadtemperature";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		SeasonsAPI.instance.getBlockTemperatureRegistry().clear();
		Seasons.tempReader.readTemperaturesFromFile();
		Seasons.instance.addTemperatures();
	}

}
