package es.uvigo.esei.daa.dao;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class PeopleDAOTest {
	private PeopleDAO dao;

	@Before
	public void setUp() throws Exception {
		this.dao = new PeopleDAO();
	}

	@Test
	public void testGet() throws DAOException {
		final Person person = this.dao.get(4);
		
		assertEquals(4, person.getId());
		assertEquals("María", person.getName());
		assertEquals("Márquez", person.getSurname());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExistentId() throws DAOException {
		this.dao.get(100);
	}

	@Test
	public void testList() throws DAOException {
		assertEquals(10, this.dao.list().size());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws DAOException {
		this.dao.delete(4);
		
		assertEquals(9, this.dao.list().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteNonExistentId() throws DAOException {
		this.dao.delete(100);
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testModify() throws DAOException {
		this.dao.modify(new Person(5, "John", "Doe"));
		
		final Person person = this.dao.get(5);
		
		assertEquals(5, person.getId());
		assertEquals("John", person.getName());
		assertEquals("Doe", person.getSurname());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyNonExistentId() throws DAOException {
		this.dao.modify(new Person(100, "John", "Doe"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyNullPerson() throws DAOException {
		this.dao.modify(null);
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws DAOException {
		final Person person = this.dao.add("John", "Doe");
		
		assertEquals("John", person.getName());
		assertEquals("Doe", person.getSurname());
		
		final Person personGet = this.dao.get(person.getId());

		assertEquals(person.getId(), personGet.getId());
		assertEquals("John", personGet.getName());
		assertEquals("Doe", personGet.getSurname());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullName() throws DAOException {
		this.dao.add(null, "Doe");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullSurname() throws DAOException {
		this.dao.add("John", null);
	}
}
