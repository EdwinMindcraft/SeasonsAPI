package mod.mindcraft.seasons;

import java.util.HashMap;

import mod.mindcraft.seasons.api.SeasonsAPI;
import mod.mindcraft.seasons.api.enums.EnumSeason;
import mod.mindcraft.seasons.api.interfaces.ITemperatureModifier;
import mod.mindcraft.seasons.api.interfaces.IWorldInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldInterface implements IWorldInterface {
	
	private World worldObj;

	public void setWorld(World world) {
		this.worldObj = world;
	}

	public void removeWorld() {
		setWorld(null);
		WorldHandler.tempMap.clear();
	}
	
	public void setTemp(BlockPos pos, float temp) {
		WorldHandler.tempMap.get(getWorld().getChunkFromBlockCoords(pos).getChunkCoordIntPair()).addBlockTemp(pos, temp);
	}
	
	@Override
	public EnumSeason getSeason() {
		return getSeason(worldObj.getWorldTime());
	}
	
	@Override
	public EnumSeason getSeason(long time) {
		long seasonLenght = 24000 * SeasonsAPI.instance.getCfg().seasonLenght;
		long yearTime = time % (seasonLenght * 4);
		int seasonOrdinal = (int) Math.floor(yearTime / seasonLenght);
		return EnumSeason.values()[seasonOrdinal];
	}
	
	@Override
	public float getTemperature(BlockPos pos, boolean external) {
		if (pos == null)
			return 0F;
		BlockPos newPos = pos.down().up();
		if (getWorld() == null)
			return 0F;
		if (WorldHandler.tempMap == null)
			WorldHandler.tempMap = new HashMap<ChunkCoordIntPair, ChunkTemperature>();
		float timeMultiplier = -(Math.abs(12000 - ((worldObj.getWorldTime() + 6000) % 24000))) / 6000F;
		timeMultiplier++;
		if (external) {
			try {
				float temp = WorldHandler.tempMap.get(getWorld().getChunkFromBlockCoords(newPos).getChunkCoordIntPair()).calcBlockExternalTemp(worldObj, newPos);
				temp += getSeason().temperatureDif;
				temp += (getWorld().getBiomeGenForCoords(pos).getTemperature() * 12.5F * timeMultiplier);
				if (!getWorld().canBlockSeeSky(pos)) temp /= 1.5;
				return temp > 0 ? temp * getSeason().temperatureMultiplier : temp * getSeason().getOpposite().temperatureMultiplier;
			} catch (NullPointerException e) {
				ChunkTemperature temp = new ChunkTemperature();
				temp.calcChunkTemp(getWorld(), getWorld().getChunkFromBlockCoords(newPos).xPosition * 16, getWorld().getChunkFromBlockCoords(newPos).zPosition * 16);
				WorldHandler.tempMap.put(getWorld().getChunkFromBlockCoords(newPos).getChunkCoordIntPair(), temp);
				float temp2 = temp.getTempForBlock(newPos);
				temp2 += getSeason().temperatureDif;
				temp2 += (getWorld().getBiomeGenForCoords(pos).getTemperature() * 12.5F * timeMultiplier);
				if (!getWorld().canBlockSeeSky(pos)) temp2 /= 1.5;
				return temp2 > 0 ? temp2 * getSeason().temperatureMultiplier : temp2 * getSeason().getOpposite().temperatureMultiplier;
			}			
		} else {
			try {
				float temp = WorldHandler.tempMap.get(getWorld().getChunkFromBlockCoords(newPos).getChunkCoordIntPair()).getTempForBlock(newPos);
				temp += getSeason().temperatureDif;
				temp += (getWorld().getBiomeGenForCoords(pos).getTemperature() * 12.5F * timeMultiplier);
				if (!getWorld().canBlockSeeSky(pos)) temp /= 1.5;
				return temp > 0 ? temp * getSeason().temperatureMultiplier : temp * getSeason().getOpposite().temperatureMultiplier;
			} catch (NullPointerException e) {
				ChunkTemperature temp = new ChunkTemperature();
				temp.calcChunkTemp(getWorld(), getWorld().getChunkFromBlockCoords(newPos).xPosition * 16, getWorld().getChunkFromBlockCoords(newPos).zPosition * 16);
				WorldHandler.tempMap.put(getWorld().getChunkFromBlockCoords(newPos).getChunkCoordIntPair(), temp);
				float temp2 = temp.getTempForBlock(newPos);
				temp2 += getSeason().temperatureDif;
				temp2 += (getWorld().getBiomeGenForCoords(pos).getTemperature() * 12.5F * timeMultiplier);
				if (!getWorld().canBlockSeeSky(pos)) temp2 /= 1.5;
				return temp2 > 0 ? temp2 * getSeason().temperatureMultiplier : temp2 * getSeason().getOpposite().temperatureMultiplier;
			}
		}
	}

	@Override
	public float getTemperature(BlockPos pos) {
		return getTemperature(pos, false);
	}
	
	@Override
	public float getTemperatureForBiome(BiomeGenBase biome) {
		return 25F * (biome.getTemperature() - 0.15F);
	}

	@Override
	public float getTemperatureFromTime(BiomeGenBase biome, long time) {
		float temp = getTemperatureForBiome(biome) + (biome.getTemperature() * 25F * ((float)((time + 12000) % 24000 - 12000) / 12000F));
		return temp > 0 ? temp * getSeason().temperatureMultiplier : temp * getSeason().getOpposite().temperatureMultiplier;
	}

	@Override
	public float getTemperatureFromTime(BlockPos pos, long time) {
		return getTemperatureFromTime(getWorld().getBiomeGenForCoords(pos), time);
	}

	@Override
	public float getTemperatureFromTime(BiomeGenBase biome) {
		return getTemperatureFromTime(biome, worldObj.getWorldTime());
	}

	@Override
	public float getTemperatureFromTime(BlockPos pos) {
		return getTemperatureFromTime(pos, worldObj.getWorldTime());
	}
	
	@Override
	public float getInternalTemperature(EntityPlayer entity) {
		float temp = getTemperature(new BlockPos(entity.posX, entity.posY, entity.posZ));
		for (int i = 0; i < entity.inventory.armorInventory.length; i++) {
			ItemStack armor = entity.inventory.armorInventory[i];
			if (armor != null && armor.getItem() instanceof ITemperatureModifier) {
				temp = ((ITemperatureModifier)armor.getItem()).getTemperature(armor, temp);
			}
		}
		return temp;
	}

	public World getWorld() {
		return worldObj;
	}
}
