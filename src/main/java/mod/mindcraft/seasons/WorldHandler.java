package mod.mindcraft.seasons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import mod.mindcraft.seasons.api.DamageSources;
import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandTime;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
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
	public void debugInfo(RenderGameOverlayEvent.Text e) {
		float temp = (float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getTemperature(Minecraft.getMinecraft().thePlayer.getPosition()) * 100) / 100);
		if (SeasonsAPI.instance.getCfg().enableTempDebug && Minecraft.getMinecraft().gameSettings.showDebugInfo)
			e.left.add("\u00a7c[SAPI]\u00a7r" + (temp < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "Temperature : " + temp + " C");
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e) {
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setWorld(e.world);
	}
	
	@SubscribeEvent
	public void onConfigChanged (ConfigChangedEvent e) {
		if (!e.modID.equals("seasonsapi"))
			return;
		System.out.println("CFG");
		SeasonsCFG cfg = SeasonsAPI.instance.cfg;
		ArrayList<String> propOrder = new ArrayList<String>();
		Property prop = cfg.get("seasons", "Season Lenght", 7);
		propOrder.add(prop.getName());
		cfg.setCategoryPropertyOrder("seasons", propOrder);
		propOrder = new ArrayList<String>();
		prop = cfg.get("advanced", "Enable Debug", false);
		propOrder.add(prop.getName());
		cfg.setCategoryPropertyOrder("advanced", propOrder);
		cfg.save();
		cfg.reload();
		SeasonsAPI.instance.cfg = cfg;
	}
	
	@SubscribeEvent
	public void livingTick (LivingUpdateEvent e) {
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		if (e.entityLiving.isPotionActive(Seasons.HYPOTHERMIA)) {
			PotionEffect hypothermia = e.entityLiving.getActivePotionEffect(Seasons.HYPOTHERMIA);
			e.entityLiving.motionX -= e.entityLiving.motionX * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			e.entityLiving.motionZ -= e.entityLiving.motionZ * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			if (hypothermia.getAmplifier() > 0 && e.entityLiving.ticksExisted % 20 == 0)
				e.entityLiving.attackEntityFrom(DamageSources.hypothermia, hypothermia.getAmplifier());
		}
		if (e.entityLiving.isPotionActive(Seasons.BURNT)) {
			PotionEffect burnt = e.entityLiving.getActivePotionEffect(Seasons.BURNT);
			Random rand = new Random();
			if (rand.nextFloat() < (0.01F * (burnt.getAmplifier() + 1)))
				e.entityLiving.setFire(2 + burnt.getAmplifier());
			if (burnt.getAmplifier() > 0 && e.entityLiving.ticksExisted % 20 == 0)
				e.entityLiving.attackEntityFrom(DamageSources.burnt, burnt.getAmplifier());
		}

		if (cfg.enableHardcoreTemperature) {
			ChunkTemperature temp = tempMap.get(e.entityLiving.worldObj.getChunkFromBlockCoords(e.entityLiving.getPosition()).getChunkCoordIntPair());
			try {
				if (e.entityLiving instanceof EntityPlayer && e.entityLiving.ticksExisted % 10 == 0) {
					tempMap.get(e.entityLiving.worldObj.getChunkFromBlockCoords(e.entityLiving.getPosition()).getChunkCoordIntPair()).calcBlockTemp(e.entityLiving.worldObj, e.entityLiving.getPosition());
				}
			} catch (Exception ex) {}
			if (temp != null) {
				if (temp.getTempForBlock(e.entityLiving.getPosition()) < cfg.hypothermiaStart) {
					int hypothermiaLevel = (int) Math.floor(Math.abs(temp.getTempForBlock(e.entityLiving.getPosition()) - cfg.hypothermiaStart) / (float)cfg.hypothermiaLevelDiff);
					e.entityLiving.removePotionEffect(Seasons.HYPOTHERMIA.id);
					e.entityLiving.addPotionEffect(new PotionEffect(Seasons.HYPOTHERMIA.id, 201, hypothermiaLevel, true, false));
				} else if (temp.getTempForBlock(e.entityLiving.getPosition()) > cfg.burntStart) {
					int burntLevel = (int) Math.floor(Math.abs(temp.getTempForBlock(e.entityLiving.getPosition()) - cfg.burntStart) / (float)cfg.burntDiff);
					e.entityLiving.removePotionEffect(Seasons.BURNT.id);
					e.entityLiving.addPotionEffect(new PotionEffect(Seasons.BURNT.id, 200, burntLevel));
				} else {
					PotionEffect hypothermia = e.entityLiving.getActivePotionEffect(Seasons.HYPOTHERMIA);
					PotionEffect burnt = e.entityLiving.getActivePotionEffect(Seasons.BURNT);
					if (hypothermia != null && hypothermia.getIsAmbient())
						e.entityLiving.removePotionEffect(Seasons.HYPOTHERMIA.id);
					if (burnt != null && e.entityLiving.isInWater())
						e.entityLiving.removePotionEffect(Seasons.BURNT.id);
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
