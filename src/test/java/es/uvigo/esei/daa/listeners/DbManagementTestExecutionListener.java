package es.uvigo.esei.daa.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class DbManagementTestExecutionListener extends AbstractTestExecutionListener {
	private DbManagement configuration;
	private DataSource datasource;
	
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		final Class<?> testClass = testContext.getTestClass();
		this.configuration = testClass.getAnnotation(DbManagement.class);
		
		if (this.configuration == null)
			throw new IllegalStateException(String.format(
				"Missing %s annotation in %s class",
				DbManagement.class.getSimpleName(), testClass.getName()
			));
		
		this.datasource = testContext.getApplicationContext().getBean(DataSource.class);
		
		switch (this.configuration.action()) {
			case DROP_CREATE_DROP:
				executeDrop();
			case CREATE_DROP:
			case ONLY_CREATE:
				executeCreate();
				break;
			default:
		}
	}
	
	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		try {
			switch (this.configuration.action()) {
				case DROP_CREATE_DROP:
				case CREATE_DROP:
				case ONLY_DROP:
					executeDrop();
					break;
				default:
			}
		} finally {
			this.configuration = null;
			this.datasource = null;
		}
	}
	
	private void executeCreate() throws SQLException, IOException {
		this.executeQueries(configuration.create());
	}
	
	private void executeDrop() throws SQLException, IOException {
		this.executeQueries(configuration.drop());
	}
	
	private void executeQueries(String ... queriesPaths)
	throws SQLException, IOException {
		try (Connection connection = this.datasource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				for (String queryPath : queriesPaths) {
					final String queries = readFile(queryPath);
					for (String query : queries.split(";")) {
						query = query.trim();
						if (!query.trim().isEmpty()) {
							statement.addBatch(query);
						}
					}
				}
				statement.executeBatch();
			}
		}
	}
	
	private static String readFile(String path) throws IOException {
		final String classpathPrefix = "classpath:";
		
		if (path.startsWith(classpathPrefix)) {
			path = path.substring(classpathPrefix.length());
			
			final ClassLoader classLoader =
				DbManagementTestExecutionListener.class.getClassLoader();
			
			final InputStream fileIS = classLoader.getResourceAsStream(path);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileIS))) {
				final StringBuilder sb = new StringBuilder();
				
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				
				return sb.toString();
			}
		} else {
			return new String(Files.readAllBytes(Paths.get(path)));
		}
	}
}
