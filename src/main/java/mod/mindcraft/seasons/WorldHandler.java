package mod.mindcraft.seasons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import mod.mindcraft.seasons.api.event.CropsUpdateEvent;
import mod.mindcraft.seasons.api.init.SeasonPotion;
import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.init.SeasonsCFG;
import mod.mindcraft.seasons.api.init.SeasonsCFG.ScreenCoordinates;
import mod.mindcraft.seasons.api.interfaces.ITemperatureUpdater;
import mod.mindcraft.seasons.api.utils.DamageSources;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldHandler {
	
	public static HashMap<ChunkCoordIntPair, ChunkTemperature> tempMap = new HashMap<ChunkCoordIntPair, ChunkTemperature>();
	
	@SubscribeEvent
	public void onBlockUpdate(BlockEvent.NeighborNotifyEvent e) {
		if (!Seasons.enabled)
			return;
		try {
			if (e.getState() == null)
				return;
			if ((e.getState().getBlock() instanceof ITemperatureUpdater ? !((ITemperatureUpdater)e.getState().getBlock()).requiresTemperatureUpdate(e.getWorld(), e.getState(), e.getPos()): !SeasonsAPI.instance.getBlockTemperatureRegistry().hasTemperature(e.getState())))
				return;
			if (!tempMap.containsKey(e.getWorld().getChunkFromBlockCoords(e.getPos()).getChunkCoordIntPair()))
				return;
			tempMap.get(e.getWorld().getChunkFromBlockCoords(e.getPos()).getChunkCoordIntPair()).calcBlockTemp(e.getWorld(), e.getPos());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public void onCropsUpdate(CropsUpdateEvent e) {
		if (!Seasons.enabled)
			return;
		if (e.getWorld().getLightFromNeighbors(e.getPos().up()) >= 9) {
			Method getAge = ReflectionHelper.findMethod(BlockCrops.class, (BlockCrops)e.state.getBlock(), new String[] {"func_185524_e", "getAge"});
			try {
			PropertyInteger age = (PropertyInteger) getAge.invoke((BlockCrops)e.state.getBlock());
				int i = e.state.getValue(age).intValue();
				if (age.getAllowedValues().contains(i + 1)) {
					Random rand = new Random();
					float multiplier = 0;
					switch (SeasonsAPI.instance.getWorldInterface().getSeason()) {
					case SPRING:
						multiplier = SeasonsAPI.instance.getCfg().spring.growthMultiplier;
						break;
					case SUMMER:
						multiplier = SeasonsAPI.instance.getCfg().summer.growthMultiplier;					
						break;
					case AUTUMN:
						multiplier = SeasonsAPI.instance.getCfg().autumn.growthMultiplier;					
						break;
					case WINTER:
						multiplier = SeasonsAPI.instance.getCfg().winter.growthMultiplier;					
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
							if (rand.nextFloat() <= Math.abs(multiplier) && rand.nextBoolean())
								bonusStage--;
						}
					}
					if (bonusStage == 0 || !age.getAllowedValues().contains(i + bonusStage))
						return;
					e.state = e.state.withProperty(age, i + bonusStage);
					e.getWorld().setBlockState(e.getPos(), e.state);
				}
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void debugInfo(RenderGameOverlayEvent.Text e) {
		if (!Seasons.enabled)
			return;
		if (SeasonsAPI.instance.getCfg().enableTempDebug && Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			e.getLeft().add("\u00a7c[SAPI]\u00a7rChunks in temperature map : " + tempMap.size());
		}
		//System.out.println(Minecraft.getMinecraft().theWorld.getBiomeGenForCoords(Minecraft.getMinecraft().thePlayer.getPosition()).getFloatTemperature(Minecraft.getMinecraft().thePlayer.getPosition()));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void drawOverlay(TickEvent.RenderTickEvent e) {
		if (!Seasons.enabled)
			return;
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer p = mc.thePlayer;
		if (mc.thePlayer == null || mc.theWorld == null || !mc.inGameHasFocus || ((WorldInterface)SeasonsAPI.instance.getWorldInterface()).getWorld() == null)
			return;
		ScreenCoordinates extCoord = SeasonsAPI.instance.cfg.extTempCoord;
		ScreenCoordinates intCoord = SeasonsAPI.instance.cfg.intTempCoord;
		ScreenCoordinates seasonCoord = SeasonsAPI.instance.cfg.seasonCoord;
		ScreenCoordinates timeCoord = SeasonsAPI.instance.cfg.timeCoord;
		float w = mc.displayWidth / 2;
		float h = mc.displayHeight / 2;
		if (extCoord.display) {
			float temp = (float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getTemperature(new BlockPos(p.posX, p.posY, p.posZ)) * 100) / 100);
			String color = (temp < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : ""));
			String display = "External Temperature : " + temp;
			int offset = extCoord.invert ? mc.fontRendererObj.getStringWidth(display) : 0;
			mc.fontRendererObj.drawString(color + display, (int)(extCoord.x * w) - offset, (int)(extCoord.y * h), 0xffffff);			
		}
		if (intCoord.display) {
			float temp2 =(float) (Math.floor(SeasonsAPI.instance.getWorldInterface().getInternalTemperature(p) * 100) / 100);
			String intColor = (temp2 < SeasonsAPI.instance.cfg.hypothermiaStart ? "\u00a7b" : (temp2 > SeasonsAPI.instance.cfg.burntStart ? "\u00a74" : ""));
			String display = "Internal Temperature : " + temp2;
			int offset = intCoord.invert ? mc.fontRendererObj.getStringWidth(display) : 0;
			mc.fontRendererObj.drawString(intColor + display, (int)(intCoord.x * w) - offset, (int)(intCoord.y * h), 0xffffff);
		}
		if (seasonCoord.display) {
			long seasonLenght = 24000 * SeasonsAPI.instance.getCfg().seasonLenght;
			String display = "Season : " + StringUtils.capitalize(SeasonsAPI.instance.getWorldInterface().getSeason().name().toLowerCase()) + " (" + (Math.floor((float)(Minecraft.getMinecraft().theWorld.getWorldTime() % seasonLenght) * 1000F / (float)seasonLenght) / 10F) + "%)";
			int offset = seasonCoord.invert ? mc.fontRendererObj.getStringWidth(display) : 0;
			mc.fontRendererObj.drawString(display, (int)(seasonCoord.x * w) - offset, (int)(seasonCoord.y * h), 0xffffff);
		}
		if (timeCoord.display) {
			long seasonLenght = 24000 * SeasonsAPI.instance.getCfg().seasonLenght;
			long year = (long) Math.floor((float)(((WorldInterface)SeasonsAPI.instance.getWorldInterface()).getWorld().getWorldTime() / ((float)seasonLenght * 4)));
			long yearTime = (long) Math.floor(((float)((WorldInterface)SeasonsAPI.instance.getWorldInterface()).getWorld().getWorldTime() % ((float)seasonLenght * 4)) / 24000F);
			String display = "Year " + year + " Day " + yearTime;
			int offset = timeCoord.invert ? mc.fontRendererObj.getStringWidth(display) : 0;
			mc.fontRendererObj.drawString(display, (int)(timeCoord.x * w) - offset, (int)(timeCoord.y * h), 0xffffff);
		}
	}
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load e) {
		((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setWorld(e.getWorld());
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void worldTick(TickEvent.WorldTickEvent e) {
//		if (!Seasons.enabled)
//			return;
		float rainfall = SeasonsAPI.instance.cfg.getRainFall(SeasonsAPI.instance.getWorldInterface().getSeason());
		if (e.world.getWorldInfo().isRaining()) {
			//System.out.println("MEH");
			e.world.setRainStrength(rainfall);
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
		if (!Seasons.enabled)
			return;
		BlockPos pos = new BlockPos(e.getEntityLiving().posX, e.getEntityLiving().posY, e.getEntityLiving().posZ);
		SeasonsCFG cfg = SeasonsAPI.instance.getCfg();
		World world = e.getEntityLiving().worldObj;
		if (e.getEntityLiving().isPotionActive(SeasonPotion.HYPOTHERMIA)) {
			PotionEffect hypothermia = e.getEntityLiving().getActivePotionEffect(SeasonPotion.HYPOTHERMIA);
			e.getEntityLiving().motionX -= e.getEntityLiving().motionX * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			e.getEntityLiving().motionZ -= e.getEntityLiving().motionZ * (((double)hypothermia.getAmplifier() + 1D) * 0.10);
			if (hypothermia.getAmplifier() > 0 && e.getEntityLiving().ticksExisted % 20 == 0)
				e.getEntityLiving().attackEntityFrom(DamageSources.hypothermia, hypothermia.getAmplifier());
		}
		if (e.getEntityLiving().isPotionActive(SeasonPotion.BURNT)) {
			PotionEffect burnt = e.getEntityLiving().getActivePotionEffect(SeasonPotion.BURNT);
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
					player.removePotionEffect(SeasonPotion.HYPOTHERMIA);
					player.addPotionEffect(new PotionEffect(SeasonPotion.HYPOTHERMIA, 201, hypothermiaLevel, true, false));
					if (player.isPotionActive(SeasonPotion.BURNT))
						player.removePotionEffect(SeasonPotion.BURNT);
				} else if (temp > cfg.burntStart) {
					int burntLevel = (int) Math.floor(Math.abs(temp - cfg.burntStart) / (float)cfg.burntDiff);
					player.removePotionEffect(SeasonPotion.BURNT);
					player.addPotionEffect(new PotionEffect(SeasonPotion.BURNT, 200, burntLevel));
				} else {
					PotionEffect hypothermia = player.getActivePotionEffect(SeasonPotion.HYPOTHERMIA);
					PotionEffect burnt = player.getActivePotionEffect(SeasonPotion.BURNT);
					if (hypothermia != null && hypothermia.getIsAmbient())
						player.removePotionEffect(SeasonPotion.HYPOTHERMIA);
					if (burnt != null && player.isWet())
						player.removePotionEffect(SeasonPotion.BURNT);
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
	public void clientConnect (ClientConnectedToServerEvent e) {
		if (e.getConnectionType().equalsIgnoreCase("vanilla")) {
			Seasons.enabled = false;
			return;
		} else
			Seasons.enabled = true;
	}
}
