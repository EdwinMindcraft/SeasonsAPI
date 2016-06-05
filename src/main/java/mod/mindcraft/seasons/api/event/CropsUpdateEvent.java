package mod.mindcraft.seasons.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class CropsUpdateEvent extends BlockEvent {
	
	public IBlockState state;
	
	public CropsUpdateEvent(World world, BlockPos pos, IBlockState state) {
		super(world, pos, state);
		this.state = state;
	}

}
