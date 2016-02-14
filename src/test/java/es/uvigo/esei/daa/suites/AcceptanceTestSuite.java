package es.uvigo.esei.daa.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.esei.daa.web.PeopleWebTest;

@SuiteClasses({ 
	PeopleWebTest.class
})
@RunWith(Suite.class)
public class AcceptanceTestSuite {

}
