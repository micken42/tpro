package de.htw_berlin.tpro.plugin.bookstore.mb;

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

@RunWith(Arquillian.class)
public class BookListMBTest {
	
	final Logger logger = LoggerFactory.getLogger(BookListMBTest.class);

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addPackage("de.htw_berlin.tpro.plugin.bookstore.mb")
            .addPackage("de.htw_berlin.tpro.plugin.bookstore.model")
            .addPackage("de.htw_berlin.tpro.plugin.bookstore.persistence")
            .addPackage("de.htw_berlin.tpro.user_management.mb")
            .addPackage("de.htw_berlin.tpro.user_management.model")
            .addPackage("de.htw_berlin.tpro.user_management.persistence")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    BookListMB bookList;

    @Test
    public void shouldCreateCredentials() {
        Assert.assertNotNull(bookList);
    }
    
}