# autoclose-wrapper
A wrapper that adds the AutoCloseable interface to any class in order to use it in a try-with-resources statement.
This is handy in cases where a class holds resources that need to be cleaned up but does not implement AutoCloseable.

The wrapper works with reflection or with a callback. The wrapped class must still offer means to clean up its resources.

Example: `javax.ws.rs.Client` implements a close method but not AutoCloseable. The close method is automatically detected

	public interface AutocloseableClient extends Client, AutoCloseable { }
	
	public class SomeClass() {
		public void foo() {
			try (AutocloseableClient client = AutoCloseWrapper.createInstance(AutocloseableClient.class, ClientBuilder.newClient()) {
				WebTarget target = client.target(...);
				target.request(...);
			}
		}
	}
	
	
Example using a callback: 
	
	public class SomeClass() {
		public void foo() {
			try (AutocloseableClient client = AutoCloseWrapper.createInstance(AutocloseableClient.class, ClientBuilder.newClient(),
				new AutoCloseWrapper.CloseCallback<Client>() {

					@Override
					public void close(Client delegate) {
						delegate.close();
					}

				})) {
				WebTarget target = client.target(...);
				target.request(...);
			}
		}
	}

