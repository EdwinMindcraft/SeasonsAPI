package mod.mindcraft.seasons.api.utils;

import mod.mindcraft.seasons.api.event.CropsUpdateEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventUtils {
	
	public static IBlockState postCropUdate(World world, BlockPos pos, IBlockState state) {
		CropsUpdateEvent event = new CropsUpdateEvent(world, pos, state);
		MinecraftForge.EVENT_BUS.post(event);
		return event.state;
	}
	
}
