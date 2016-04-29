package mod.mindcraft.seasons.commands;

import java.util.List;

import com.google.common.collect.Lists;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

public class CommandSeason extends CommandBase {

	@Override
	public String getCommandName() {
		return "seasons";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.seasons.usage";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spring")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 0.5 + (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 4 * parseInt(args[1], 0))));
				notifyOperators(sender, this, "commands.seasons.spring", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("summer")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 1.5 + (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 4 * parseInt(args[1], 0))));
				notifyOperators(sender, this, "commands.seasons.summer", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("autumn")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 2.5 + (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 4 * parseInt(args[1], 0))));
				notifyOperators(sender, this, "commands.seasons.autumn", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("winter")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 3.5 + (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 4 * parseInt(args[1], 0))));
				notifyOperators(sender, this, "commands.seasons.winter", parseInt(args[1], 0));
				return;
			}
		}
		throw new WrongUsageException("commands.seasons.usage", new Object[0]);
	}
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"spring", "summer", "autumn", "winter"}) : Lists.<String>newArrayList();
	}

}
