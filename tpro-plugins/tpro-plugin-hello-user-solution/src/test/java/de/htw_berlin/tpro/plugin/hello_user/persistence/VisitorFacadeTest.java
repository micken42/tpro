package de.htw_berlin.tpro.plugin.hello_user.persistence;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;
import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.test_utils.PersistenceHelper;

@RunWith(Arquillian.class)
public class VisitorFacadeTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
			.addClasses(GenericDAO.class, VisitorDAO.class, VisitorDAOProducer.class, Visitor.class)
	    	.addClasses(VisitorFacade.class, VisitorFacadeImpl.class, DefaultVisitorFacade.class);
	}
	
	@Inject @DefaultVisitorFacade
	VisitorFacade visitorFacade;

	@Before
	public void initTestData() {
		PersistenceHelper.execute("INSERT INTO Visitor (id, fullname, role) VALUES (1, \"Michael Baumert\", \"Student\")");
		PersistenceHelper.execute("INSERT INTO Visitor (id, fullname, role) VALUES (2, \"Stephan Salinger\", \"Dozent\")");
	}
	
	@After
	public void clearTestData() {
		PersistenceHelper.execute("DELETE FROM Visitor");
	}
	
	@Test
	public void defaultVisitorFacadeShouldBeInjected() {
		Assert.assertNotEquals(null, visitorFacade);
	}
	
	@Test 
	public void getAllVisitorsShouldReturn2Visitors() {
		List<Visitor> visitors = visitorFacade.getAllVisitors();
		
		boolean sevenVisitorsReturned = (visitors.size() == 2);
		
		Assert.assertTrue(sevenVisitorsReturned);	
	}
}