package es.uvigo.esei.daa.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

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
						final String shaPassword = encodeSha256(password);
						
						if (shaPassword.equals(dbPassword)) {
							return encodeBase64(login + ":" + password);
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
		final String decodedToken = decodeBase64(token);
		final int colonIndex = decodedToken.indexOf(':');
		
		if (colonIndex < 0 || colonIndex == decodedToken.length()-1) {
			throw new IllegalArgumentException("Invalid token");
		}
		
		final String login = decodedToken.substring(0, decodedToken.indexOf(':'));
		final String password = encodeSha256(decodedToken.substring(decodedToken.indexOf(':') + 1));
		
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
	
	private final static String decodeBase64(String text) {
		return new String(Base64.getDecoder().decode(text.getBytes()));
	}
	
	private final static String encodeBase64(String text) {
		return Base64.getEncoder().encodeToString(text.getBytes());
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
