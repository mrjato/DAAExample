package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class UsersDAO extends DAO {
	private final static Logger LOG = Logger.getLogger("UsersDAO");
	
	public String checkLogin(String login, String password) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT password FROM users WHERE login=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, login);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						final String dbPassword = result.getString("password");
						final String shaPassword = DigestUtils.sha256Hex(password);
						
						if (shaPassword.equals(dbPassword)) {
							return new String(Base64.encodeBase64((login + ":" + password).getBytes()));
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking login", e);
			throw new DAOException(e);
		}
	}
	
	public String checkToken(String token)
	throws DAOException, IllegalArgumentException {
		final String decodedToken = new String(Base64.decodeBase64(token.getBytes()));
		final int colonIndex = decodedToken.indexOf(':');
		
		if (colonIndex < 0 || colonIndex == decodedToken.length()-1) {
			throw new IllegalArgumentException("Invalid token");
		}
		
		final String login = decodedToken.substring(0, decodedToken.indexOf(':'));
		final String password = DigestUtils.sha256Hex(
			decodedToken.substring(decodedToken.indexOf(':') + 1)
		);
		
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT password FROM users WHERE login=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, login);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						final String dbPassword = result.getString("password"); 
						
						return password.equals(dbPassword) ? login : null;
					} else {
						return null;
					}
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error checking token", e);
			throw new DAOException(e);
		}
	}
}
