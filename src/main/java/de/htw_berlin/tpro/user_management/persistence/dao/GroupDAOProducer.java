package de.htw_berlin.tpro.user_management.persistence.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class GroupDAOProducer {

	private static GroupDAO groupDAO = new GroupDAO();

	@Produces @DefaultGroupDAO
	public GroupDAO getGroupDAO() {
		return groupDAO;
	}
	
}
