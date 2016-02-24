package es.uvigo.esei.daa.dao;

import static es.uvigo.esei.daa.dataset.PeopleDataset.existentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.existentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newName;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newSurname;
import static es.uvigo.esei.daa.dataset.PeopleDataset.nonExistentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.nonExistentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.people;
import static es.uvigo.esei.daa.dataset.PeopleDataset.peopleWithout;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.containsPeopleInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.equalsToPerson;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
	public void testList() throws DAOException {
		assertThat(this.dao.list(), containsPeopleInAnyOrder(people()));
	}

	@Test
	public void testGet() throws DAOException {
		final Person person = this.dao.get(existentId());
		
		assertThat(person, is(equalsToPerson(existentPerson())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNonExistentId() throws DAOException {
		this.dao.get(nonExistentId());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws DAOException {
		this.dao.delete(existentId());

		assertThat(this.dao.list(), containsPeopleInAnyOrder(peopleWithout(existentId())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteNonExistentId() throws DAOException {
		this.dao.delete(nonExistentId());
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testModify() throws DAOException {
		final Person person = existentPerson();
		person.setName(newName());
		person.setSurname(newSurname());
		
		this.dao.modify(person);
		
		final Person persistentPerson = this.dao.get(person.getId());
		
		assertThat(persistentPerson, is(equalsToPerson(person)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyNonExistentId() throws DAOException {
		this.dao.modify(nonExistentPerson());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyNullPerson() throws DAOException {
		this.dao.modify(null);
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws DAOException {
		final Person person = this.dao.add(newName(), newSurname());
		
		assertThat(person, is(equalsToPerson(newPerson())));
		
		final Person persistentPerson = this.dao.get(person.getId());

		assertThat(persistentPerson, is(equalsToPerson(newPerson())));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullName() throws DAOException {
		this.dao.add(null, newSurname());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullSurname() throws DAOException {
		this.dao.add(newName(), null);
	}
}
