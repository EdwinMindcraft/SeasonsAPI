package mod.mindcraft.seasons.api;

public class NoWorldException extends Exception {
	
	private static final long serialVersionUID = 9141003341461430702L;
	private String message;
	
	public NoWorldException(String msg) {
		this.message = msg;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
