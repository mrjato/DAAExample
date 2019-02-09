package es.uvigo.esei.daa.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.UsersDAO;
/**
 * REST resource for managing users.
 * 
 * @author Miguel Reboiro Jato.
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
	private final static Logger LOG = Logger.getLogger(UsersResource.class.getName());
	
	private final UsersDAO dao;
	
	private @Context SecurityContext security;
	
	/**
	 * Constructs a new instance of {@link UsersResource}.
	 */
	public UsersResource() {
		this(new UsersDAO());
	}
	
	// Needed for testing purposes
	UsersResource(UsersDAO dao) {
		this(dao, null);
	}
	
	// Needed for testing purposes
	UsersResource(UsersDAO dao, SecurityContext security) {
		this.dao = dao;
		this.security = security;
	}
	
	/**
	 * Returns a user with the provided login.
	 * 
	 * @param login the identifier of the user to retrieve.
	 * @return a 200 OK response with an user that has the provided login.
	 * If the request is done without providing the login credentials or using
	 * invalid credentials a 401 Unauthorized response will be returned. If the
	 * credentials are provided and a regular user (i.e. non admin user) tries
	 * to access the data of other user, a 403 Forbidden response will be
	 * returned. If the credentials are OK, but the login does not corresponds
	 * with any user, a 400 Bad Request response with an error message will be
	 * returned. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	@GET
	@Path("/{login}")
	public Response get(
		@PathParam("login") String login
	) {
		final String loggedUser = getLogin();
		
		// Each user can only access his or her own data. Only the admin user
		// can access the data of any user.
		if (loggedUser.equals(login) || this.isAdmin()) {
			try {
				return Response.ok(dao.get(login)).build();
			} catch (IllegalArgumentException iae) {
				LOG.log(Level.FINE, "Invalid user login in get method", iae);
				
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(iae.getMessage())
				.build();
			} catch (DAOException e) {
				LOG.log(Level.SEVERE, "Error getting an user", e);
				
				return Response.serverError()
					.entity(e.getMessage())
				.build();
			}
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	private String getLogin() {
		return this.security.getUserPrincipal().getName();
	}
	
	private boolean isAdmin() {
		return this.security.isUserInRole("ADMIN");
	}
}
