package es.uvigo.esei.daa.util;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public final class ContextUtils {
	private final static SimpleNamingContextBuilder CONTEXT_BUILDER =
		new SimpleNamingContextBuilder();
	
	private ContextUtils() {}

	public static void createFakeContext(DataSource datasource)
	throws IllegalStateException, NamingException {
		CONTEXT_BUILDER.bind("java:/comp/env/jdbc/daaexample", datasource);
		CONTEXT_BUILDER.activate();
	}
	
	public static void clearContextBuilder() {
		CONTEXT_BUILDER.clear();
		CONTEXT_BUILDER.deactivate();
	}
}
