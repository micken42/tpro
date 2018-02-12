package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;

@RunWith(Arquillian.class)
public class PermissionDAOProducerTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
    		.addClass(GenericDAO.class)
    		.addClass(PermissionDAO.class)
    		.addClass(PermissionDAOProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsResource("META-INF/test-data.sql", "META-INF/test-data.sql");
    }

    @Inject @DefaultPermissionDAO
    GenericDAO<Permission> permissionDAO;

    @Test
	public void permissionDAOProducerShouldProducePermissionDAOInstance() {
		Assert.assertNotEquals(null, permissionDAO);
    }

    @Test
	public void producedPermissionDAOInstanceShouldBeAbleToCreateEntityManagerInstance() {
		Assert.assertNotEquals(null, permissionDAO.getEntityManager());
    }
}