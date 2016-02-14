package es.uvigo.esei.daa.listeners;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import es.uvigo.esei.daa.listeners.ApplicationContextBinding.None;


public class ApplicationContextJndiBindingTestExecutionListener extends AbstractTestExecutionListener {
	private SimpleNamingContextBuilder contextBuilder;
	
	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		final Class<?> testClass = testContext.getTestClass();
		
		final ApplicationContextBinding[] bindings = testClass.getAnnotationsByType(ApplicationContextBinding.class);
		
		this.contextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		for (ApplicationContextBinding binding : bindings) {
			final String bindingName = binding.name();
			final Class<?> bindingType = binding.type();
			
			Object bean;
			if (bindingName.isEmpty() && bindingType.equals(None.class)) {
				throw new IllegalArgumentException("name or type attributes must be configured in ApplicationContextBinding");
			} else if (bindingName.isEmpty()) {
				bean = testContext.getApplicationContext().getBean(bindingType);
			} else if (bindingType.equals(None.class)) {
				bean = testContext.getApplicationContext().getBean(bindingName);
			} else {
				bean = testContext.getApplicationContext().getBean(bindingName, bindingType);
			}
			
			this.contextBuilder.bind(binding.jndiUrl(), bean);
		}
	}
	
	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		this.contextBuilder.clear();
		this.contextBuilder = null;
	}
}
