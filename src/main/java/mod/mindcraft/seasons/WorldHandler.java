package mod.mindcraft.seasons;

import java.util.HashMap;
import java.util.Random;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.SeasonsCFG;
import mod.mindcraft.seasons.api.event.CropsUpdateEvent;
import mod.mindcraft.seasons.api.interfaces.ITemperatureUpdater;
import mod.mindcraft.seasons.api.utils.DamageSources;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.apache.commons.lang3.StringUtils;

public class WorldHandler {
	
	public static HashMap<ChunkCoordIntPair, ChunkTemperature> tempMap = new HashMap<ChunkCoordIntPair, ChunkTemperature>();
	
	@SubscribeEvent
	public void onBlockUpdate(BlockEvent.NeighborNotifyEvent e) {
		BlockPos pos = e.getPos();
		try {
			if ((e.getState().getBlock() instanceof ITemperatureUpdater ? !SeasonsAPI.instance.getBlockTemperatureRegistry().hasTemperature(e.getState()) : !((ITemperatureUpdater)e.getState().getBlock()).requiresTemperatureUpdate(e.getWorld(), e.getState(), e.getPos())))
				return;
			if (!tempMap.containsKey(new ChunkCoordIntPair((int)Math.floor((float)e.getPos().getX() / 16F), (int)Math.floor((float)e.getPos().getZ() / 16F))))
				return;

			tempMap.get(e.getWorld().getChunkFromBlockCoords(pos).getChunkCoordIntPair()).calcBlockTemp(e.getWorld(), pos);
		} catch (Exception ex) {}
	}
	
	@SubscribeEvent
	public void onCropsUpdate(CropsUpdateEvent e) {
		if (e.getWorld().getLightFromNeighbors(e.getPos().up()) >= 9) {
			int i = ((Integer)e.state.getValue(BlockCrops.AGE)).intValue();
			if (i < 7) {
				Random rand = new Random();
				float multiplier = 0;
				switch (SeasonsAPI.instance.getWorldInterface().getSeason()) {
				case SPRING:
					multiplier = SeasonsAPI.instance.getCfg().springGrowthMultiplier;
					break;
				case SUMMER:
					multiplier = SeasonsAPI.instance.getCfg().summerGrowthMultiplier;					
					break;
				case AUTUMN:
					multiplier = SeasonsAPI.instance.getCfg().autumnGrowthMultiplier;					
					break;
				case WINTER:
					multiplier = SeasonsAPI.instance.getCfg().winterGrowthMultiplier;					
					break;					
				default:
					break;
				}
				
				int bonusStage = 0;
				if (multiplier > 0) {
					for (;multiplier > 0; multiplier--) {
						if (rand.nextFloat() <= multiplier)
							bonusStage++;
					}
				}
				else {
					for (;multiplier < 0; multiplier++) {
						if (rand.nextFloat() <= Math.abs(multiplier))
							bonusStage--;
					}					
				}
				i = MathHelper.clamp_int(i + bonusStage, 0, 7);
				e.state = e.state.withProperty(BlockCrops.AGE, i);
				e.getWorld().setBlockState(e.getPos(), e.state);
			}
		}
	}
	
	@SubscribeEvent
	public void debugInfo(RenderGameOverlayEvent.Text e) {
		if (SeasonsAPI.instance.getCfg().enableTempDebug && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			e.getLeft().add("\u00a7c[SAPI]\u00a7rChunks in temperature map : " + tempMap.size());
		}
		if (SeasonsAPI.instance.getCfg().seasonAlwaysVisible) {
			long seasonLenght = 24000 * SeasonsAPI.instance.getCfg().seasonLenght;
			long year = (long) Math.floor((float)(((WorldInterface)SeasonsAPI.instance.getWorldInterface()).getWorld().getWorldTime() / ((float)seasonLenght * 4)));
			long yearTime = (long) Math.floor(((float)((WorldInterface)SeasonsAPI.instance.getWorldInterface()).getWorld().getWorldTime() % ((float)seasonLenght * 4)) / 24000F);
			if (SeasonsAPI.instance.getCfg().displayTemperatureRight) {
				e.getRight().add("Season : " + StringUtils.capitalize(SeasonsAPI.instance.getWorldInterface().getSeason().name().toLowerCase()) + " (" + (Math.floor((float)(Minecraft.getMinecraft().theWorld.getWorldTime() % seasonLenght) * 1000F / (float)seasonLenght) / 10F) + "%)");
				e.getRight().add("Year " + year + " Day " + yearTime);								
			} else {
				e.getLeft().add("Season : " + StringUtils.capitalize(SeasonsAPI.instance.getWorldInterface().getSeason().name().toLowerCase()) + " (" + (Math.floor((float)(Minecraft.getMinecraft().theWorld.getWorldTime() % seasonLenght) * 1000F / (float)seasonLenght) / 10F) + "%)");
				e.getLeft().add("Year " + year + " Day " + yearTime);				
			}
		}
		if (SeasonsAPI.instance.getCfg().displayTemperature) {
			EntityPlayer p = Minecraft.getMinecraft().thePlayer;
			float temp = (float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getTemperature(new BlockPos(p.posX, p.posY, p.posZ)) * 100) / 100);
			float temp2 =(float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getInternalTemperature(p) * 100) / 100);
			if (SeasonsAPI.instance.getCfg().displayTemperatureRight) {
				e.getRight().add((temp < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "External Temperature : " + temp + " C");
				e.getRight().add((temp2 < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp2 > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "Internal Temperature : " + temp2 + " C");				
			} else {
				e.getLeft().add((temp < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "External Temperature : " + temp + " C");
				e.getLeft().add((temp2 < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp2 > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : "")) + "Internal Temperature : " + temp2 + " C");				
			}
		}

	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e) {
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setWorld(e.getWorld());
	}
	
	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent e) {
		if (e.world.getWorldInfo().isRaining()) {
			e.world.setRainStrength(SeasonsAPI.instance.cfg.getRainFall(SeasonsAPI.instance.getWorldInterface().getSeason()));
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged (ConfigChangedEvent e) {
		if (!e.getModID().equals("seasonsapi"))
			return;
		SeasonsCFG cfg = SeasonsAPI.instance.cfg;
		cfg.save();
		cfg.reload();
		SeasonsAPI.instance.cfg = cfg;
	}
	
	@SubscribeEvent
	public void livingTick (LivingUpdateEvent e) {
		BlockPos pos = new BlockPos(e.getEntityLiving().posX, e.getEntityLiving().posY, e.getEntityLiving().posZ);
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		World world = e.getEntityLiving().worldObj;
		if (e.getEntityLiving().isPotionActive(Seasons.HYPOTHERMIA)) {
			PotionEffect hypothermia = e.getEntityLiving().getActivePotionEffect(Seasons.HYPOTHERMIA);
			e.getEntityLiving().motionX -= e.getEntityLiving().motionX * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			e.getEntityLiving().motionZ -= e.getEntityLiving().motionZ * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			if (hypothermia.getAmplifier() > 0 && e.getEntityLiving().ticksExisted % 20 == 0)
				e.getEntityLiving().attackEntityFrom(DamageSources.hypothermia, hypothermia.getAmplifier());
		}
		if (e.getEntityLiving().isPotionActive(Seasons.BURNT)) {
			PotionEffect burnt = e.getEntityLiving().getActivePotionEffect(Seasons.BURNT);
			Random rand = new Random();
			if (rand.nextFloat() < (0.01F * (burnt.getAmplifier() + 1)))
				e.getEntityLiving().setFire(2 + burnt.getAmplifier());
			if (burnt.getAmplifier() > 0 && e.getEntityLiving().ticksExisted % 20 == 0)
				e.getEntityLiving().attackEntityFrom(DamageSources.burnt, burnt.getAmplifier());
		}
		
		if (e.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.getEntityLiving();
			if (cfg.enableHardcoreTemperature) {
				try {
					tempMap.get(world.getChunkFromBlockCoords(pos).getChunkCoordIntPair()).calcBlockTemp(world, pos);
				} catch (Exception ex) {}
				float temp = SeasonsAPI.instance.getWorldInterface().getInternalTemperature(player);
				if (temp < cfg.hypothermiaStart) {
					int hypothermiaLevel = (int) Math.floor(Math.abs(temp - cfg.hypothermiaStart) / (float)cfg.hypothermiaLevelDiff);
					player.removePotionEffect(Seasons.HYPOTHERMIA);
					player.addPotionEffect(new PotionEffect(Seasons.HYPOTHERMIA, 201, hypothermiaLevel, true, false));
					if (player.isPotionActive(Seasons.BURNT))
						player.removePotionEffect(Seasons.BURNT);
				} else if (temp > cfg.burntStart) {
					int burntLevel = (int) Math.floor(Math.abs(temp - cfg.burntStart) / (float)cfg.burntDiff);
					player.removePotionEffect(Seasons.BURNT);
					player.addPotionEffect(new PotionEffect(Seasons.BURNT, 200, burntLevel));
				} else {
					PotionEffect hypothermia = player.getActivePotionEffect(Seasons.HYPOTHERMIA);
					PotionEffect burnt = player.getActivePotionEffect(Seasons.BURNT);
					if (hypothermia != null && hypothermia.getIsAmbient())
						player.removePotionEffect(Seasons.HYPOTHERMIA);
					if (burnt != null && player.isWet())
						player.removePotionEffect(Seasons.BURNT);
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
