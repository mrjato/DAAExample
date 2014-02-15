package es.uvigo.esei.daa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import es.uvigo.esei.daa.entities.Person;

public class PeopleDAO extends DAO {
	public Person get(int id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM people WHERE id=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, id);
				
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						return new Person(
							result.getInt("id"),
							result.getString("name"),
							result.getString("surname")
						);
					} else {
						throw new IllegalArgumentException("Invalid id");
					}
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public List<Person> list() throws DAOException {
		try (final Connection conn = this.getConnection()) {
			try (Statement statement = conn.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM people")) {
					final List<Person> people = new LinkedList<>();
					
					while (result.next()) {
						people.add(new Person(
							result.getInt("id"),
							result.getString("name"),
							result.getString("surname")
						));
					}
					
					return people;
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public void delete(int id)
	throws DAOException, IllegalArgumentException {
		try (final Connection conn = this.getConnection()) {
			final String query = "DELETE FROM people WHERE id=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, id);
				
				if (statement.executeUpdate() != 1) {
					throw new IllegalArgumentException("Invalid id");
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	
	public Person modify(int id, String name, String surname)
	throws DAOException, IllegalArgumentException {
		if (name == null || surname == null) {
			throw new IllegalArgumentException("name and surname can't be null");
		}
		
		try (final Connection conn = this.getConnection()) {
			final String query = "UPDATE people SET name=?, surname=? WHERE id=?";
			
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, name);
				statement.setString(2, surname);
				statement.setInt(3, id);
				
				if (statement.executeUpdate() == 1) {
					return new Person(id, name, surname); 
				} else {
					throw new IllegalArgumentException("name and surname can't be null");
				}
			}
		} catch (SQLException e) {
			throw new DAOException();
		}
	}
	
	public Person add(String name, String surname)
	throws DAOException, IllegalArgumentException {
		if (name == null || surname == null) {
			throw new IllegalArgumentException("name and surname can't be null");
		}
		
		try (final Connection conn = this.getConnection()) {
			final String query = "INSERT INTO people VALUES(null, ?, ?)";
			
			try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, name);
				statement.setString(2, surname);
				
				if (statement.executeUpdate() == 1) {
					try (ResultSet resultKeys = statement.getGeneratedKeys()) {
						if (resultKeys.next()) {
							return new Person(resultKeys.getInt(1), name, surname);
						} else {
							throw new SQLException("Error retrieving inserted id");
						}
					}
				} else {
					throw new SQLException("Error inserting value");
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
}
