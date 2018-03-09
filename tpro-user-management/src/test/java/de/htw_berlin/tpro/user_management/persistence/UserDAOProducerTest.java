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
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.DefaultUserDAO;
import de.htw_berlin.tpro.user_management.persistence.GenericDAO;
import de.htw_berlin.tpro.user_management.persistence.UserDAO;
import de.htw_berlin.tpro.user_management.persistence.UserDAOProducer;

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
	public void userDAOProducerShouldProduceRoleDAOInstance() {
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