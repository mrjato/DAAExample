package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.TestUtils.assertBadRequestStatus;
import static es.uvigo.esei.daa.TestUtils.assertOkStatus;
import static javax.ws.rs.client.Entity.entity;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.listeners.ApplicationContextBinding;
import es.uvigo.esei.daa.listeners.ApplicationContextJndiBindingTestExecutionListener;
import es.uvigo.esei.daa.listeners.DbManagement;
import es.uvigo.esei.daa.listeners.DbManagementTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:contexts/mem-context.xml")
@TestExecutionListeners({
	DbUnitTestExecutionListener.class,
	DbManagementTestExecutionListener.class,
	ApplicationContextJndiBindingTestExecutionListener.class
})
@ApplicationContextBinding(
	jndiUrl = "java:/comp/env/jdbc/daaexample",
	type = DataSource.class
)
@DbManagement(
	create = "classpath:db/hsqldb.sql",
	drop = "classpath:db/hsqldb-drop.sql"
)
@DatabaseSetup("/datasets/dataset.xml")
@ExpectedDatabase("/datasets/dataset.xml")
public class PeopleResourceTest extends JerseyTest {
	@Override
	protected Application configure() {
		return new ResourceConfig(PeopleResource.class)
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
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws IOException {
		final Form form = new Form();
		form.param("name", "John");
		form.param("surname", "Doe");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertOkStatus(response);
		
		final Person person = response.readEntity(Person.class);
		assertEquals(11, person.getId());
		assertEquals("John", person.getName());
		assertEquals("Doe", person.getSurname());
	}

	@Test
	public void testAddMissingName() throws IOException {
		final Form form = new Form();
		form.param("surname", "Ximénez");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertBadRequestStatus(response);
	}

	@Test
	public void testAddMissingSurname() throws IOException {
		final Form form = new Form();
		form.param("name", "Xoel");
		
		final Response response = target("people")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertBadRequestStatus(response);
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testModify() throws IOException {
		final Form form = new Form();
		form.param("name", "John");
		form.param("surname", "Doe");
		
		final Response response = target("people/5")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertOkStatus(response);
		
		final Person person = response.readEntity(Person.class);
		assertEquals(5, person.getId());
		assertEquals("John", person.getName());
		assertEquals("Doe", person.getSurname());
	}

	@Test
	public void testModifyName() throws IOException {
		final Form form = new Form();
		form.param("name", "Marta");
		
		final Response response = target("people/4")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertBadRequestStatus(response);
	}

	@Test
	public void testModifySurname() throws IOException {
		final Form form = new Form();
		form.param("surname", "Méndez");
		
		final Response response = target("people/4")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
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
	@ExpectedDatabase("/datasets/dataset-delete.xml")
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
