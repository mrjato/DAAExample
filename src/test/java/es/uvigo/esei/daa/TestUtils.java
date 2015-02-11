package es.uvigo.esei.daa;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;


public final class TestUtils {
	private final static SimpleNamingContextBuilder CONTEXT_BUILDER
		= new SimpleNamingContextBuilder();
	
	private TestUtils() {}
	
	public static void createFakeContext() throws NamingException {
		createFakeContext(createTestingDataSource());
	}

	public static void createFakeContext(DataSource datasource)
	throws IllegalStateException, NamingException {
		CONTEXT_BUILDER.bind("java:/comp/env/jdbc/daaexample", datasource);
		CONTEXT_BUILDER.activate();
	}
	
	public static void clearContextBuilder() {
		CONTEXT_BUILDER.clear();
		CONTEXT_BUILDER.deactivate();
	}

	public static BasicDataSource createTestingDataSource() {
		final BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/daaexampletest?allowMultiQueries=true");
		ds.setUsername("daa");
		ds.setPassword("daa");
		ds.setMaxActive(100);
		ds.setMaxIdle(30);
		ds.setMaxWait(10000);
		return ds;
	}

	public static BasicDataSource createEmptyDataSource() {
		return new BasicDataSource();
	}
	
	public static void clearTestDatabase() throws SQLException {
		final String queries = new StringBuilder()
			.append("DELETE FROM `people`;")
			.append("DELETE FROM `users`;")
		.toString();

		final DataSource ds = createTestingDataSource();
		try (Connection connection = ds.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(queries);
			}
		}
	}
	
	public static void initTestDatabase() throws SQLException {
		final String queries = new StringBuilder()
			.append("ALTER TABLE `people` AUTO_INCREMENT = 1;")
			.append("ALTER TABLE `users` AUTO_INCREMENT = 1;")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Antón', 'Álvarez');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Ana', 'Amargo');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Manuel', 'Martínez');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'María', 'Márquez');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Lorenzo', 'López');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Laura', 'Laredo');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Perico', 'Palotes');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Patricia', 'Pérez');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Juan', 'Jiménez');")
			.append("INSERT INTO `people` (`id`,`name`,`surname`) VALUES (0, 'Julia', 'Justa');")
			.append("INSERT INTO `users` (`login`,`password`) VALUES ('mrjato', '59189332a4abf8ddf66fde068cad09eb563b4bd974f7663d97ff6852a7910a73');")
		.toString();

		final DataSource ds = createTestingDataSource();
		try (Connection connection = ds.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.execute(queries);
			}
		}
	}

	public static void assertOkStatus(final Response response) {
		assertEquals("Unexpected status code", Response.Status.OK.getStatusCode(), response.getStatus());
	}

	public static void assertBadRequestStatus(final Response response) {
		assertEquals("Unexpected status code", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
	}
}
