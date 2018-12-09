package de.github.vogelb.autoclose;

public class OtherClose extends Base {
	
	public OtherClose(ClosedStatus closed) {
		super(closed);
	}
	
	public void otherCloseMethod() {
		System.out.println("OtherClose.close()");
		setClosed();
	}
	
	public String stillOtherCloseMethod(String message) {
		System.out.println("OtherClose.close(" +  message + ")");
		setClosed();
		return message;
	}
}
