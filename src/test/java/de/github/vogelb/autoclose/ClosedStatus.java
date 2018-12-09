package de.github.vogelb.autoclose;

public class ClosedStatus {
	private boolean value = false;

	public boolean isClosed() {
		return value;
	}

	public void close() {
		value = true;
	}
	
	
}
