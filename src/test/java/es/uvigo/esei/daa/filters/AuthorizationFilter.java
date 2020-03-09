package es.uvigo.esei.daa.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.UsersDAO;
import es.uvigo.esei.daa.entities.User;

/**
 * This performs the Basic HTTP authentication following (almost) the same
 * rules as the defined in the web.xml file.
 * 
 * @author Miguel Reboiro Jato
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthorizationFilter implements ContainerRequestFilter {
	// Add here the list of REST paths that an administrator can access.
	private final static List<String> ADMIN_PATHS = Arrays.asList("people");
	
	private final UsersDAO dao;

	public AuthorizationFilter() {
		this.dao = new UsersDAO();
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Get the authentication passed in HTTP headers parameters
		final String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (auth == null) {
			requestContext.abortWith(createResponse());
		} else {
			final byte[] decodedToken = Base64.getDecoder()
				.decode(auth.substring(6));
			
			final String userColonPass = new String(decodedToken);
			final String[] userPass = userColonPass.split(":", 2);
			
			if (userPass.length == 2) {
				try {
					if (this.dao.checkLogin(userPass[0], userPass[1])) {
						final User user = this.dao.get(userPass[0]);
						
						if (isAdminPath(requestContext) && !user.getRole().equals("ADMIN")) {
							requestContext.abortWith(createResponse());
						} else {
							requestContext.setSecurityContext(new UserSecurityContext(user));
						}
					} else {
						requestContext.abortWith(createResponse());
					}
				} catch (DAOException e) {
					requestContext.abortWith(createResponse());
				}
			} else {
				requestContext.abortWith(createResponse());
			}
		}
	}
	
	private static boolean isAdminPath(ContainerRequestContext context) {
		final List<PathSegment> pathSegments = context.getUriInfo().getPathSegments();
		
		if (pathSegments.isEmpty()) {
			return false;
		} else {
			final String path = pathSegments.get(0).getPath();
			return ADMIN_PATHS.contains(path);
		}
	}
	
	private static Response createResponse() {
		return Response.status(Status.UNAUTHORIZED)
			.header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"DAAExample\"")
			.entity("Page requires login.")
		.build();
	}
	
	private static final class UserSecurityContext implements SecurityContext {
		private final User user;

		private UserSecurityContext(User user) {
			this.user = user;
		}

		@Override
		public boolean isUserInRole(String role) {
			return user.getRole().equals(role);
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {
				@Override
				public String getName() {
					return user.getLogin();
				}
			};
		}

		@Override
		public String getAuthenticationScheme() {
			return SecurityContext.BASIC_AUTH;
		}
	}
}
