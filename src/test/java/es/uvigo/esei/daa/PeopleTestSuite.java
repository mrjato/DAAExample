package es.uvigo.esei.daa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.esei.daa.dao.PeopleDAOTest;
import es.uvigo.esei.daa.rest.PeopleTest;
import es.uvigo.esei.daa.web.PeopleWebTest;

@SuiteClasses({ PeopleDAOTest.class, PeopleTest.class, PeopleWebTest.class })
@RunWith(Suite.class)
public class PeopleTestSuite {
}
