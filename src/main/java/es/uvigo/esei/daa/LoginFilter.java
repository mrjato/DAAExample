package es.uvigo.esei.daa;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.UsersDAO;

/**
 * Security filter that implements a login protocol based on the HTTP Basic
 * Authentication protocol. In this case, the login and password can be provided
 * as plain request parameters or as a cookie named "token" that should contain
 * both values in the same format as HTTP Basic Authentication
 * ({@code base64(login + ":" + password)}).
 * 
 * @author Miguel Reboiro Jato
 *
 */
@WebFilter(urlPatterns = { "/*", "/logout" })
public class LoginFilter implements Filter {
	private static final String REST_PATH = "/rest";
	private static final String INDEX_PATH = "/index.html";
	private static final String LOGOUT_PATH = "/logout";

	@Override
	public void doFilter(
		ServletRequest request, 
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		try {
			if (isLogoutPath(httpRequest)) {
				removeTokenCookie(httpResponse);
				redirectToIndex(httpRequest, httpResponse);
			} else if (isIndexPath(httpRequest) || checkToken(httpRequest)) {
				chain.doFilter(request, response);
			} else if (checkLogin(httpRequest, httpResponse)) {
				continueWithRedirect(httpRequest, httpResponse);
			} else if (isRestPath(httpRequest)) {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);				
			} else {
				redirectToIndex(httpRequest, httpResponse);
			}
		} catch (IllegalArgumentException iae) {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (DAOException e) {
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
	private boolean isLogoutPath(HttpServletRequest request) {
		return request.getServletPath().equals(LOGOUT_PATH);
	}
	
	private boolean isIndexPath(HttpServletRequest request) {
		return request.getServletPath().equals(INDEX_PATH);
	}
	
	private boolean isRestPath(HttpServletRequest request) {
		return request.getServletPath().startsWith(REST_PATH);
	}

	private void redirectToIndex(
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException {
		response.sendRedirect(request.getContextPath());
	}

	private void continueWithRedirect(
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException {
		String redirectPath = request.getRequestURI();
		if (request.getQueryString() != null)
			redirectPath += request.getQueryString();
		
		response.sendRedirect(redirectPath);
	}
	
	private void removeTokenCookie(HttpServletResponse response) {
		final Cookie cookie = new Cookie("token", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
	private boolean checkLogin(
		HttpServletRequest request, 
		HttpServletResponse response
	) throws DAOException {
		final String login = request.getParameter("login");
		final String password = request.getParameter("password");
		
		if (login != null && password != null) {
			final UsersDAO dao = new UsersDAO();
			if (dao.checkLogin(login, password)) {
				final Credentials credentials = new Credentials(login, password);
				
				response.addCookie(new Cookie("token", credentials.toToken()));
				
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean checkToken(HttpServletRequest request)
	throws DAOException, IllegalArgumentException {
		final Cookie[] cookies = Optional.ofNullable(request.getCookies())
			.orElse(new Cookie[0]);
		
		for (Cookie cookie : cookies) {
			if ("token".equals(cookie.getName())) {
				final Credentials credentials = new Credentials(cookie.getValue());
				
				final UsersDAO dao = new UsersDAO();
				
				return dao.checkLogin(credentials.getLogin(), credentials.getPassword());
			}
		}
		
		return false;
	}
	
	private static class Credentials {
		private final String login;
		private final String password;
		
		public Credentials(String token) {
			final String decodedToken = decodeBase64(token);
			final int colonIndex = decodedToken.indexOf(':');
			
			if (colonIndex < 0 || colonIndex == decodedToken.length()-1) {
				throw new IllegalArgumentException("Invalid token");
			}
			
			this.login = decodedToken.substring(0, colonIndex);
			this.password = decodedToken.substring(colonIndex + 1);
		}
		
		public Credentials(String login, String password) {
			this.login = requireNonNull(login, "Login can't be null");
			this.password = requireNonNull(password, "Password can't be null");
		}
		
		public String getLogin() {
			return login;
		}
		
		public String getPassword() {
			return password;
		}
		
		public String toToken() {
			return encodeBase64(this.login + ":" + this.password);
		}
		
		private final static String decodeBase64(String text) {
			return new String(Base64.getDecoder().decode(text.getBytes()));
		}
		
		private final static String encodeBase64(String text) {
			return Base64.getEncoder().encodeToString(text.getBytes());
		}
	}
}
