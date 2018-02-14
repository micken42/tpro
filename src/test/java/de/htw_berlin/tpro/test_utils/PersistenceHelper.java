package de.htw_berlin.tpro.test_utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceHelper {

	public static final EntityManagerFactory emf = 
    		Persistence.createEntityManagerFactory("user_management");
	
	public static void execute(String query) {
		EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        em.createNativeQuery(query).executeUpdate();
        
        em.getTransaction().commit();
        em.close();
	}
	
}
