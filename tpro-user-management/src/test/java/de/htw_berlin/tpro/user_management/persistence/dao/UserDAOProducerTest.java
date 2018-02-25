package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.test_utils.DeploymentHelper;
import de.htw_berlin.tpro.user_management.model.User;

@RunWith(Arquillian.class)
public class UserDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    		.addClasses(GenericDAO.class, UserDAO.class, UserDAOProducer.class);
    }

    @Inject @DefaultUserDAO
    GenericDAO<User> userDAO;

    @Test
	public void userDAOProducerShouldProducePermissionDAOInstance() {
		Assert.assertNotEquals(null, userDAO);
    }

    @Test
    public void producedUserDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	userDAO.beginTransaction();
    	EntityManager em = userDAO.getEntityManager();
    	userDAO.closeTransaction();

    	Assert.assertNotEquals(null, em);
    }
}