package de.github.vogelb.autoclose;

public class AutoClose extends Base implements AutoCloseable {

	public AutoClose(ClosedStatus closed) {
		super(closed);
	}
	
	@Override
	public void close() {
		setClosed();
		System.out.println("Autclose.close()");
	}

}
