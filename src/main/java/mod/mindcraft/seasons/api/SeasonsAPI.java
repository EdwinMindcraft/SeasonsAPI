package mod.mindcraft.seasons.api;

public class SeasonsAPI {
	
	private IWorldInterface worldInterface;
	private IBlockTemperatureRegistry blockTemperatureRegistry;
	public SeasonsCFG cfg;
	
	public static final SeasonsAPI instance = new SeasonsAPI();
	
	private SeasonsAPI() {}
	
	public IWorldInterface getWorldInterface() {
		return worldInterface;
	}
	
	public IBlockTemperatureRegistry getBlockTemperatureRegistry() {
		return blockTemperatureRegistry;
	}
	
	public final void setWorldInterface(IWorldInterface worldInterface) {
		this.worldInterface = worldInterface;
	}
	
	public final void setBlockTemperatureRegistry(IBlockTemperatureRegistry blockTemperatureRegistry) {
		this.blockTemperatureRegistry = blockTemperatureRegistry;
	}
	
	public void setCfg(SeasonsCFG cfg) {
		this.cfg = cfg;
	}
	
	public SeasonsCFG getCfg() {
		return cfg;
	}
}
