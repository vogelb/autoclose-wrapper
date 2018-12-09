package de.github.vogelb.autoclose;

public class HasCloseMethod extends Base {
	
	public HasCloseMethod(ClosedStatus closed) {
		super(closed);
	}
	
	public void close() {
		System.out.println("HasCloseMethod.close()");
		setClosed();
	}
}
