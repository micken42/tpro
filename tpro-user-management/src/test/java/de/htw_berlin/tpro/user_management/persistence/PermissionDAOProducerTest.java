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
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.DefaultPermissionDAO;
import de.htw_berlin.tpro.user_management.persistence.GenericDAO;
import de.htw_berlin.tpro.user_management.persistence.PermissionDAO;
import de.htw_berlin.tpro.user_management.persistence.PermissionDAOProducer;

@RunWith(Arquillian.class)
public class PermissionDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
    	return DeploymentHelper.createDefaultDeployment()
    		.addClasses(GenericDAO.class, PermissionDAO.class, PermissionDAOProducer.class);
    }

    @Inject @DefaultPermissionDAO
    GenericDAO<Permission> permissionDAO;

    @Test
	public void permissionDAOProducerShouldProducePermissionDAOInstance() {
		Assert.assertNotEquals(null, permissionDAO);
    }

    @Test
	public void producedPermissionDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
    	permissionDAO.beginTransaction();
    	EntityManager em = permissionDAO.getEntityManager();
		Assert.assertNotEquals(null, em);

		permissionDAO.closeTransaction();
    }
}