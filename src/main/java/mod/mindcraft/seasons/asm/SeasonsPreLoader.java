package mod.mindcraft.seasons.asm;

import java.util.Map;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class SeasonsPreLoader extends DummyModContainer implements IFMLLoadingPlugin {
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"mod.mindcraft.seasons.asm.Transformer"};
	}

	public SeasonsPreLoader() {
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
