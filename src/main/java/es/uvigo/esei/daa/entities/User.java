package es.uvigo.esei.daa.entities;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a user.
 * 
 * @author Miguel Reboiro Jato
 */
public class User {
	private String login;
	private String password;
	private String role;

	// Constructor needed for the JSON conversion
	User() {}
	
	/**
	 * Constructs a new instance of {@link User}.
	 *
	 * @param login login that identifies the user in the system.
	 * @param password password of the user encoded using SHA-256 and with the
	 * "salt" prefix added.
	 */
	public User(String login, String password, String role) {
		this.setLogin(login);
		this.setPassword(password);
		this.setRole(role);
	}

	/**
	 * Returns the login of the user.
	 * 
	 * @return the login of the user.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets the login of the user.
	 * 
	 * @param login the login that identifies the user in the system.
	 */
	public void setLogin(String login) {
		this.login = requireNonNull(login, "Login can't be null");
	}
	
	/**
	 * Returns the password of the user.
	 * 
	 * @return the password of the user.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the users password.
	 * @param password the password of the user encoded using SHA-256 and with
	 * the "salt" prefix added.
	 */
	public void setPassword(String password) {
		requireNonNull(password, "Password can't be null");
		if (!password.matches("[a-zA-Z0-9]{64}"))
			throw new IllegalArgumentException("Password must be a valid SHA-256");
		
		this.password = password;
	}
	
	/**
	 * Returns the role of the user.
	 * 
	 * @return the role of the user.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 * 
	 * @param role the role of the user
	 */
	public void setRole(String role) {
		this.role = requireNonNull(role, "Role can't be null");
	}
}
