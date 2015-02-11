package es.uvigo.esei.daa.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PeopleDAO;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {
	private final static Logger LOG = Logger.getLogger("PeopleResource");
	
	private final PeopleDAO dao;
	
	public PeopleResource() {
		this(new PeopleDAO());
	}
	
	// For testing purposes
	PeopleResource(PeopleDAO dao) {
		this.dao = dao;
	}

	@GET
	public Response list() {
		try {
			return Response.ok(this.dao.list()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error listing people", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/{id}")
	public Response get(
		@PathParam("id") int id
	) {
		try {
			return Response.ok(this.dao.get(id), MediaType.APPLICATION_JSON).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in get method", iae);
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error getting a person", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(
		@PathParam("id") int id
	) {
		try {
			this.dao.delete(id);
			
			return Response.ok(id).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in delete method", iae);
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error deleting a person", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("/{id}")
	public Response modify(
		@PathParam("id") int id, 
		@FormParam("name") String name, 
		@FormParam("surname") String surname
	) {
		try {
			return Response.ok(this.dao.modify(id, name, surname)).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in modify method", iae);
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error modifying a person", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@POST
	public Response add(
		@FormParam("name") String name, 
		@FormParam("surname") String surname
	) {
		try {
			return Response.ok(this.dao.add(name, surname)).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid person id in add method", iae);
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage()).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error adding a person", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
