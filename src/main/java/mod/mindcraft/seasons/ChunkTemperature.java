package mod.mindcraft.seasons;

import java.util.HashMap;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.IBlockTemperatureRegistry;
import mod.mindcraft.seasons.api.interfaces.IWorldInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkTemperature {
	
	private float[] temp;
	SeasonsAPI ins = SeasonsAPI.instance;
	IWorldInterface wInt = ins.getWorldInterface();
	IBlockTemperatureRegistry tempReg = ins.getBlockTemperatureRegistry();
	
	public ChunkTemperature() {
		temp = new float[16*16*256];
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 16*16*256; i++) {
			NBTTagCompound tmp = new NBTTagCompound();
			tmp.setInteger("ID", i);
			tmp.setFloat("Temp", temp[i]);
			list.appendTag(tmp);
		}
		compound.setTag("TempMap", list);
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList("TempMap", 10);
		for (int i = 0; i < list.tagCount(); i++) {
			temp[list.getCompoundTagAt(i).getInteger("ID")] = list.getCompoundTagAt(i).getFloat("Temp");
		}
	}
	
	public float getTempForBlock(World world, BlockPos pos) {
		return temp[meta(pos)];
	}
	
	public void addBlockTemp(World world, BlockPos pos, float temp) {
		this.temp[meta(pos)] = temp;
	}
	
	public void calcBlockTemp(World world, BlockPos pos) {
		temp[meta(pos)] = tempReg.getTemperatureForBlock(world.getBlockState(pos));
		multiChunkRadiate(world, pos, SeasonsAPI.instance.cfg.temperatureSpreadDistance, wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos)), temp);
	}
	
	public float calcBlockExternalTemp(World world, BlockPos pos) {
		float maxTemp = Integer.MIN_VALUE;
		float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
		timeTemp += timeTemp * (-Math.abs(64 - pos.getY()) / 64F);
		if (maxTemp == Integer.MIN_VALUE) {
			maxTemp = timeTemp;
		}
		else if ((maxTemp <= 0 && timeTemp < maxTemp) || (maxTemp > 0 && timeTemp > maxTemp)) {
			maxTemp = timeTemp;
		}
		temp[meta(pos)] = maxTemp;
		return maxTemp;
	}
	
	private void calculateChunkTemperature(World world, Chunk chunk) {
		if (world == null) return;
		int radius = SeasonsAPI.instance.getCfg().temperatureSpreadDistance;
		int min = Integer.MIN_VALUE;
		float averageTemperature = 20F;
		float[] internalMapping = new float[16*16*256];
		BlockPos defaultPos = new BlockPos(chunk.xPosition * 16, 0, chunk.zPosition * 16);
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					int meta = i + j*16 + k*256;
					internalMapping[meta] = min;
				}
			}
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					BlockPos pos = defaultPos.add(i, k, j);
					IBlockState state = world.getBlockState(pos);
					if (tempReg.hasTemperature(state)) {
						int meta = i + j*16 + k*256;
						float blockTemp = tempReg.getTemperatureForBlock(state);
						if (blockTemp < averageTemperature) {
							if (blockTemp > internalMapping[meta]) {
								internalMapping[meta] = blockTemp;
								internalMapping = radiate(world, pos, radius, averageTemperature, internalMapping);
							}
						} else if (blockTemp > averageTemperature){
							if (blockTemp < internalMapping[meta]) {
								internalMapping[meta] = blockTemp;
								internalMapping = radiate(world, pos, radius, averageTemperature, internalMapping);
							}
						} else {
							internalMapping[meta] = averageTemperature;
						}
					}
				}
			}
		}
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					int meta = i + j*16 + k*256;
					BlockPos pos = defaultPos.add(i, k, j);
					if (internalMapping[meta] == min) {
						internalMapping[meta] = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));;
						float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
						timeTemp += timeTemp * (-Math.abs(64 - pos.getY()) / 64F);
						internalMapping[meta] += timeTemp;
						internalMapping[meta] /= 2;
					}
				}
			}
		}
		temp = internalMapping;
		
	}
	
	private float[] radiate(World world, BlockPos pos, int radius, float averageTemp, float[] internalMapping) {
		ChunkCoordIntPair coords = world.getChunkFromBlockCoords(pos).getChunkCoordIntPair();
		pos = pos.add(-coords.chunkXPos * 16, 0, -coords.chunkZPos * 16);
		float blockTemp = internalMapping[meta(pos)];
		System.out.println("BlockTemp " + blockTemp);
		for (int y = -radius; y <= radius; y++) {
			if (y + pos.getY() < 0 || y + pos.getY() > 255)
				continue;
			for (int x = -radius; x <= radius; x++) {
				if (x + pos.getX() < 0 || x + pos.getX() > 15)
					continue;
				for (int z = -radius; z <= radius; z++) {
					if (z + pos.getZ() < 0 || z + pos.getZ() > 15)
						continue;
					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
					if (dist == 0 || dist > radius)
						continue;
					int meta = meta(pos.add(x, y, z));
					float newTemp = getNewTemperature(blockTemp, dist, radius, averageTemp);
					internalMapping[meta] = getTemperature(internalMapping[meta], averageTemp, newTemp);
				}
			}
		}
		return internalMapping;
	}
	
	private void multiChunkRadiate(World world, BlockPos pos, int radius, float averageTemp, float[] internalMapping) {
		HashMap<ChunkCoordIntPair, ChunkTemperature> map = WorldHandler.tempMap;
		ChunkCoordIntPair coords = world.getChunkFromBlockCoords(pos).getChunkCoordIntPair();
		BlockPos chunkPos = pos.add(-coords.chunkXPos * 16, 0, -coords.chunkZPos * 16);
		System.out.println(chunkPos);
		float blockTemp = internalMapping[meta(pos)];
		for (int y = -radius; y <= radius; y++) {
			if (y + pos.getY() < 0 || y + pos.getY() > 255)
				continue;
			for (int x = -radius; x <= radius; x++) {
				boolean diffX = false;
				if (x + chunkPos.getX() < 0 || x + chunkPos.getX() > 15)
					diffX = true;
				for (int z = -radius; z <= radius; z++) {
					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
					if (dist == 0 || dist > radius)
						continue;
					if (diffX || z + chunkPos.getZ() < 0 || z + chunkPos.getZ() > 15) {
						BlockPos internalPos = pos.add(x, y, z);
						ChunkTemperature temp = map.get(world.getChunkFromBlockCoords(internalPos).getChunkCoordIntPair());
						if (temp == null){
							temp = new ChunkTemperature();
							temp.calcChunkTemp(world, world.getChunkFromBlockCoords(pos));
							map.put(world.getChunkFromBlockCoords(internalPos).getChunkCoordIntPair(), temp);
						}
						float newTemp = getNewTemperature(blockTemp, dist, radius, averageTemp);
						int meta = meta(internalPos);
						temp.temp[meta] = getTemperature(temp.temp[meta], averageTemp, newTemp);
					} else {
						int meta = meta(pos.add(x, y, z));
						float newTemp = getNewTemperature(blockTemp, dist, radius, averageTemp);
						internalMapping[meta] = getTemperature(internalMapping[meta], averageTemp, newTemp);
						//System.out.println(pos.add(x, y, z) + " : " + newTemp + " vs " + internalMapping[meta]);
					}
				}
			}
		}
		this.temp = internalMapping;
	}
	
	private float getNewTemperature(float blockTemp, int dist, int radius, float averageTemp) {
		return (blockTemp * (1F-((float)dist / (float)radius))) + (averageTemp * ((float)dist / (float)radius));
	}
	
	private float getTemperature(float current, float averageTemp, float newTemp) {
		if (newTemp < averageTemp) {
			if (newTemp < current) {
				return newTemp;
			}
		} else if (newTemp > averageTemp){
			if (newTemp > current) {
				return newTemp;
			}
		} else {
			return averageTemp;
		}
		return current;
//		return newTemp;
	}
	
	private int meta(BlockPos pos) {
		int xMod = pos.getX() < 0 ? 16 : 0;
		int zMod = pos.getZ() < 0 ? 16 : 0;
		pos = new BlockPos(Math.abs(xMod - (pos.getX() % 16)), pos.getY(), Math.abs(zMod - (pos.getZ() % 16)));
		return pos.getX() + (pos.getZ() * 16) + (MathHelper.clamp_int(pos.getY(), 0, 256) * 256);
	}
	
	public void calcChunkTemp(World world, Chunk chunk) {
		calculateChunkTemperature(world, chunk);
	}
}
