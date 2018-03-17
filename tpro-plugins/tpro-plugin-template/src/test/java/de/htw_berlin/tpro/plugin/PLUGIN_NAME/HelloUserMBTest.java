package de.htw_berlin.tpro.plugin.PLUGIN_NAME;

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

import de.htw_berlin.tpro.plugin.PLUGIN_NAME.HelloUserMB;

@RunWith(Arquillian.class)
public class HelloUserMBTest {
	
	final Logger logger = LoggerFactory.getLogger(HelloUserMBTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(HelloUserMB.class)
            .addPackage("de.htw_berlin.tpro.user_management.mb")
            .addPackage("de.htw_berlin.tpro.user_management.model")
            .addPackage("de.htw_berlin.tpro.user_management.persistence")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    HelloUserMB helloUserMB;

    @Test
    public void shouldCreateHelloUserMB() {
        Assert.assertNotNull(helloUserMB);
    }
    
}