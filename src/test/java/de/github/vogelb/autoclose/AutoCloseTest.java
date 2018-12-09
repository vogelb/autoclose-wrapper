package de.github.vogelb.autoclose;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.Test;

import de.github.vogelb.autoclose.AutoCloseWrapper.CloseCallback;

public class AutoCloseTest {
	
	public interface AutocloseableClient extends Client, AutoCloseable { }

	@Test
	public void testAutoClose() {
		ClosedStatus closed = new ClosedStatus();
		try (AutoClose foo = new AutoClose(closed)) {
			foo.foo();
			assertFalse(closed.isClosed());
		}
		assertTrue(closed.isClosed());
	}
	
	@Test
	public void testCloseable() {
		ClosedStatus closed = new ClosedStatus();
		try (IsCloseable foo = new IsCloseable(closed)) {
			foo.foo();
			assertFalse(closed.isClosed());
		}
		assertTrue(closed.isClosed());
	}
	
	@Test
	public void testCloseMethod() {
		ClosedStatus closed = new ClosedStatus();
		try (AutocloseFoo foo = AutoCloseWrapper.createInstance(AutocloseFoo.class, new HasCloseMethod(closed))) {
			foo.foo();
			assertFalse(closed.isClosed());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertTrue(closed.isClosed());
	}

	@Test
	public void testNoClose() {
		ClosedStatus closed = new ClosedStatus();
		try (AutocloseFoo foo = AutoCloseWrapper.createInstance(AutocloseFoo.class, new NoClose(closed),
				new AutoCloseWrapper.CloseCallback<NoClose>() {

					@Override
					public void close(NoClose delegate) {
						System.out.println("CloseCallback.close(NoClose)");
						delegate.setClosed();
					}

				})) {
			foo.foo();
			assertFalse(closed.isClosed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue(closed.isClosed());
	}

	@Test
	public void testOtherClose() {
		ClosedStatus closed = new ClosedStatus();

		try (AutocloseFoo foo = AutoCloseWrapper.createInstance(AutocloseFoo.class, new OtherClose(closed), "otherCloseMethod")) {
			foo.foo();
			assertFalse(closed.isClosed());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertTrue(closed.isClosed());
	}

	@Test
	public void testStillOtherClose() {
		ClosedStatus closed = new ClosedStatus();
		Method closeMethod;
		try {
			OtherClose c = new OtherClose(closed);
			closeMethod = c.getClass().getMethod("stillOtherCloseMethod", new Class[] { String.class });
			try (AutocloseFoo foo = AutoCloseWrapper.createInstance(AutocloseFoo.class, new OtherClose(closed), closeMethod, "Hello World!")) {
				foo.foo();
				assertFalse(closed.isClosed());
			} catch (Exception e) {
				fail(e.getMessage());
			}
			assertTrue(closed.isClosed());
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testClientReflection() {
		try (AutocloseableClient client = AutoCloseWrapper.createInstance(AutocloseableClient.class, ClientBuilder.newClient())){
			client.target("http://google.de");
		}
	}
	
	private final class ClientCloseCallback implements CloseCallback<Client> {
		@Override
		public void close(Client delegate) {
			System.out.println("Client.close()");
			delegate.close();
		}
	}
	
	@Test
	public void testClientCallback() {
		try (AutocloseableClient client = AutoCloseWrapper.createInstance(AutocloseableClient.class, ClientBuilder.newClient(), new ClientCloseCallback())){
			client.target("http://google.de");
		}
	}
	
}
