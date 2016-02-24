package es.uvigo.esei.daa.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import es.uvigo.esei.daa.entities.Person;

public class IsEqualToPerson extends IsEqualToEntity<Person> {
	public IsEqualToPerson(Person entity) {
		super(entity);
	}

	@Override
	protected boolean matchesSafely(Person actual) {
		this.clearDescribeTo();
		
		if (actual == null) {
			this.addTemplatedDescription("actual", expected.toString());
			return false;
		} else {
			return checkAttribute("id", Person::getId, actual)
				&& checkAttribute("name", Person::getName, actual)
				&& checkAttribute("surname", Person::getSurname, actual);
		}
	}

	/**
	 * Factory method that creates a new {@link IsEqualToEntity} matcher with
	 * the provided {@link Person} as the expected value.
	 * 
	 * @param person the expected person.
	 * @return a new {@link IsEqualToEntity} matcher with the provided
	 * {@link Person} as the expected value.
	 */
	@Factory
	public static IsEqualToPerson equalsToPerson(Person person) {
		return new IsEqualToPerson(person);
	}
	
	/**
	 * Factory method that returns a new {@link Matcher} that includes several
	 * {@link IsEqualToPerson} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * 
	 * @param persons the persons to be used as the expected values.
	 * @return a new {@link Matcher} that includes several
	 * {@link IsEqualToPerson} matchers, each one using an {@link Person} of the
	 * provided ones as the expected value.
	 * @see IsEqualToEntity#containsEntityInAnyOrder(java.util.function.Function, Object...)
	 */
	@Factory
	public static Matcher<Iterable<? extends Person>> containsPeopleInAnyOrder(Person ... persons) {
		return containsEntityInAnyOrder(IsEqualToPerson::equalsToPerson, persons);
	}

}
