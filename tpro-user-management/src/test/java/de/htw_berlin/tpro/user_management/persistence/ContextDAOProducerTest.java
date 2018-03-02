package de.htw_berlin.tpro.user_management.persistence;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.persistence.ContextDAO;
import de.htw_berlin.tpro.user_management.persistence.ContextDAOProducer;
import de.htw_berlin.tpro.user_management.persistence.DefaultContextDAO;
import de.htw_berlin.tpro.user_management.persistence.GenericDAO;

@RunWith(Arquillian.class)
public class ContextDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    			.addClasses(GenericDAO.class, ContextDAO.class, ContextDAOProducer.class);
    }

    @Inject @DefaultContextDAO
    GenericDAO<Context> contextDAO;

    @Test
	public void contextDAOProducerShouldProducePermissionDAOInstance() {
		Assert.assertNotEquals(null, contextDAO);
    }

    @Test
	public void producedContextDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	contextDAO.beginTransaction();
    	EntityManager em = contextDAO.getEntityManager();
		Assert.assertNotEquals(null, em);

    	contextDAO.closeTransaction();
    }
}