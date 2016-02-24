package es.uvigo.esei.daa.matchers;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * An abstract {@link Matcher} that can be used to create new matchers that
 * compare entities by their attributes.
 *
 * @author Miguel Reboiro Jato
 *
 * @param <T> the type of the entities to be matched.
 */
public abstract class IsEqualToEntity<T> extends TypeSafeMatcher<T> {
	/**
	 * The expected entity.
	 */
	protected final T expected;

	private Consumer<Description> describeTo;

	/**
	 * Constructs a new instance of {@link IsEqualToEntity}.
	 *
	 * @param entity the expected tentity.
	 */
	public IsEqualToEntity(final T entity) {
		this.expected = requireNonNull(entity);
	}

	@Override
	public void describeTo(final Description description) {
		if (this.describeTo != null)
            this.describeTo.accept(description);
	}

	/**
	 * Adds a new description using the template:
	 * <p>
	 * {@code <expected class> entity with value '<expected>' for <attribute>}
	 * </p>
	 *
	 * @param attribute the name of the attribute compared.
	 * @param expected the expected value.
	 */
	protected void addTemplatedDescription(final String attribute, final Object expected) {
		this.describeTo = d -> d.appendText(String.format(
			"%s entity with value '%s' for %s",
			this.expected.getClass().getSimpleName(),
			expected, attribute
		));
	}

	/**
	 * Adds as the description of this matcher the
	 * {@link Matcher#describeTo(Description)} method of other matcher.
	 *
	 * @param matcher the matcher whose description will be used.
	 */
	protected void addMatcherDescription(final Matcher<?> matcher) {
		this.describeTo = matcher::describeTo;
	}

	/**
	 * Cleans the current description.
	 */
	protected void clearDescribeTo() {
		this.describeTo = null;
	}
	
	protected <R> boolean checkAttribute(
		final String attribute,
		final Function<T, R> getter, final T actual,
		final Function<R, Matcher<R>> matcherFactory
	) {
		final R expectedValue = getter.apply(this.expected);
		final R actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || actualValue == null) {
			this.addTemplatedDescription(attribute, expectedValue);
			return false;
		} else {
			final Matcher<R> matcher = matcherFactory.apply(expectedValue);
			if (matcher.matches(actualValue)) {
				return true;
			} else {
				this.addMatcherDescription(matcher);
				
				return false;
			}
		}
	}

	/**
	 * Compares the expected and the actual value of an array attribute. The
	 * elements of the attribute will be checked using a custom matcher.
	 * If the comparison fails, the description of the error will be updated.
	 *
	 * @param attribute the name of the attribute compared.
	 * @param getter the getter function of the attribute.
	 * @param actual the actual entity being compared to the expected entity.
	 * @param matcherFactory a function that creates a matcher for the expected
	 * array values.
	 * @param <R> type of the value returned by the getter.
	 * @return {@code true} if the value of the expected and actual attributes
	 * are equals and {@code false} otherwise. If the result is {@code false},
	 * the current description will be updated.
	 */
	protected <R> boolean checkArrayAttribute(
		final String attribute,
		final Function<T, R[]> getter, final T actual,
		final Function<R[], Matcher<Iterable<? extends R>>> matcherFactory
	) {
		final R[] expectedValue = getter.apply(this.expected);
		final R[] actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || actualValue == null) {
			this.addTemplatedDescription(attribute, expectedValue);
			return false;
		} else {
			final Matcher<Iterable<? extends R>> matcher =
				matcherFactory.apply(expectedValue);
			
			if (matcher.matches(asList(actualValue))) {
				return true;
			} else {
				this.addMatcherDescription(matcher);
				
				return false;
			}
		}
	}

	/**
	 * Compares the expected and the actual value of an iterable attribute. The
	 * elements of the attribute will be checked using a custom matcher.
	 * If the comparison fails, the description of the error will be updated.
	 *
	 * @param attribute the name of the attribute compared.
	 * @param getter the getter function of the attribute.
	 * @param actual the actual entity being compared to the expected entity.
	 * @param matcherFactory a function that creates a matcher for the expected
	 * iterable values.
	 * @param <R> type of the value returned by the getter.
	 * @return {@code true} if the value of the expected and actual attributes
	 * are equals and {@code false} otherwise. If the result is {@code false},
	 * the current description will be updated.
	 */
	protected <R> boolean checkIterableAttribute(
		final String attribute,
		final Function<T, Iterable<R>> getter, final T actual,
		final Function<Iterable<R>, Matcher<Iterable<? extends R>>> matcherFactory
	) {
		final Iterable<R> expectedValue = getter.apply(this.expected);
		final Iterable<R> actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || actualValue == null) {
			this.addTemplatedDescription(attribute, expectedValue);
			return false;
		} else {
			final Matcher<Iterable<? extends R>> matcher =
				matcherFactory.apply(expectedValue);
			
			if (matcher.matches(actualValue)) {
				return true;
			} else {
				this.addMatcherDescription(matcher);
				
				return false;
			}
		}
	}

	/**
	 * Compares the expected and the actual value of an attribute. If the
	 * comparison fails, the description of the error will be updated.
	 *
	 * @param attribute the name of the attribute compared.
	 * @param getter the getter function of the attribute.
	 * @param actual the actual entity being compared to the expected entity.
	 * @param <R> type of the value returned by the getter.
	 * @return {@code true} if the value of the expected and actual attributes
	 * are equals and {@code false} otherwise. If the result is {@code false},
	 * the current description will be updated.
	 */
	protected <R> boolean checkAttribute(
		final String attribute, final Function<T, R> getter, final T actual
	) {
		final R expectedValue = getter.apply(this.expected);
		final R actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || !expectedValue.equals(actualValue)) {
			this.addTemplatedDescription(attribute, expectedValue);
			return false;
		} else {
            return true;
		}
	}

	/**
	 * Compares the expected and the actual value of an array attribute. If the
	 * comparison fails, the description of the error will be updated.
	 *
	 * @param attribute the name of the attribute compared.
	 * @param getter the getter function of the attribute.
	 * @param actual the actual entity being compared to the expected entity.
	 * @param <R> type of the value returned by the getter.
	 * @return {@code true} if the value of the expected and actual attributes
	 * are equals and {@code false} otherwise. If the result is {@code false},
	 * the current description will be updated.
	 */
	protected <R> boolean checkArrayAttribute(
		final String attribute, final Function<T, R[]> getter, final T actual
	) {
		final R[] expectedValue = getter.apply(this.expected);
		final R[] actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || actualValue == null) {
			this.addTemplatedDescription(attribute, expectedValue == null ? "null" : Arrays.toString(expectedValue));
			return false;
		} else if (!Arrays.equals(expectedValue, actualValue)) {
			this.addTemplatedDescription(attribute, Arrays.toString(expectedValue));
			return false;
		} else
            return true;
	}

	/**
	 * Compares the expected and the actual value of an int array attribute. If
	 * the comparison fails, the description of the error will be updated.
	 *
	 * @param attribute the name of the attribute compared.
	 * @param getter the getter function of the attribute.
	 * @param actual the actual entity being compared to the expected entity.
	 * @param <R> type of the value returned by the getter.
	 * @return {@code true} if the value of the expected and actual attributes
	 * are equals and {@code false} otherwise. If the result is {@code false},
	 * the current description will be updated.
	 */
	protected boolean checkIntArrayAttribute(
		final String attribute, final Function<T, int[]> getter, final T actual
	) {
		final int[] expectedValue = getter.apply(this.expected);
		final int[] actualValue = getter.apply(actual);

		if (expectedValue == null && actualValue == null) {
			return true;
		} else if (expectedValue == null || actualValue == null) {
			this.addTemplatedDescription(attribute, expectedValue == null ? "null" : Arrays.toString(expectedValue));
			return false;
		} else if (!Arrays.equals(expectedValue, actualValue)) {
			this.addTemplatedDescription(attribute, Arrays.toString(expectedValue));
			return false;
		} else
            return true;
	}

	/**
	 * Utility method that generates a {@link Matcher} that compares several
	 * entities.
	 *
	 * @param converter a function to create a matcher for an entity.
	 * @param entities the entities to be used as the expected values.
	 * @param <T> type of the entity.
	 * @return a new {@link Matcher} that compares several entities.
	 */
	@SafeVarargs
	protected static <T> Matcher<Iterable<? extends T>> containsEntityInAnyOrder(
		final Function<T, Matcher<? super T>> converter, final T ... entities
	) {
		final Collection<Matcher<? super T>> entitiesMatchers = stream(entities)
			.map(converter)
		.collect(toList());

		return containsInAnyOrder(entitiesMatchers);
	}

	/**
	 * Utility method that generates a {@link Matcher} that compares several
	 * entities.
	 *
	 * @param converter a function to create a matcher for an entity.
	 * @param entities the entities to be used as the expected values.
	 * @param <T> type of the entity.
	 * @return a new {@link Matcher} that compares several entities.
	 */
	protected static <T> Matcher<Iterable<? extends T>> containsEntityInAnyOrder(
		final Function<T, Matcher<? super T>> converter, final Iterable<T> entities
	) {
		final Collection<Matcher<? super T>> entitiesMatchers =
			StreamSupport.stream(entities.spliterator(), false)
				.map(converter)
			.collect(toList());

		return containsInAnyOrder(entitiesMatchers);
	}

    /**
     * Utility method that generates a {@link Matcher} that compares several
     * entities in the same received order.
     *
     * @param converter A function to create a matcher for an entity.
     * @param entities The entities to be used as the expected values, in the
     *        order to be compared.
     * @param <T> The type of the entity.
     *
     * @return A new {@link Matcher} that compares several entities in the same
     *         received order.
     */
    @SafeVarargs
    protected static <T> Matcher<Iterable<? extends T>> containsEntityInOrder(
        final Function<T, Matcher<? super T>> converter, final T ... entities
    ) {
        return contains(stream(entities).map(converter).collect(toList()));
    }

    /**
     * Utility method that generates a {@link Matcher} that compares several
     * entities in the same received order.
     *
     * @param converter A function to create a matcher for an entity.
     * @param entities The entities to be used as the expected values, in the
     *        order to be compared.
     * @param <T> The type of the entity.
     *
     * @return A new {@link Matcher} that compares several entities in the same
     *         received order.
     */
    protected static <T> Matcher<Iterable<? extends T>> containsEntityInOrder(
        final Function<T, Matcher<? super T>> converter, final Iterable<T> entities
    ) {
        final List<Matcher<? super T>> matchersList =
        	StreamSupport.stream(entities.spliterator(), false)
        		.map(converter)
        	.collect(toList());
        
		return contains(matchersList);
    }
}
