package es.uvigo.esei.daa.dao;

import static es.uvigo.esei.daa.dataset.PeopleDataset.existentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.existentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newName;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newSurname;
import static es.uvigo.esei.daa.dataset.PeopleDataset.people;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.containsPeopleInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.equalsToPerson;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reset;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;

import com.mysql.jdbc.Statement;

import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.util.DatabaseQueryUnitTest;

public class PeopleDAOUnitTest extends DatabaseQueryUnitTest {
	@Test
	public void testList() throws Exception {
		final Person[] people = people();
		
		for (Person person : people) {
			expectPersonRow(person);
		}
		expect(result.next()).andReturn(false);
		result.close();
		
		replayAll();
		final PeopleDAO peopleDAO = new PeopleDAO();

		assertThat(peopleDAO.list(), containsPeopleInAnyOrder(people));
	}
	
	@Test(expected = DAOException.class)
	public void testListUnexpectedException() throws Exception {
		expect(result.next()).andThrow(new SQLException());
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.list();
	}
	
	@Test
	public void testGet() throws Exception {
		final Person existentPerson = existentPerson();
		
		expectPersonRow(existentPerson);
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		assertThat(peopleDAO.get(existentId()), is(equalTo(existentPerson)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetMissing() throws Exception {
		expect(result.next()).andReturn(false);
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.get(existentId());
	}
	
	@Test(expected = DAOException.class)
	public void testGetUnexpectedException() throws Exception {
		expect(result.next()).andThrow(new SQLException());
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.get(existentId());
	}

	@Test
	public void testAdd() throws Exception {
		final Person person = newPerson();
		reset(connection);
		expect(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
			.andReturn(statement);
		expect(statement.executeUpdate()).andReturn(1);
		expect(statement.getGeneratedKeys()).andReturn(result);
		
		// Key retrieval
		expect(result.next()).andReturn(true);
		expect(result.getInt(1)).andReturn(person.getId());
		connection.close();
		result.close();

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		final Person newPerson = peopleDAO.add(person.getName(), person.getSurname());
		
		assertThat(newPerson, is(equalsToPerson(person)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullName() throws Exception {
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		resetAll(); // No expectations
		
		peopleDAO.add(null, newSurname());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullSurname() throws Exception {
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		resetAll(); // No expectations
		
		peopleDAO.add(newName(), null);
	}

	@Test(expected = DAOException.class)
	public void testAddZeroUpdatedRows() throws Exception {
		reset(connection);
		expect(connection.prepareStatement(anyString(), eq(1)))
			.andReturn(statement);
		expect(statement.executeUpdate()).andReturn(0);
		connection.close();

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.add(newName(), newSurname());
	}

	@Test(expected = DAOException.class)
	public void testAddNoGeneratedKey() throws Exception {
		reset(connection);
		expect(connection.prepareStatement(anyString(), eq(1)))
			.andReturn(statement);
		expect(statement.executeUpdate()).andReturn(1);
		expect(statement.getGeneratedKeys()).andReturn(result);
		expect(result.next()).andReturn(false);
		result.close();
		connection.close();

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.add(newName(), newSurname());
	}
	
	@Test(expected = DAOException.class)
	public void testAddUnexpectedException() throws Exception {
		reset(connection);
		expect(connection.prepareStatement(anyString(), eq(1)))
			.andReturn(statement);
		expect(statement.executeUpdate()).andThrow(new SQLException());
		connection.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.add(newName(), newSurname());
	}

	@Test
	public void testDelete() throws Exception {
		expect(statement.executeUpdate()).andReturn(1);
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(existentId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteInvalidId() throws Exception {
		expect(statement.executeUpdate()).andReturn(0);
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(existentId());
	}

	@Test(expected = DAOException.class)
	public void testDeleteUnexpectedException() throws Exception {
		expect(statement.executeUpdate()).andThrow(new SQLException());
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(existentId());
	}

	@Test
	public void testModify() throws Exception {
		expect(statement.executeUpdate()).andReturn(1);

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.modify(existentPerson());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyNullPerson() throws Exception {
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		resetAll(); // No expectations
		
		peopleDAO.modify(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModifyZeroUpdatedRows() throws Exception {
		expect(statement.executeUpdate()).andReturn(0);

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.modify(existentPerson());
	}
	
	@Test(expected = DAOException.class)
	public void testModifyUnexpectedException() throws Exception {
		expect(statement.executeUpdate()).andThrow(new SQLException());
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.modify(existentPerson());
	}
	
	private void expectPersonRow(Person person) throws SQLException {
		expect(result.next()).andReturn(true);
		expect(result.getInt("id")).andReturn(person.getId());
		expect(result.getString("name")).andReturn(person.getName());
		expect(result.getString("surname")).andReturn(person.getSurname());
	}
}
