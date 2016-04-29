package mod.mindcraft.seasons;

import java.util.HashMap;
import java.util.Random;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import mod.mindcraft.seasons.api.interfaces.ITemperatureUpdater;
import mod.mindcraft.seasons.api.utils.DamageSources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.StringUtils;

public class WorldHandler {
	
	public static HashMap<ChunkCoordIntPair, ChunkTemperature> tempMap = new HashMap<ChunkCoordIntPair, ChunkTemperature>();
	
	@SubscribeEvent
	public void onBlockUpdate(BlockEvent.NeighborNotifyEvent e) {
		BlockPos pos = e.pos;
		try {
			if ((e.state.getBlock() instanceof ITemperatureUpdater ? !SeasonsAPI.instance.getBlockTemperatureRegistry().hasTemperature(e.state) : !((ITemperatureUpdater)e.state.getBlock()).requiresTemperatureUpdate(e.world, e.state, e.pos)))
				return;
			if (!tempMap.containsKey(new ChunkCoordIntPair((int)Math.floor((float)e.pos.getX() / 16F), (int)Math.floor((float)e.pos.getZ() / 16F))))
				return;

			tempMap.get(e.world.getChunkFromBlockCoords(pos).getChunkCoordIntPair()).calcBlockTemp(e.world, pos);
		} catch (Exception ex) {}
	}
	
	@SubscribeEvent
	public void debugInfo(RenderGameOverlayEvent.Text e) {
		if (SeasonsAPI.instance.getCfg().enableTempDebug && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			float temp = (float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getTemperature(Minecraft.getMinecraft().thePlayer.getPosition()) * 100) / 100);
			e.left.add("\u00a7c[SAPI]\u00a7r" + (temp < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "Temperature : " + temp + " C");
			e.left.add("\u00a7c[SAPI]\u00a7rChunks in temperature map : " + tempMap.size());
			long seasonLenght = 24000 * SeasonsAPI.instance.getCfg().seasonLenght;
			e.left.add("\u00a7c[SAPI]\u00a7rSeason : " + StringUtils.capitalize(SeasonsAPI.instance.getWorldInterface().getSeason().name().toLowerCase()) + " (" + (Math.floor((float)(Minecraft.getMinecraft().theWorld.getWorldTime() % seasonLenght) * 1000F / (float)seasonLenght) / 10F) + "%)");
		}
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e) {
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setWorld(e.world);
	}
	
	@SubscribeEvent
	public void onConfigChanged (ConfigChangedEvent e) {
		if (!e.modID.equals("seasonsapi"))
			return;
		SeasonsCFG cfg = SeasonsAPI.instance.cfg;
		cfg.save();
		cfg.reload();
		SeasonsAPI.instance.cfg = cfg;
	}
	
	@SubscribeEvent
	public void livingTick (LivingUpdateEvent e) {
		BlockPos pos = e.entityLiving.getPosition();
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		World world = e.entityLiving.worldObj;
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
		
		if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.entityLiving;
			if (cfg.enableHardcoreTemperature) {
				try {
					tempMap.get(world.getChunkFromBlockCoords(pos).getChunkCoordIntPair()).calcBlockTemp(world, pos);
				} catch (Exception ex) {}
				float temp = SeasonsAPI.instance.getWorldInterface().getTemperature(pos);
				if (temp < cfg.hypothermiaStart) {
					int hypothermiaLevel = (int) Math.floor(Math.abs(temp - cfg.hypothermiaStart) / (float)cfg.hypothermiaLevelDiff);
					player.removePotionEffect(Seasons.HYPOTHERMIA.id);
					player.addPotionEffect(new PotionEffect(Seasons.HYPOTHERMIA.id, 201, hypothermiaLevel, true, false));
					if (player.isPotionActive(Seasons.BURNT.id))
						player.removePotionEffect(Seasons.BURNT.id);
				} else if (temp > cfg.burntStart) {
					int burntLevel = (int) Math.floor(Math.abs(temp - cfg.burntStart) / (float)cfg.burntDiff);
					player.removePotionEffect(Seasons.BURNT.id);
					player.addPotionEffect(new PotionEffect(Seasons.BURNT.id, 200, burntLevel));
				} else {
					PotionEffect hypothermia = player.getActivePotionEffect(Seasons.HYPOTHERMIA);
					PotionEffect burnt = player.getActivePotionEffect(Seasons.BURNT);
					if (hypothermia != null && hypothermia.getIsAmbient())
						player.removePotionEffect(Seasons.HYPOTHERMIA.id);
					if (burnt != null && player.isWet())
						player.removePotionEffect(Seasons.BURNT.id);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void worldUnload(WorldEvent.Unload e) {
		System.out.println("Unloading " + tempMap.size() + " chunks");
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).removeWorld();
	}
}
