package es.uvigo.esei.daa.dataset;

import java.util.Arrays;
import java.util.Base64;

import es.uvigo.esei.daa.entities.User;

public final class UsersDataset {
	private UsersDataset() {}

	public static User[] users() {
		return new User[] {
			new User(adminLogin(), "43f413b773f7d0cfad0e8e6529ec1249ce71e8697919eab30d82d800a3986b70"),
			new User(normalLogin(), "688f21dd2d65970f174e2c9d35159250a8a23e27585452683db8c5d10b586336")
		};
	}
	
	public static User user(String login) {
		return Arrays.stream(users())
			.filter(user -> user.getLogin().equals(login))
			.findAny()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	public static String adminLogin() {
		return "admin";
	}
	
	public static String normalLogin() {
		return "normal";
	}
	
	public static String userToken(String login) {
		final String chain = login + ":" + login + "pass";
		
		return Base64.getEncoder().encodeToString(chain.getBytes());
	}
}
