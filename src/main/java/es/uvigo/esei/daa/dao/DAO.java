package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Simple base class for DAO (Data Access Object) classes. This super-class is
 * responsible for providing a {@link java.sql.Connection} to its sub-classes.
 *  
 * @author Miguel Reboiro Jato
 *
 */
public abstract class DAO {
	private final static Logger LOG = Logger.getLogger(DAO.class.getName());
	private final static String JNDI_NAME = "java:/comp/env/jdbc/daaexample"; 
	
	private DataSource dataSource;
	
	/**
	 * Constructs a new instance of {@link DAO}.
	 */
	public DAO() {
		try {
			this.dataSource = (DataSource) new InitialContext().lookup(JNDI_NAME);
		} catch (NamingException e) {
			LOG.log(Level.SEVERE, "Error initializing DAO", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns an open {@link java.sql.Connection}.
	 * 
	 * @return an open {@link java.sql.Connection}.
	 * @throws SQLException if an error happens while establishing the
	 * connection with the database.
	 */
	protected Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
