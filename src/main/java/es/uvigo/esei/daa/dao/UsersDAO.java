package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;

public class UsersDAO extends DAO {
	public String checkLogin(String login, String password) throws DAOException {
		final String shaPassword = DigestUtils.sha256Hex(password);

		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT password FROM users WHERE login=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, login);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						final String dbPassword = result.getString("password");
						
						if (shaPassword.equals(dbPassword)) {
							return DigestUtils.sha256Hex(login + dbPassword);
						} else {
							return null;
						}
					} else {
						return null;
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public String checkToken(String token) throws DAOException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT login FROM users WHERE sha2(concat(login, password), 256)=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, token);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return result.getString("login");
					} else {
						return null;
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
}
