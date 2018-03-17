package de.htw_berlin.tpro.plugin.hello_user.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;
import de.htw_berlin.tpro.test_utils.DeploymentHelper;

@RunWith(Arquillian.class)
public class VisitorDAOProducerTest {

	@Deployment
	public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addClasses(GenericDAO.class, VisitorDAO.class, VisitorDAOProducer.class, Visitor.class);
	}
	
    @Inject @DefaultVisitorDAO
    GenericDAO<Visitor> visitorDAO;

    @Test
	public void visitorDAOProducerShouldProduceVisitorDAOInstance() {
		Assert.assertNotEquals(null, visitorDAO);
    }

    @Test
	public void producedVisitorDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	visitorDAO.beginTransaction();
    	EntityManager em = visitorDAO.getEntityManager();
		Assert.assertNotEquals(null, em);

    	visitorDAO.closeTransaction();
    }
}