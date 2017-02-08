package es.uvigo.esei.daa;

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import es.uvigo.esei.daa.rest.PeopleResource;
import es.uvigo.esei.daa.rest.UsersResource;

/**
 * Configuration of the REST application. This class includes the resources and
 * configuration parameter used in the REST API of the application.
 * 
 * @author Miguel Reboiro Jato
 *
 */
@ApplicationPath("/rest/*")
public class DAAExampleApplication extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		return Stream.of(
			PeopleResource.class,
			UsersResource.class
		).collect(toSet());
	}
	
	@Override
	public Map<String, Object> getProperties() {
		// Activates JSON automatic conversion in JAX-RS
		return Collections.singletonMap(
			"com.sun.jersey.api.json.POJOMappingFeature", true
		);
	}
}
