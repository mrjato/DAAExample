package es.uvigo.esei.daa.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PeopleDAO;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class People {
	private final PeopleDAO dao;
	
	public People() {
		this.dao = new PeopleDAO();
	}

	@GET
	@Path("/list")
	public Response list() {
		try {
			return Response.ok(this.dao.list(), MediaType.APPLICATION_JSON).build();
		} catch (DAOException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/delete")
	public Response delete(
		@QueryParam("id") int id
	) {
		try {
			this.dao.delete(id);
			
			return Response.ok(id).build();
		} catch (DAOException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/modify")
	public Response modify(
		@QueryParam("id") int id, 
		@QueryParam("name") String name, 
		@QueryParam("surname") String surname
	) {
		try {
			return Response.ok(this.dao.modify(id, name, surname)).build();
		} catch (DAOException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/add")
	public Response add(
		@QueryParam("name") String name, 
		@QueryParam("surname") String surname
	) {
		try {
			return Response.ok(this.dao.add(name, surname)).build();
		} catch (DAOException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
}
