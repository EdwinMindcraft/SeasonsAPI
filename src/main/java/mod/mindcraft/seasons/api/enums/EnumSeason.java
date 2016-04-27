package mod.mindcraft.seasons.api.enums;

public enum EnumSeason {
	SPRING(1.0F),
	SUMMER(1.5F),
	AUTUMN(1.0F),
	WINTER(0.5F);
	
	public final float temperatureMultiplier;
	
	EnumSeason(float temperatureMultiplier) {
		this.temperatureMultiplier = temperatureMultiplier;
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
