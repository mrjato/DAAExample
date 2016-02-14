package es.uvigo.esei.daa.dao;

import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import es.uvigo.esei.daa.DatabaseQueryUnitTest;
import es.uvigo.esei.daa.entities.Person;

public class PeopleDAOUnitTest extends DatabaseQueryUnitTest {
	@Test
	public void testGet() throws Exception {
		final Person person = new Person(1, "Pepe", "Pérez");
		
		expect(result.next()).andReturn(true);
		expect(result.getInt("id")).andReturn(person.getId());
		expect(result.getString("name")).andReturn(person.getName());
		expect(result.getString("surname")).andReturn(person.getSurname());
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		assertEquals("Unexpected person data",
			person, peopleDAO.get(person.getId())
		);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetMissing() throws Exception {
		expect(result.next()).andReturn(false);
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.get(2);
	}
	
	@Test(expected = DAOException.class)
	public void testGetUnexpectedException() throws Exception {
		expect(result.next()).andThrow(new SQLException());
		result.close();
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.get(2);
	}

	@Test
	public void testList() throws Exception {
		final List<Person> people = Arrays.asList(
			new Person(1, "Pepe", "Pérez"),
			new Person(2, "Paco", "Martínez"),
			new Person(3, "Martina", "Juárez")
		);
		
		for (Person person : people) {
			expect(result.next()).andReturn(true);
			expect(result.getInt("id")).andReturn(person.getId());
			expect(result.getString("name")).andReturn(person.getName());
			expect(result.getString("surname")).andReturn(person.getSurname());
		}
		expect(result.next()).andReturn(false);
		result.close();
		
		replayAll();
		final PeopleDAO peopleDAO = new PeopleDAO();

		assertEquals("Unexpected people data",
			people, peopleDAO.list()
		);
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
	public void testAdd() throws Exception {
		final Person person = new Person(1, "Pepe", "Pérez");
		
		reset(connection);
		expect(connection.prepareStatement(anyString(), eq(1)))
			.andReturn(statement);
		expect(statement.executeUpdate()).andReturn(1);
		expect(statement.getGeneratedKeys()).andReturn(result);
		expect(result.next()).andReturn(true);
		expect(result.getInt(1)).andReturn(person.getId()); // Key retrieval
		connection.close();
		result.close();

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		final Person newPerson = peopleDAO.add(person.getName(), person.getSurname());
		
		assertEquals(person, newPerson);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullName() throws Exception {
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		resetAll(); // No expectations
		
		peopleDAO.add(null, "Pepe");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullSurname() throws Exception {
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		
		resetAll(); // No expectations
		
		peopleDAO.add("Pepe", null);
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
		peopleDAO.add("Paco", "Pérez");
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
		peopleDAO.add("Paco", "Pérez");
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
		peopleDAO.add("Paco", "Pérez");
	}

	@Test
	public void testDelete() throws Exception {
		expect(statement.executeUpdate()).andReturn(1);
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteInvalidId() throws Exception {
		expect(statement.executeUpdate()).andReturn(0);
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(1);
	}

	@Test(expected = DAOException.class)
	public void testDeleteUnexpectedException() throws Exception {
		expect(statement.executeUpdate()).andThrow(new SQLException());
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.delete(1);
	}

	@Test
	public void testModify() throws Exception {
		final Person person = new Person(1, "Pepe", "Pérez");
		
		expect(statement.executeUpdate()).andReturn(1);

		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.modify(person);
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
		peopleDAO.modify(new Person(1, "Paco", "Pérez"));
	}
	
	@Test(expected = DAOException.class)
	public void testModifyUnexpectedException() throws Exception {
		expect(statement.executeUpdate()).andThrow(new SQLException());
		
		replayAll();
		
		final PeopleDAO peopleDAO = new PeopleDAO();
		peopleDAO.modify(new Person(1, "Paco", "Pérez"));
	}
}
