package mod.mindcraft.seasons.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import mod.mindcraft.seasons.api.init.SeasonsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomTemperatureReader {
	
	private File in;
	private static final Logger logger = LogManager.getLogger("SeasonAPI Custom Temperature");
	
	public CustomTemperatureReader(File in) {
		this.in = in;
	}
	
	public void readTemperaturesFromFile() {
		ArrayList<String> file = new ArrayList<String>();
		try {
			if (!in.getParentFile().exists())
				in.getParentFile().mkdirs();
			if (!in.exists()) {
				in.createNewFile();
				PrintWriter writer = new PrintWriter(in);
				writer.print("#Format : modid:block:state:temperature\n#Exemple : minecraft:end_rod:facing=up:-10\n#This will make the upward end_rod be at -10 C\n#Leaving the state blank will make it ignore state");
				writer.close();
			}
			BufferedReader nr = new BufferedReader(new FileReader(in));
			while (true) {
				String ln = nr.readLine();
				if (ln == null)
					break;
				int comment = ln.indexOf("#");
				if (comment != -1)
					ln = ln.substring(0, comment);
				if (ln.length() != 0)
					file.add(ln);
			}
			nr.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		
		for (String str : file) {
			String[] newStr = str.split(":");
			if (newStr.length != 3 && newStr.length != 4) {
				logger.warn("Skiping line : " + str + ", invalid format : " + newStr.length);
				continue;
			}
			if (newStr.length == 4) {
				newStr[0] = newStr[0] + ":" + newStr[1];
				newStr[1] = newStr[2];
				newStr[2] = newStr[3];
			}
			if (!GameRegistry.findRegistry(Block.class).containsKey(new ResourceLocation(newStr[0]))) {
				logger.warn("Skiping line : " + str + ", Block : " + newStr[0] + " does not exist!");
				continue;
			}
			Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(newStr[0]));
			IBlockState state = block.getDefaultState();
			boolean ignoreState = false;
			if (newStr[1].length() == 0) {
				logger.info("Using state ignoring for " + newStr[0]);
				ignoreState = true;
			}
			if (!ignoreState) {
				ArrayList<String> props = new ArrayList<String>();
				for (String prop : newStr[1].split(",")) {
					if (prop.split("=").length == 2)
						props.add(prop);
					else
						logger.warn("State " + prop + " is in an incorrect format");
				}
				for (IProperty<?> prop : state.getProperties().keySet()) {
					state = doProp(state, prop, props);
				}
			}
			try {
				if (SeasonsAPI.instance.getBlockTemperatureRegistry().addTemperatureToBlock(state, Float.valueOf(newStr[2]), ignoreState, true)) {
					logger.info("Successfully added temperature " + Float.valueOf(newStr[2]) + " to " + state.toString());
				} else {
					logger.info("Error : Cannot set temperature for" + state.toString());
				}
			} catch (NumberFormatException e) {
				logger.warn(newStr[2] + " cannot be used as temperature as it isn't a float");
			}
		}
	}
	
	private <T extends Comparable<T>> IBlockState doProp(IBlockState state, IProperty<T> prop, ArrayList<String> props) {
		for (String cur : props) {
			String[] data = cur.split("=");
			if(data[0].equalsIgnoreCase(prop.getName())) {
				for (T allowed : prop.getAllowedValues()) {
					if (prop.getName(allowed).equalsIgnoreCase(data[1])) {
						state = state.withProperty(prop, allowed);
						break;
					}
				}
			}
		}		
		return state;
	}
}
