package es.uvigo.esei.daa.util;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;

import com.mysql.jdbc.PreparedStatement;

/**
 * Super-class for unit tests in the DAO layer.
 * 
 * <p>The default {@link DatabaseQueryUnitTest#setUp()} method in this class
 * create mocks for the datasource, connection, statement, and result variables
 * that can be used by the DAO object under test.</p>
 * 
 * @author Miguel Reboiro Jato
 */
public abstract class DatabaseQueryUnitTest {
	protected DataSource datasource;
	protected Connection connection;
	protected PreparedStatement statement;
	protected ResultSet result;
	
	protected boolean verify;

	/**
	 * Configures the mocks and enables the verification.
	 * 
	 * @throws Exception if an error happens while configuring the mocks.
	 */
	@Before
	public void setUp() throws Exception {
		datasource = createMock(DataSource.class);
		connection = createMock(Connection.class);
		statement = createNiceMock(PreparedStatement.class);
		result = createMock(ResultSet.class);
		
		expect(datasource.getConnection())
			.andReturn(connection);
		expect(connection.prepareStatement(anyString()))
			.andReturn(statement)
			.anyTimes(); // statement is optional
		expect(statement.executeQuery())
			.andReturn(result)
			.anyTimes(); // executeQuery is optional
		statement.close();
		connection.close();
		
		verify = true;
	}
	
	/**
	 * Removes the default behavior of the mock instances and disables the mock
	 * verification.
	 */
	protected void resetAll() {
		reset(result, statement, connection, datasource);
		verify = false;
	}

	/**
	 * Replays the configured behavior of the mock instances and enables the
	 * mock verification. The mocked datasource is also added to a new context.
	 */
	protected void replayAll()
	throws Exception {
		replay(result, statement, connection, datasource);
		verify = true;
		
		ContextUtils.createFakeContext(datasource);
	}
	
	/**
	 * Clears the context and verifies the mocks if the verification is enabled.
	 * 
	 * @throws Exception if an error happens during verification.
	 */
	@After
	public void tearDown() throws Exception {
		ContextUtils.clearContextBuilder();
		
		try {
			if (verify) {
				verify(datasource, connection, statement, result);
				verify = false;
			}
		} finally {
			datasource = null;
			connection = null;
			statement = null;
			result = null;
		}
	}
}
