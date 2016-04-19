package es.uvigo.esei.daa.rest;

import static es.uvigo.esei.daa.dataset.PeopleDataset.existentId;
import static es.uvigo.esei.daa.dataset.PeopleDataset.existentPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newName;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newPerson;
import static es.uvigo.esei.daa.dataset.PeopleDataset.newSurname;
import static es.uvigo.esei.daa.dataset.PeopleDataset.people;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasBadRequestStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasInternalServerErrorStatus;
import static es.uvigo.esei.daa.matchers.HasHttpStatus.hasOkStatus;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.containsPeopleInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPerson.equalsToPerson;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PeopleDAO;
import es.uvigo.esei.daa.entities.Person;

public class PeopleResourceUnitTest {
	private PeopleDAO daoMock;
	private PeopleResource resource;

	@Before
	public void setUp() throws Exception {
		daoMock = createMock(PeopleDAO.class);
		resource = new PeopleResource(daoMock);
	}

	@After
	public void tearDown() throws Exception {
		try {
			verify(daoMock);
		} finally {
			daoMock = null;
			resource = null;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testList() throws Exception {
		final List<Person> people = asList(people());
		
		expect(daoMock.list()).andReturn(people);
		
		replay(daoMock);
		
		final Response response = resource.list();
		
		assertThat(response, hasOkStatus());
		assertThat((List<Person>) response.getEntity(), containsPeopleInAnyOrder(people()));
	}

	@Test
	public void testListDAOException() throws Exception {
		expect(daoMock.list()).andThrow(new DAOException());
		
		replay(daoMock);
		
		final Response response = resource.list();
		
		assertThat(response, hasInternalServerErrorStatus());
	}

	@Test
	public void testGet() throws Exception {
		final Person person = existentPerson();
		
		expect(daoMock.get(person.getId())).andReturn(person);
		
		replay(daoMock);
		
		final Response response = resource.get(person.getId());
		
		assertThat(response, hasOkStatus());
		assertThat((Person) response.getEntity(), is(equalsToPerson(person)));
	}

	@Test
	public void testGetDAOException() throws Exception {
		expect(daoMock.get(anyInt())).andThrow(new DAOException());
		
		replay(daoMock);
		
		final Response response = resource.get(existentId());
		
		assertThat(response, hasInternalServerErrorStatus());
	}

	@Test
	public void testGetIllegalArgumentException() throws Exception {
		expect(daoMock.get(anyInt())).andThrow(new IllegalArgumentException());
		
		replay(daoMock);
		
		final Response response = resource.get(existentId());
		
		assertThat(response, hasBadRequestStatus());
	}
	
	@Test
	public void testDelete() throws Exception {
		daoMock.delete(anyInt());
		
		replay(daoMock);
		
		final Response response = resource.delete(1);
		
		assertThat(response, hasOkStatus());
	}

	@Test
	public void testDeleteDAOException() throws Exception {
		daoMock.delete(anyInt());
		expectLastCall().andThrow(new DAOException());
		
		replay(daoMock);
		
		final Response response = resource.delete(1);
		
		assertThat(response, hasInternalServerErrorStatus());
	}

	@Test
	public void testDeleteIllegalArgumentException() throws Exception {
		daoMock.delete(anyInt());
		expectLastCall().andThrow(new IllegalArgumentException());
		replay(daoMock);
		
		final Response response = resource.delete(1);
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testModify() throws Exception {
		final Person person = existentPerson();
		person.setName(newName());
		person.setSurname(newSurname());
		
		daoMock.modify(person);
		
		replay(daoMock);

		final Response response = resource.modify(
			person.getId(), person.getName(), person.getSurname());
		
		assertThat(response, hasOkStatus());
		assertEquals(person, response.getEntity());
	}

	@Test
	public void testModifyDAOException() throws Exception {
		daoMock.modify(anyObject());
		expectLastCall().andThrow(new DAOException());
		
		replay(daoMock);

		final Response response = resource.modify(existentId(), newName(), newSurname());
		
		assertThat(response, hasInternalServerErrorStatus());
	}

	@Test
	public void testModifyIllegalArgumentException() throws Exception {
		daoMock.modify(anyObject());
		expectLastCall().andThrow(new IllegalArgumentException());
		
		replay(daoMock);

		final Response response = resource.modify(existentId(), newName(), newSurname());
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testModifyNullPointerException() throws Exception {
		daoMock.modify(anyObject());
		expectLastCall().andThrow(new NullPointerException());
		
		replay(daoMock);

		final Response response = resource.modify(existentId(), newName(), newSurname());
		
		assertThat(response, hasBadRequestStatus());
	}

	@Test
	public void testAdd() throws Exception {
		expect(daoMock.add(newName(), newSurname()))
			.andReturn(newPerson());
		replay(daoMock);
		

		final Response response = resource.add(newName(), newSurname());
		
		assertThat(response, hasOkStatus());
		assertThat((Person) response.getEntity(), is(equalsToPerson(newPerson())));
	}

	@Test
	public void testAddDAOException() throws Exception {
		expect(daoMock.add(anyString(), anyString()))
			.andThrow(new DAOException());
		replay(daoMock);

		final Response response = resource.add(newName(), newSurname());
		
		assertThat(response, hasInternalServerErrorStatus());
	}

	@Test
	public void testAddIllegalArgumentException() throws Exception {
		expect(daoMock.add(anyString(), anyString()))
			.andThrow(new IllegalArgumentException());
		replay(daoMock);
		
		final Response response = resource.add(newName(), newSurname());
		
		assertThat(response, hasBadRequestStatus());
	}
}
