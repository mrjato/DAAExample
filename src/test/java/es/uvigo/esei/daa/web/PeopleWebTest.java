package es.uvigo.esei.daa.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.listeners.ApplicationContextBinding;
import es.uvigo.esei.daa.listeners.ApplicationContextJndiBindingTestExecutionListener;
import es.uvigo.esei.daa.listeners.DbManagement;
import es.uvigo.esei.daa.listeners.DbManagementTestExecutionListener;
import es.uvigo.esei.daa.web.pages.MainPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:contexts/hsql-context.xml")
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
public class PeopleWebTest {
	private static final int DEFAULT_WAIT_TIME = 1;
	
	private WebDriver driver;
	private MainPage mainPage;
	
	@Before
	public void setUp() throws Exception {
		final String baseUrl = "http://localhost:9080/DAAExample/";
		
		driver = new FirefoxDriver();
		driver.get(baseUrl);
		
		// Login as "admin:admin"
		driver.manage().addCookie(new Cookie("token", "YWRtaW46YWRtaW4="));
		
		// Driver will wait DEFAULT_WAIT_TIME if it doesn't find and element.
		driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
		
		mainPage = new MainPage(driver, baseUrl);
		mainPage.navigateTo();
	}
	
	@After
	public void tearDown() throws Exception {
		driver.quit();
		driver = null;
		mainPage = null;
	}

	@Test
	public void testList() throws Exception {
		assertEquals(10, mainPage.countPeople());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws Exception {
		final String name = "John";
		final String surname = "Doe";
		
		final Person newPerson = mainPage.addPerson(name, surname);
		
		assertEquals(name, newPerson.getName());
		assertEquals(surname, newPerson.getSurname());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testEdit() throws Exception {
		final int id = 5;
		final String newName = "John";
		final String newSurname = "Doe";

		mainPage.editPerson(id, "John", "Doe");
		
		final Person person = mainPage.getPerson(id);
		
		assertEquals(id, person.getId());
		assertEquals(newName, person.getName());
		assertEquals(newSurname, person.getSurname());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws Exception {
		mainPage.deletePerson(4);
		
		assertFalse(mainPage.hasPerson(4));
	}
}
