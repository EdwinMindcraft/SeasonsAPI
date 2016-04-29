package mod.mindcraft.seasons.commands;

import java.util.List;

import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import com.google.common.collect.Lists;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 2) {
			long yearTime = (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 4 * parseInt(args[1], 0));
			float halfSeason = (float)SeasonsAPI.instance.getCfg().seasonLenght / 2F;
			long timeToRemove = SeasonsAPI.instance.getCfg().morningSeasonSet ? (long)((halfSeason - Math.floor(halfSeason)) * 24000) : 0;
			System.out.println();
			if (args[0].equalsIgnoreCase("spring")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 0.5 + yearTime - timeToRemove));
				notifyOperators(sender, this, "commands.seasons.spring", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("summer")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 1.5 + yearTime - timeToRemove));
				notifyOperators(sender, this, "commands.seasons.summer", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("autumn")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 2.5 + yearTime - timeToRemove));
				notifyOperators(sender, this, "commands.seasons.autumn", parseInt(args[1], 0));
				return;
			}
			else if (args[0].equalsIgnoreCase("winter")) {
				sender.getEntityWorld().setWorldTime((long) (SeasonsAPI.instance.getCfg().seasonLenght * 24000 * 3.5 + yearTime - timeToRemove));
				notifyOperators(sender, this, "commands.seasons.winter", parseInt(args[1], 0));
				return;
			}
		}
		throw new WrongUsageException("commands.seasons.usage", new Object[0]);
	}
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"spring", "summer", "autumn", "winter"}) : Lists.<String>newArrayList();
	}

}
