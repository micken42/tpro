package de.htw_berlin.tpro.plugin.hello_user.mb;

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

import de.htw_berlin.tpro.plugin.hello_user.mb.HelloUserMB;

@RunWith(Arquillian.class)
public class HelloUserMBTest {
	
	final Logger logger = LoggerFactory.getLogger(HelloUserMBTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(HelloUserMB.class)
            .addPackage("de.htw_berlin.tpro.plugin.hello_user.model")
            .addPackage("de.htw_berlin.tpro.plugin.hello_user.persistence")
            .addPackage("de.htw_berlin.tpro.user_management.model")
            .addPackage("de.htw_berlin.tpro.user_management.persistence")
            .addPackage("de.htw_berlin.tpro.user_management.service")
            .addPackage("de.htw_berlin.tpro.framework")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    HelloUserMB helloUserMB;

    @Test
    public void shouldCreateHelloUserMB() {
        Assert.assertNotNull(helloUserMB);
    }
    
}