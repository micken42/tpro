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

import de.htw_berlin.tpro.view.Credentials;

@RunWith(Arquillian.class)
public class CredentialsTest {
	
	final Logger logger = LoggerFactory.getLogger(CredentialsTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(Credentials.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    Credentials credentials;

    @Test
    public void shouldCreateCredentials() {
        Assert.assertNotEquals(null, credentials);
    }
    
}