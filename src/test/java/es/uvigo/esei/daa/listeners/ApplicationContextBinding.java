package es.uvigo.esei.daa.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApplicationContextBindings.class)
public @interface ApplicationContextBinding {
	public String jndiUrl();
	public String name() default "";
	public Class<?> type() default None.class;
	
	public final static class None {}
}
