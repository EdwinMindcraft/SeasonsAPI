package mod.mindcraft.seasons.packets;

import io.netty.buffer.ByteBuf;
import mod.mindcraft.seasons.WorldInterface;
import mod.mindcraft.seasons.api.SeasonsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTemperature implements IMessage, IMessageHandler<PacketTemperature, IMessage> {
	
	private BlockPos pos;
	private float temp;
	
	public PacketTemperature(BlockPos pos, float temp) {
		this.pos = pos;
		this.temp = temp;
	}
	
	public PacketTemperature() {
		
	}
	
	@Override
	public IMessage onMessage(final PacketTemperature message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			
			@Override
			public void run() {
				((WorldInterface)SeasonsAPI.instance.getWorldInterface()).setTemp(message.pos, message.temp);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		String str = ByteBufUtils.readUTF8String(buf);
		pos = new BlockPos(Integer.valueOf(str.split(" ")[0]), Integer.valueOf(str.split(" ")[1]), Integer.valueOf(str.split(" ")[2]));
		temp = Float.valueOf(str.split(" ")[3]);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + temp);
	}

}
