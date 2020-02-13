package es.uvigo.esei.daa.web;

import static es.uvigo.esei.daa.dataset.PeopleDataset.existentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.existentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newName;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newSurname;
import static es.uvigo.esei.daa.dataset.PeopleDataset.people;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.containsPeopleInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.equalsToPerson;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.html5.LocalStorage;
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
	private static final int DEFAULT_WAIT_TIME = 5;
	
	private WebDriver driver;
	private MainPage mainPage;
	
	@Before
	public void setUp() throws Exception {
		final String baseUrl = "http://localhost:9080/DAAExample/";

		final FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.privatebrowsing.autostart", true);
		
		final FirefoxOptions options = new FirefoxOptions();
		options.setProfile(profile);
		
		final FirefoxDriver firefoxDriver;
		driver = firefoxDriver = new FirefoxDriver();
		driver.get(baseUrl);
		
		// Driver will wait DEFAULT_WAIT_TIME if it doesn't find and element.
		driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		// Login as "admin:adminpass"
		final LocalStorage localStorage = firefoxDriver.getLocalStorage();
		// YWRtaW46YWRtaW5wYXNz
		localStorage.setItem("user", "{\"login\":\"admin\",\"password\":\"adminpass\"}");
		
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
		assertThat(mainPage.listPeople(), containsPeopleInAnyOrder(people()));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws Exception {
		final Person newPerson = mainPage.addPerson(newName(), newSurname());
		
		assertThat(newPerson, is(equalsToPerson(newPerson())));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testEdit() throws Exception {
		final Person person = existentPerson();
		person.setName(newName());
		person.setSurname(newSurname());

		mainPage.editPerson(person);
		
		final Person webPerson = mainPage.getPerson(person.getId());
		
		assertThat(webPerson, is(equalsToPerson(person)));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws Exception {
		mainPage.deletePerson(existentId());
		
		assertFalse(mainPage.hasPerson(existentId()));
	}
}
