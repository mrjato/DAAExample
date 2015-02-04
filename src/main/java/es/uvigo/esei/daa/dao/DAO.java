package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public abstract class DAO {
	private final static Logger LOG = Logger.getLogger("DAO");
	private final static String JNDI_NAME = "java:/comp/env/jdbc/daaexample"; 
	
	private final DataSource dataSource;
	
	public DAO() {
		Context initContext;
		try {
			initContext = new InitialContext();
			this.dataSource = (DataSource) initContext.lookup(
				System.getProperty("db.jndi", JNDI_NAME)
			);
		} catch (NamingException e) {
			LOG.log(Level.SEVERE, "Error initializing DAO", e);
			throw new RuntimeException(e);
		}
	}
	
	protected Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
