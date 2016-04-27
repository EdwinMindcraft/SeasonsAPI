package mod.mindcraft.seasons;

import java.util.HashMap;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandTime;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldHandler {
	
	public static HashMap<ChunkCoordIntPair, ChunkTemperature> tempMap = new HashMap<ChunkCoordIntPair, ChunkTemperature>();
	
	@SubscribeEvent
	public void onBlockUpdate(BlockEvent.NeighborNotifyEvent e) {
		BlockPos pos = e.pos;
		try {
			if (!SeasonsAPI.instance.getBlockTemperatureRegistry().hasTemperature(e.state))
				return;
			if (!tempMap.containsKey(new ChunkCoordIntPair((int)Math.floor((float)e.pos.getX() / 16F), (int)Math.floor((float)e.pos.getZ() / 16F))))
				return;
			for (int x = -5; x < 6; x++) {
				for (int y = -5; y < 6; y++) {
					if (y + pos.getY() < 0 || y + pos.getY() > 256)
						continue;
					for (int z = -5; z < 6; z++) {
						int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
						if (dist > 5)
							continue;
						tempMap.get(e.world.getChunkFromBlockCoords(pos).getChunkCoordIntPair()).calcBlockTemp(e.world, pos.add(x, y, z));
					}
				}
			}
		} catch (Exception ex) {}
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e) {
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setWorld(e.world);
	}
	
	@SubscribeEvent
	public void livingTick (LivingUpdateEvent e) {
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		if (e.entityLiving.isPotionActive(Seasons.HYPOTHERMIA)) {
			PotionEffect hypothermia = e.entityLiving.getActivePotionEffect(Seasons.HYPOTHERMIA);
			e.entityLiving.motionX -= e.entityLiving.motionX * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			e.entityLiving.motionZ -= e.entityLiving.motionZ * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
		}
		if (cfg.enableHardcoreTemperature) {
			ChunkTemperature temp = tempMap.get(e.entityLiving.worldObj.getChunkFromBlockCoords(e.entityLiving.getPosition()).getChunkCoordIntPair());
			if (temp != null) {
				if (temp.getTempForBlock(e.entityLiving.getPosition()) < cfg.hypothermiaStart) {
					int hypothermiaLevel = (int) Math.floor(Math.abs(temp.getTempForBlock(e.entityLiving.getPosition())) / (float)cfg.hypothermiaLevelDiff);
					e.entityLiving.addPotionEffect(new PotionEffect(Seasons.HYPOTHERMIA.id, 201, hypothermiaLevel));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload e) {
		System.out.println("Unloading " + tempMap.size() + " chunks");
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).removeWorld();
	}
	
	@SubscribeEvent
	public void onCommand(CommandEvent e) {
		if (!SeasonsAPI.instance.getCfg().enableTotalTimeSet)
			return;
        try {
			if (e.command != null && e.command instanceof CommandTime) {
				if (e.parameters.length > 1 && e.parameters[0].equals("set")) {
	                int l;
	
	                if (e.parameters[1].equals("day"))
	                {
	                    l = 1000;
	                }
	                else if (e.parameters[1].equals("night"))
	                {
	                    l = 13000;
	                }
	                else
	                {
						l = CommandBase.parseInt(e.parameters[1], 0);
	                }
	                for (int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i)
	                {
	                    MinecraftServer.getServer().worldServers[i].setTotalWorldTime((long)l);
	                }
				}
				else if (e.parameters.length > 1 && e.parameters[0].equals("add")) {
	                int l;
	
	                if (e.parameters[1].equals("day"))
	                {
	                    l = 1000;
	                }
	                else if (e.parameters[1].equals("night"))
	                {
	                    l = 13000;
	                }
	                else
	                {
						l = CommandBase.parseInt(e.parameters[1], 0);
	                }
	                for (int i = 0; i < MinecraftServer.getServer().worldServers.length; ++i)
	                {
	                    WorldServer worldserver = MinecraftServer.getServer().worldServers[i];
	                    worldserver.setTotalWorldTime(worldserver.getTotalWorldTime() + (long)l);
	                }
				}
			}
		} catch (NumberInvalidException e1) {
		}

	}
}
