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
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.persistence.DefaultRoleDAO;
import de.htw_berlin.tpro.user_management.persistence.GenericDAO;
import de.htw_berlin.tpro.user_management.persistence.RoleDAO;
import de.htw_berlin.tpro.user_management.persistence.RoleDAOProducer;

@RunWith(Arquillian.class)
public class RoleDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    		.addClasses(GenericDAO.class, RoleDAO.class, RoleDAOProducer.class);
    }

    @Inject @DefaultRoleDAO
    GenericDAO<Role> roleDAO;

    @Test
	public void roleDAOProducerShouldProduceRoleDAOInstance() {
		Assert.assertNotEquals(null, roleDAO);
    }

    @Test
	public void producedRoleDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	roleDAO.beginTransaction();
    	EntityManager em = roleDAO.getEntityManager();
		Assert.assertNotEquals(null, em);

		roleDAO.closeTransaction();
    }
}