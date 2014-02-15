package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.TestUtils.assertOkStatus;
import static es.uvigo.esei.daa.TestUtils.assertBadRequestStatus;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.uvigo.esei.daa.TestUtils;
import es.uvigo.esei.daa.entities.Person;

public class PeopleTest extends JerseyTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestUtils.createFakeContext();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		TestUtils.initTestDatabase();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		
		TestUtils.clearTestDatabase();
	}

	@Override
	protected Application configure() {
		return new ResourceConfig(People.class)
			.register(JacksonJsonProvider.class)
			.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
	}

	@Override
	protected void configureClient(ClientConfig config) {
		super.configureClient(config);
		
		config.register(JacksonJsonProvider.class);
		config.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
	}
	
	@Test
	public void testList() throws IOException {
		final Response response = target("people").request().get();
		assertOkStatus(response);

		final List<Person> people = response.readEntity(new GenericType<List<Person>>(){});
		assertEquals(10, people.size());
	}

	@Test
	public void testGet() throws IOException {
		final Response response = target("people/4").request().get();
		assertOkStatus(response);
		
		final Person person = response.readEntity(Person.class);
		assertEquals(4, person.getId());
		assertEquals("María", person.getName());
		assertEquals("Márquez", person.getSurname());
	}

	@Test
	public void testGetInvalidId() throws IOException {
		assertBadRequestStatus(target("people/100").request().get());
	}

	@Test
	public void testAdd() throws IOException {
		final Form form = new Form();
		form.param("name", "Xoel");
		form.param("surname", "Ximénez");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertOkStatus(response);
		
		final Person person = response.readEntity(Person.class);
		assertEquals(11, person.getId());
		assertEquals("Xoel", person.getName());
		assertEquals("Ximénez", person.getSurname());
	}

	@Test
	public void testAddMissingName() throws IOException {
		final Form form = new Form();
		form.param("surname", "Ximénez");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertBadRequestStatus(response);
	}

	@Test
	public void testAddMissingSurname() throws IOException {
		final Form form = new Form();
		form.param("name", "Xoel");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertBadRequestStatus(response);
	}

	@Test
	public void testModify() throws IOException {
		final Form form = new Form();
		form.param("name", "Marta");
		form.param("surname", "Méndez");
		
		final Response response = target("people/4")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertOkStatus(response);
		
		final Person person = response.readEntity(Person.class);
		assertEquals(4, person.getId());
		assertEquals("Marta", person.getName());
		assertEquals("Méndez", person.getSurname());
	}

	@Test
	public void testModifyName() throws IOException {
		final Form form = new Form();
		form.param("name", "Marta");
		
		final Response response = target("people/4")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertBadRequestStatus(response);
	}

	@Test
	public void testModifySurname() throws IOException {
		final Form form = new Form();
		form.param("surname", "Méndez");
		
		final Response response = target("people/4")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertBadRequestStatus(response);
	}

	@Test
	public void testModifyInvalidId() throws IOException {
		final Form form = new Form();
		form.param("name", "Marta");
		form.param("surname", "Méndez");
		
		final Response response = target("people/100")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertBadRequestStatus(response);
	}

	@Test
	public void testDelete() throws IOException {
		final Response response = target("people/4").request().delete();
		assertOkStatus(response);
		
		assertEquals(4, (int) response.readEntity(Integer.class));
	}

	@Test
	public void testDeleteInvalidId() throws IOException {
		assertBadRequestStatus(target("people/100").request().delete());
	}
}
