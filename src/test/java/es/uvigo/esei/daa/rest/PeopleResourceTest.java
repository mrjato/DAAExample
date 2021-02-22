package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.dataset.PeopleDataset.existentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.existentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newName;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newSurname;
import static es.uvigo.esei.daa.dataset.PeopleDataset.nonExistentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.people;
import static es.uvigo.esei.daa.dataset.UsersDataset.adminLogin;
import static es.uvigo.esei.daa.dataset.UsersDataset.normalLogin;
import static es.uvigo.esei.daa.dataset.UsersDataset.userToken;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasBadRequestStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasOkStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasUnauthorized;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.containsPeopleInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.equalsToPerson;
import static javax.ws.rs.client.Entity.entity;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

import es.uvigo.esei.daa.DAAExampleTestApplication;
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
		return new DAAExampleTestApplication();
	}

	@Override
	protected void configureClient(ClientConfig config) {
		super.configureClient(config);
		
		// Enables JSON transformation in client
		config.register(JacksonJsonProvider.class);
		config.property("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
	}
	
	@Test
	public void testList() throws IOException {
		final Response response = target("people").request()
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.get();
		assertThat(response, hasOkStatus());

		final List<Person> people = response.readEntity(new GenericType<List<Person>>(){});
		
		assertThat(people, containsPeopleInAnyOrder(people()));
	}
	
	@Test
	public void testListUnauthorized() throws IOException {
		final Response response = target("people").request()
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.get();
		assertThat(response, hasUnauthorized());
	}

	@Test
	public void testGet() throws IOException {
		final Response response = target("people/" + existentId()).request()
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.get();
		assertThat(response, hasOkStatus());
		
		final Person person = response.readEntity(Person.class);
		
		assertThat(person, is(equalsToPerson(existentPerson())));
	}
	
	@Test
	public void testGetUnauthorized() throws IOException {
		final Response response = target("people/" + existentId()).request()
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.get();
		assertThat(response, hasUnauthorized());
	}

	@Test
	public void testGetInvalidId() throws IOException {
		final Response response = target("people/" + nonExistentId()).request()
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.get();
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("surname", newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response, hasOkStatus());
		
		final Person person = response.readEntity(Person.class);
		
		assertThat(person, is(equalsToPerson(newPerson())));
	}
	
	@Test
	public void testAddUnauthorized() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("surname", newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response, hasUnauthorized());
	}

	@Test
	public void testAddMissingName() throws IOException {
		final Form form = new Form();
		form.param("surname", newSurname());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testAddMissingSurname() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		
		final Response response = target("people").request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.post(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testModify() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("surname", newSurname());
		
		final Response response = target("people/" + existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response, hasOkStatus());
		
		final Person modifiedPerson = response.readEntity(Person.class);
		
		final Person person = existentPerson();
		person.setName(newName());
		person.setSurname(newSurname());
		
		assertThat(modifiedPerson, is(equalsToPerson(person)));
	}

	@Test
	public void testModifyUnauthorized() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("surname", newSurname());
		
		final Response response = target("people/" + existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response, hasUnauthorized());
	}

	@Test
	public void testModifyName() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		
		final Response response = target("people/" + existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testModifySurname() throws IOException {
		final Form form = new Form();
		form.param("surname", newSurname());
		
		final Response response = target("people/" + existentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.put(entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testModifyInvalidId() throws IOException {
		final Form form = new Form();
		form.param("name", newName());
		form.param("surname", newSurname());
		
		final Response response = target("people/" + nonExistentId()).request(MediaType.APPLICATION_JSON_TYPE)
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response, hasBadRequestStatus());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws IOException {
		final Response response = target("people/" + existentId()).request()
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.delete();
		
		assertThat(response, hasOkStatus());
		
		final Integer deletedId = response.readEntity(Integer.class);
		
		assertThat(deletedId, is(equalTo(existentId())));
	}
	
	@Test
	public void testDeleteUnauthorized() throws IOException {
		final Response response = target("people/" + existentId()).request()
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.delete();
		
		assertThat(response, hasUnauthorized());
	}

	@Test
	public void testDeleteInvalidId() throws IOException {
		final Response response = target("people/" + nonExistentId()).request()
			.header("Authorization", "Basic " + userToken(adminLogin()))
		.delete();

		assertThat(response, hasBadRequestStatus());
	}
}
