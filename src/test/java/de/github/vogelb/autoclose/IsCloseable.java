package de.github.vogelb.autoclose;

import java.io.Closeable;
import java.io.IOException;

public class IsCloseable extends Base implements Closeable {
	
	public IsCloseable(ClosedStatus closed) {
		super(closed);
	}
	
	@Override
	public void close() {
		System.out.println("IsCloseable.close()");
		setClosed();
	}

}
