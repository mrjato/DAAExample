package es.uvigo.esei.daa;

import static org.junit.Assert.assertEquals;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public final class TestUtils {
	private final static SimpleNamingContextBuilder CONTEXT_BUILDER =
		new SimpleNamingContextBuilder();
	
	private TestUtils() {}

	public static void createFakeContext(DataSource datasource)
	throws IllegalStateException, NamingException {
		CONTEXT_BUILDER.bind("java:/comp/env/jdbc/daaexample", datasource);
		CONTEXT_BUILDER.activate();
	}
	
	public static void clearContextBuilder() {
		CONTEXT_BUILDER.clear();
		CONTEXT_BUILDER.deactivate();
	}

	public static void assertOkStatus(final Response response) {
		assertEquals("Unexpected status code", Response.Status.OK.getStatusCode(), response.getStatus());
	}

	public static void assertBadRequestStatus(final Response response) {
		assertEquals("Unexpected status code", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
