package mod.mindcraft.seasons.commands;

import java.util.List;

import com.google.common.collect.Lists;

import mod.mindcraft.seasons.Seasons;
import mod.mindcraft.seasons.WorldHandler;
import mod.mindcraft.seasons.api.init.SeasonsAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

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
		if (args.length > 0 && args[0].equalsIgnoreCase("values")) {
			SeasonsAPI.instance.getBlockTemperatureRegistry().clear();
			Seasons.instance.addTemperatures();
			Seasons.tempReader.readTemperaturesFromFile();
			notifyOperators(sender, this, "Reloaded Temperature Values");
			return;
		}
		if (args.length > 0 && args[0].equalsIgnoreCase("map")) {
			WorldHandler.tempMap.clear();
			notifyOperators(sender, this, "Reloaded Temperature Map, your game may lag a bit");
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"values", "map"}) : Lists.<String>newArrayList();
	}

}
