package de.github.vogelb.autoclose;

public class Base implements Foo {
	
	private final ClosedStatus closed;
	
	public Base(ClosedStatus status) {
		closed = status;
	}

	public boolean isClosed() {
		return closed.isClosed();
	}
	
	protected void setClosed() {
		closed.close();
	}

	public void foo() {
		System.out.println("Foo(" + getClass().getName() + ")");
	}
}
