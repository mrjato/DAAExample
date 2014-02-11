package es.uvigo.esei.daa;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.UsersDAO;

public class LoginFilter implements Filter {
	@Override
	public void doFilter(
		ServletRequest request, 
		ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (isLogoutPath(httpRequest)) {
			removeCookie(httpResponse);
			redirectToIndex(httpRequest, httpResponse);
		} else if (isIndexPath(httpRequest) ||
			checkLogin(httpRequest, httpResponse) ||
			checkToken(httpRequest)
		) {
			chain.doFilter(request, response);
		} else {
			redirectToIndex(httpRequest, httpResponse);
		}
	}

	private void redirectToIndex(
		HttpServletRequest httpRequest,
		HttpServletResponse httpResponse
	) throws IOException {
		httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.html");
	}
	
	private void removeCookie(HttpServletResponse httpResponse) {
		final Cookie cookie = new Cookie("token", "");
		cookie.setMaxAge(0);
		httpResponse.addCookie(cookie);
	}
	
	private boolean isLogoutPath(HttpServletRequest httpRequest) {
		return httpRequest.getServletPath().equals("/logout");
	}
	
	private boolean isIndexPath(HttpServletRequest httpRequest) {
		return httpRequest.getServletPath().equals("/index.html");
	}
	
	private boolean checkLogin(HttpServletRequest httpRequest, HttpServletResponse response) {
		final String login = httpRequest.getParameter("login");
		final String password = httpRequest.getParameter("password");
		
		if (login != null && password != null) {
			try {
				final UsersDAO dao = new UsersDAO();
				final String token = dao.checkLogin(login, password);
				
				if (token == null) {
					return false;
				} else {
					response.addCookie(new Cookie("token", token));
					
					return true;
				}
			} catch (DAOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean checkToken(HttpServletRequest httpRequest) {
		final Cookie[] cookies = httpRequest.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("token")) {
					try {
						return new UsersDAO().checkToken(cookie.getValue()) != null;
					} catch (DAOException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
