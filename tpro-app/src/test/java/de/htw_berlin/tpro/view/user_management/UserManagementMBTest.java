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

import de.htw_berlin.tpro.view.UserManager;

@RunWith(Arquillian.class)
public class UserManagementMBTest {
	
	final Logger logger = LoggerFactory.getLogger(UserManagementMBTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(UserManager.class)
            .addPackage("de.htw_berlin.tpro.user_management.mb")
            .addPackage("de.htw_berlin.tpro.user_management.model")
            .addPackage("de.htw_berlin.tpro.user_management.persistence")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    UserManager userManagementMB;

    @Test
    public void shouldCreateCredentials() {
        Assert.assertNotNull(userManagementMB);
    }
    
}