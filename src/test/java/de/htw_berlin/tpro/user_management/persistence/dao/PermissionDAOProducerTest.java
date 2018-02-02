package de.htw_berlin.tpro.user_management.persistence.dao;

import org.junit.Assert;
import org.junit.Test;

//@RunWith(Arquillian.class)
public class PermissionDAOProducerTest {
//
//    @Deployment
//    public static JavaArchive createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class)
//    		.addClass(GenericDAO.class)
//    		.addClass(PermissionDAO.class)
//    		.addClass(PermissionDAOProducer.class)
//            .addManifestResource(EmptyAsset.INSTANCE, "beans.xml")
//            .addResource("../test-persistence.xml", "META-INF/persistence.xml");
//    }
//
//    @Inject
//    GenericDAO<Permission> permissionDAO;

    @Test
	public void producerShouldProducePermissionDAOInstance() {
//		Assert.assertNotEquals(null, permissionDAO);
    	Assert.assertNotEquals("Not implemented jet.", null);
    }
}