package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.dataset.UsersDataset.adminLogin;
import static es.uvigo.esei.daa.dataset.UsersDataset.normalLogin;
import static es.uvigo.esei.daa.dataset.UsersDataset.user;
import static es.uvigo.esei.daa.dataset.UsersDataset.userToken;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasOkStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasUnauthorized;
import static es.uvigo.esei.daa.matchers.IsEqualToUser.equalsToUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;
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
import es.uvigo.esei.daa.entities.User;
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
public class UsersResourceTest extends JerseyTest {
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
	public void testGetAdminOwnUser() throws IOException {
		final String admin = adminLogin();
		
		final Response response = target("users/" + admin).request()
			.header("Authorization", "Basic " + userToken(admin))
		.get();
		assertThat(response, hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		assertThat(user, is(equalsToUser(user(admin))));
	}
	
	@Test
	public void testGetAdminOtherUser() throws IOException {
		final String admin = adminLogin();
		final String otherUser = normalLogin();
		
		final Response response = target("users/" + otherUser).request()
			.header("Authorization", "Basic " + userToken(admin))
		.get();
		assertThat(response, hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		assertThat(user, is(equalsToUser(user(otherUser))));
	}
	
	@Test
	public void testGetNormalOwnUser() throws IOException {
		final String login = normalLogin();
		
		final Response response = target("users/" + login).request()
			.header("Authorization", "Basic " + userToken(login))
		.get();
		assertThat(response, hasOkStatus());
		
		final User user = response.readEntity(User.class);
		
		assertThat(user, is(equalsToUser(user(login))));
	}
	
	@Test
	public void testGetNoCredentials() throws IOException {
		final Response response = target("users/" + normalLogin()).request().get();
		
		assertThat(response, hasUnauthorized());
	}
	
	@Test
	public void testGetBadCredentials() throws IOException {
		final Response response = target("users/" + adminLogin()).request()
			.header("Authorization", "Basic YmFkOmNyZWRlbnRpYWxz")
		.get();
		
		assertThat(response, hasUnauthorized());
	}
	
	@Test
	public void testGetIllegalAccess() throws IOException {
		final Response response = target("users/" + adminLogin()).request()
			.header("Authorization", "Basic " + userToken(normalLogin()))
		.get();
		
		assertThat(response, hasUnauthorized());
	}
}
