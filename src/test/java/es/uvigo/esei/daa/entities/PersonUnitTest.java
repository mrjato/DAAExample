package es.uvigo.esei.daa.entities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PersonUnitTest {
	@Test
	public void testPersonIntStringString() {
		final int id = 1;
		final String name = "John";
		final String surname = "Doe";
		
		final Person person = new Person(id, name, surname);
		
		assertThat(person.getId(), is(equalTo(id)));
		assertThat(person.getName(), is(equalTo(name)));
		assertThat(person.getSurname(), is(equalTo(surname)));
	}

	@Test(expected = NullPointerException.class)
	public void testPersonIntStringStringNullName() {
		new Person(1, null, "Doe");
	}
	
	@Test(expected = NullPointerException.class)
	public void testPersonIntStringStringNullSurname() {
		new Person(1, "John", null);
	}

	@Test
	public void testSetName() {
		final int id = 1;
		final String surname = "Doe";
		
		final Person person = new Person(id, "John", surname);
		person.setName("Juan");
		
		assertThat(person.getId(), is(equalTo(id)));
		assertThat(person.getName(), is(equalTo("Juan")));
		assertThat(person.getSurname(), is(equalTo(surname)));
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullName() {
		final Person person = new Person(1, "John", "Doe");
		
		person.setName(null);
	}

	@Test
	public void testSetSurname() {
		final int id = 1;
		final String name = "John";
		
		final Person person = new Person(id, name, "Doe");
		person.setSurname("Dolores");
		
		assertThat(person.getId(), is(equalTo(id)));
		assertThat(person.getName(), is(equalTo(name)));
		assertThat(person.getSurname(), is(equalTo("Dolores")));
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullSurname() {
		final Person person = new Person(1, "John", "Doe");
		
		person.setSurname(null);
	}

	@Test
	public void testEqualsObject() {
		final Person personA = new Person(1, "Name A", "Surname A");
		final Person personB = new Person(1, "Name B", "Surname B");
		
		assertTrue(personA.equals(personB));
	}

	@Test
	public void testEqualsHashcode() {
		EqualsVerifier.forClass(Person.class)
			.withIgnoredFields("name", "surname")
			.suppress(Warning.STRICT_INHERITANCE)
			.suppress(Warning.NONFINAL_FIELDS)
		.verify();
	}
}
