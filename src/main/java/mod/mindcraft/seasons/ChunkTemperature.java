package mod.mindcraft.seasons;

import mod.mindcraft.seasons.api.IBlockTemperatureRegistry;
import mod.mindcraft.seasons.api.IWorldInterface;
import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

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
	
	public float calcBlockTemp(World world, BlockPos pos) {
		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
		IChunkProvider provider = world.getChunkProvider();
		float maxTemp = tempReg.getTemperatureForBlock(world.getBlockState(pos));
		for (int y = -5; y < 6; y++) {
			if (y + pos.getY() < 0 || y + pos.getY() > 256)
				continue;
			for (int x = -5; x < 6; x++) {
				for (int z = -5; z < 6; z++) {
					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
					if (dist == 0 || dist > 5)
						continue;
					BlockPos newPos = pos.add(x, y, z);
					if (!provider.chunkExists((int)Math.floor((float)newPos.getX() / 16F), (int)Math.floor((float)newPos.getZ() / 16F)))
						continue;
					if (world.isAirBlock(newPos))
						continue;
					float temp = tempReg.getTemperatureForBlock(world.getBlockState(newPos));
					if (temp == Integer.MIN_VALUE)
						continue;
					temp *= (5-(float)dist) * 0.2F;
					if (temp > maxTemp)
						maxTemp = temp;
				}
			}
		}
		float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
		timeTemp += timeTemp * (-Math.abs(64 - pos.getY()) / 64F);
		if (maxTemp == Integer.MIN_VALUE) {
			maxTemp = timeTemp;
		}
		else if ((maxTemp <= 0 && timeTemp < maxTemp) || (maxTemp > 0 && timeTemp > maxTemp)) {
			maxTemp = timeTemp;
		}
		if (world.getBlockState(pos).getBlock().equals(Blocks.glass))
			System.out.println(maxTemp);
		temp[meta] = maxTemp;
		return maxTemp;
	}
	
	public float calcBlockExternalTemp(World world, BlockPos pos) {
		int meta = (Math.abs(pos.getX()) % 16) + (Math.abs(pos.getZ()) % 16) * 16 + (Math.abs(pos.getY()) % 256) * 256;
		IChunkProvider provider = world.getChunkProvider();
		float maxTemp = Integer.MIN_VALUE;
		for (int y = -5; y < 6; y++) {
			if (y + pos.getY() < 0 || y + pos.getY() > 256)
				continue;
			for (int x = -5; x < 6; x++) {
				for (int z = -5; z < 6; z++) {
					int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
					if (dist == 0 || dist > 5)
						continue;
					BlockPos newPos = pos.add(x, y, z);
					if (!provider.chunkExists((int)Math.floor((float)newPos.getX() / 16F), (int)Math.floor((float)newPos.getZ() / 16F)))
						continue;
					if (world.isAirBlock(newPos))
						continue;
					float temp = tempReg.getTemperatureForBlock(world.getBlockState(newPos));
					if (temp == Integer.MIN_VALUE)
						continue;
					temp *= (5-(float)dist) * 0.2F;
					if (temp > maxTemp)
						maxTemp = temp;
				}
			}
		}
		float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
		timeTemp += timeTemp * (-Math.abs(64 - pos.getY()) / 64F);
		if (maxTemp == Integer.MIN_VALUE) {
			maxTemp = timeTemp;
		}
		else if ((maxTemp <= 0 && timeTemp < maxTemp) || (maxTemp > 0 && timeTemp > maxTemp)) {
			maxTemp = timeTemp;
		}
		if (world.getBlockState(pos).getBlock().equals(Blocks.glass))
			System.out.println(maxTemp);
		temp[meta] = maxTemp;
		return maxTemp;
	}
	
	public void calcChunkTemp(World world, int posX, int posY) {
		if (world == null)
			return;
		int min = Integer.MIN_VALUE;
		boolean enableEnableAdvancedTemperatureGen = SeasonsAPI.instance.getCfg().enableAdvancedTemperatureGen;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 256; k++) {
					//System.out.println("Iter : " + i);
					BlockPos pos = new BlockPos(posX + i, k, posY + j);
					float maxTemp = tempReg.getTemperatureForBlock(world.getBlockState(pos));
					if (enableEnableAdvancedTemperatureGen) {
						if (world.isAirBlock(pos) && world.isAirBlock(pos.down(1)) && world.isAirBlock(pos.down(2)) && world.isAirBlock(pos.down(3)) && world.isAirBlock(pos.down(4)) && world.isAirBlock(pos.down(5))) {
							temp[i + k*16 + j*256] = (float) (wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos)) * (-Math.abs(64 - pos.getY()) * 0.1));
							continue;
						}
						for (int y = -5; y < 6; y++) {
							if (y + pos.getY() < 0 || y + pos.getY() > 256)
								continue;
							for (int x = -5; x < 6; x++) {
								for (int z = -5; z < 6; z++) {
									int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
									if (dist == 0 || dist > 5)
										continue;
									BlockPos newPos = pos.add(x, y, z);
									if (world.isAirBlock(newPos))
										continue;
									float temp = tempReg.getTemperatureForBlock(world.getBlockState(newPos));
									if (temp == min)
										continue;
									temp *= (5-(float)dist) * 0.2F;
									if (temp > maxTemp)
										maxTemp = temp;
								}
							}
						}
					}
					float timeTemp = wInt.getTemperatureForBiome(world.getBiomeGenForCoords(pos));
					if (timeTemp > maxTemp) {
						maxTemp = timeTemp;
						maxTemp += timeTemp * (-Math.abs(64F - pos.getY()) / 32F);
					}
					temp[i + j*16 + k*256] = maxTemp;
				}
			}
		}
	}
}
