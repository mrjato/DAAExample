package es.uvigo.esei.daa.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbManagement {
	public String[] create() default "";
	public String[] drop() default "";
	public DbManagementAction action() default DbManagementAction.CREATE_DROP; 
}
