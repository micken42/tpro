package de.htw_berlin.tpro.view.user_management;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.htw_berlin.tpro.view.UserManagement;

@RunWith(Arquillian.class)
public class UserManagementTest {
	
	final Logger logger = LoggerFactory.getLogger(UserManagementTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
	    		.addPackage("de.htw_berlin.tpro.framework")
	    		.addPackage("de.htw_berlin.tpro.controller")
	            .addPackage("de.htw_berlin.tpro.view")
	            .addPackage("de.htw_berlin.tpro.user_management.mb")
	            .addPackage("de.htw_berlin.tpro.user_management.model")
	            .addPackage("de.htw_berlin.tpro.user_management.persistence")
	            .addPackage("de.htw_berlin.tpro.user_management.service")
	            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    UserManagement userManagement;

    @Test
    public void shouldCreateCredentials() {
        Assert.assertNotNull(userManagement);
    }
    
}