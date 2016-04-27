package mod.mindcraft.seasons.asm;

import java.util.Map;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class SeasonsPreLoader extends DummyModContainer implements IFMLLoadingPlugin {
	
	ModMetadata md = new ModMetadata();
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"mod.mindcraft.seasons.asm.Transformer"};
	}

	public SeasonsPreLoader() {
		md.modId="seasonsAPIPreloader";
		md.version="1.0.0";
		md.name="Seasons API Preloader";
	}
	
	@Override
	public String getModContainerClass() {
		return "mod.mindcraft.seasons.asm.SeasonsPreLoader";
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
	
	@Override
	public ModMetadata getMetadata() {
		return md;
	}
	
	@Override
	public String getModId() {
		return getMetadata().modId;
	}
	
	@Override
	public String getName() {
		return getMetadata().name;
	}
	
	@Override
	public String getVersion() {
		return getMetadata().name;
	}
}
