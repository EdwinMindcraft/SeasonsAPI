package mod.mindcraft.seasons;

import java.util.HashMap;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import mod.mindcraft.seasons.api.interfaces.IBlockTemperatureRegistry;
import mod.mindcraft.seasons.api.interfaces.IWorldInterface;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
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
	
	public float getTempForBlock(BlockPos pos) {
		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
		return temp[meta];
	}
	
	public void addBlockTemp(BlockPos pos, float temp) {
		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
		this.temp[meta] = temp;
	}
	
	public void calcBlockTemp(World world, BlockPos pos) {
		//multiChunkRadiate(world, pos, SeasonsAPI.instance.cfg.temperatureSpreadDistance, 20, temp);
		multiChunkRadiate(world, pos, SeasonsAPI.instance.cfg.temperatureSpreadDistance, 20, temp);
//		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
//		int tempSpreadDist = SeasonsAPI.instance.getCfg().temperatureSpreadDistance;
//		float maxTemp = tempReg.getTemperatureForBlock(world.getBlockState(pos));
//		boolean hasTemp = false;
//		for (int y = -tempSpreadDist; y <= tempSpreadDist; y++) {
//			if (y + pos.getY() < 0 || y + pos.getY() > 256)
//				continue;
//			for (int x = -tempSpreadDist; x <= tempSpreadDist; x++) {
//				for (int z = -tempSpreadDist; z <= tempSpreadDist; z++) {
//					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
//					if (dist == 0 || dist > tempSpreadDist)
//						continue;
//					BlockPos newPos = pos.add(x, y, z);
//					if (world.isAirBlock(newPos))
//						continue;
//					float temp = tempReg.getTemperatureForBlock(world.getBlockState(newPos));
//					if (temp == Integer.MIN_VALUE)
//						continue;
//					temp *= 1F - ((float)dist /(float)tempSpreadDist);
//					if (maxTemp == Integer.MIN_VALUE || Math.abs(temp) > Math.abs(maxTemp))
//						maxTemp = temp;
//					hasTemp = true;
//				}
//			}
//		}
//		float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
//		timeTemp += timeTemp * (-Math.abs(64F - pos.getY()) / 64F);
//		if (maxTemp == Integer.MIN_VALUE) {
//			maxTemp = timeTemp;
//		} else {
//			maxTemp = (maxTemp * 0.5F) + (timeTemp * 0.5F);
//		}
//		temp[meta] = maxTemp;
//		return hasTemp;
	}
	
	public float calcBlockExternalTemp(World world, BlockPos pos) {
		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
//		IChunkProvider provider = world.getChunkProvider();
		float maxTemp = Integer.MIN_VALUE;
//		for (int y = -1; y < 2; y++) {
//			if (y + pos.getY() < 0 || y + pos.getY() > 256)
//				continue;
//			for (int x = -1; x < 2; x++) {
//				for (int z = -1; z < 2; z++) {
//					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
//					if (dist == 0 || dist > 1)
//						continue;
//					BlockPos newPos = pos.add(x, y, z);
//					if (!provider.chunkExists((int)Math.floor((float)newPos.getX() / 16F), (int)Math.floor((float)newPos.getZ() / 16F)))
//						continue;
//					if (world.isAirBlock(newPos))
//						continue;
//					float temp = tempReg.getTemperatureForBlock(world.getBlockState(newPos));
//					if (temp == Integer.MIN_VALUE)
//						continue;
//					temp *= (5-(float)dist) * 0.2F;
//					if (temp > maxTemp)
//						maxTemp = temp;
//				}
//			}
//		}
		float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
		timeTemp += timeTemp * (-Math.abs(64 - pos.getY()) / 64F);
		if (maxTemp == Integer.MIN_VALUE) {
			maxTemp = timeTemp;
		}
		else if ((maxTemp <= 0 && timeTemp < maxTemp) || (maxTemp > 0 && timeTemp > maxTemp)) {
			maxTemp = timeTemp;
		}
		temp[meta] = maxTemp;
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
							}
						} else if (blockTemp > averageTemperature){
							if (blockTemp < internalMapping[meta]) {
								internalMapping[meta] = blockTemp;
								internalMapping = radiate(pos, radius, averageTemperature, internalMapping);
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
	
	private float[] radiate(BlockPos pos, int radius, float averageTemp, float[] internalMapping) {
		pos = new BlockPos(pos.getX() % 16, pos.getY(), pos.getZ() % 16);
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
					int meta = (pos.getX() + x) + (pos.getZ() + z)*16 + (pos.getY() + y)*256;
					float blockTemp = internalMapping[x + z*16 + y*256];
					float newTemp = ((blockTemp * dist / (float)radius) + (averageTemp * (1 - (dist / (float)radius))));
					if (newTemp < averageTemp) {
						if (newTemp > internalMapping[meta]) {
							internalMapping[meta] = newTemp;
						}
					} else if (newTemp > averageTemp){
						if (newTemp < internalMapping[meta]) {
							internalMapping[meta] = newTemp;
						}
					}
				}
			}
		}
		return internalMapping;
	}
	
	private void multiChunkRadiate(World world, BlockPos pos, int radius, float averageTemp, float[] internalMapping) {
		HashMap<ChunkCoordIntPair, ChunkTemperature> map = WorldHandler.tempMap;
		BlockPos chunkPos = new BlockPos(pos.getX() % 16, pos.getY(), pos.getZ() % 16);
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
						ChunkTemperature temp = map.get(world.getChunkFromBlockCoords(internalPos));
						int meta = internalPos.getX() % 16 + internalPos.getZ() % 16 *16 + internalPos.getY()*256;
						float blockTemp = internalMapping[x + z*16 + y*256];
						float newTemp = ((blockTemp * dist / (float)radius) + (averageTemp * (1 - (dist / (float)radius))));
						int meta2 = meta(new BlockPos(internalPos.getX() % 16, internalPos.getY(), internalPos.getZ() % 16));
						boolean changed = false;
						if (newTemp < averageTemp) {
							if (newTemp > temp.temp[meta]) {
								temp.temp[meta2] = newTemp;
								changed = true;
							}
						} else if (newTemp > averageTemp){
							if (newTemp < temp.temp[meta]) {
								temp.temp[meta2] = newTemp;
								changed = true;
							}
						}
						if (changed)
							temp.temp = temp.radiate(new BlockPos(internalPos.getX() % 16, internalPos.getY(), internalPos.getZ()), radius - dist, averageTemp, temp.temp);
					} else {
						int meta = meta(chunkPos.add(x, y, z));
						float blockTemp = internalMapping[meta(chunkPos)];
						float newTemp = ((blockTemp * dist / (float)radius) + (averageTemp * (1 - (dist / (float)radius))));
						if (newTemp < averageTemp) {
							if (newTemp > internalMapping[meta]) {
								internalMapping[meta] = newTemp;
							}
						} else if (newTemp > averageTemp){
							if (newTemp < internalMapping[meta]) {
								internalMapping[meta] = newTemp;
							}
						}
					}
				}
			}
		}
		this.temp = internalMapping;
	}
	
	private int meta(BlockPos pos) {
		return (pos.getX()) + (pos.getZ())*16 + (pos.getY())*256;
	}
	
	public void calcChunkTemp(World world, Chunk chunk) {
		calculateChunkTemperature(world, chunk);
	}
}
