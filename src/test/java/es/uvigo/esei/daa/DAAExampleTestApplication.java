package es.uvigo.esei.daa;

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import es.uvigo.esei.daa.filters.AuthorizationFilter;

@ApplicationPath("/rest/*")
public class DAAExampleTestApplication extends DAAExampleApplication {
	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>(super.getClasses());
		
		classes.add(AuthorizationFilter.class);
		
		return unmodifiableSet(classes);
	}
}
