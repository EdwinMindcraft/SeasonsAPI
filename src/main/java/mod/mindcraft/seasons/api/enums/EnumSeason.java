package mod.mindcraft.seasons.api.enums;

public enum EnumSeason {
	SPRING(1.0F, 0),
	SUMMER(1.5F, 20),
	AUTUMN(1.0F, -10),
	WINTER(0.5F, -20);
	
	public final float temperatureMultiplier;
	public final int temperatureDif;
	
	EnumSeason(float temperatureMultiplier, int temperatureDif) {
		this.temperatureMultiplier = temperatureMultiplier;
		this.temperatureDif = temperatureDif;
	}
	
	public EnumSeason getOpposite() {
		switch (this) {
		case SPRING: return AUTUMN;
		case SUMMER: return WINTER;
		case AUTUMN: return SPRING;
		case WINTER: return SUMMER;
		default:
			return null;
		}
	}
	
	public EnumSeason next() {
		switch (this) {
		case SPRING: return SUMMER;
		case SUMMER: return AUTUMN;
		case AUTUMN: return WINTER;
		case WINTER: return SPRING;
		default:
			return null;
		}		
	}
	
	public EnumSeason prev() {
		switch (this) {
		case SPRING: return WINTER;
		case SUMMER: return SPRING;
		case AUTUMN: return SUMMER;
		case WINTER: return AUTUMN;
		default:
			return null;
		}		
	}
}
