package es.uvigo.esei.daa.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class for managing the users of the system.
 * 
 * @author Miguel Reboiro Jato
 */
public class UsersDAO extends DAO {
	private final static Logger LOG = Logger.getLogger(UsersDAO.class.getName());
	
	private final static String SALT = "daaexample-";

	/**
	 * Checks if the provided credentials (login and password) correspond with a
	 * valid user registered in the system.
	 * 
	 * <p>The password is stored in the system "salted" and encoded with the
	 * SHA-256 algorithm.</p>
	 * 
	 * @param login the login of the user.
	 * @param password the password of the user.
	 * @return {@code true} if the credentials are valid. {@code false}
	 * otherwise.
	 * @throws DAOException if an error happens while checking the credentials.
	 */
	public boolean checkLogin(String login, String password) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT password FROM users WHERE login=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, login);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						final String dbPassword = result.getString("password");
						final String shaPassword = encodeSha256(SALT + password);
						
						return shaPassword.equals(dbPassword);
					} else {
						return false;
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking login", e);
			throw new DAOException(e);
		}
	}
	
	private final static String encodeSha256(String text) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] digested = digest.digest(text.getBytes());
			
			return hexToString(digested);
		} catch (NoSuchAlgorithmException e) {
			LOG.log(Level.SEVERE, "SHA-256 not supported", e);
			throw new RuntimeException(e);
		}
	}
	
	private final static String hexToString(byte[] hex) {
		final StringBuilder sb = new StringBuilder();
		
		for (byte b : hex) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		return sb.toString();
	}
}
