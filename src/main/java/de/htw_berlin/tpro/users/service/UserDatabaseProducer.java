package de.htw_berlin.tpro.users.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.htw_berlin.tpro.users.annotation.UserDatabase;

@ApplicationScoped
public class UserDatabaseProducer {

	@Produces 
	@UserDatabase 
	@PersistenceContext(unitName="tpro")
    private EntityManager entityManager;
   
}
