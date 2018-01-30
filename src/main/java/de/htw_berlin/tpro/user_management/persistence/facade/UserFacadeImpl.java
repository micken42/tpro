package de.htw_berlin.tpro.user_management.persistence.facade;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultUserDAO;
import de.htw_berlin.tpro.user_management.persistence.dao.GenericDAO;

@DefaultUserFacade
@Dependent
public class UserFacadeImpl implements UserFacade {

	private static final long serialVersionUID = 1L;
	
	// TODO: WHY IS THE INJECTION NOT WORKING ???
	@Inject @DefaultUserDAO
	GenericDAO<User> userDAO;
	
	@Override
	public void updateAllUsers(List<User> users) {
		userDAO.beginTransaction();
		users.forEach(userDAO::update);
		userDAO.flush();
		userDAO.commitAndCloseTransaction();
	}
	
	@Override
	public void updateUser(User user) {
		userDAO.beginTransaction();
		userDAO.update(user);
		userDAO.commitAndCloseTransaction();
	}
	
	@Override
	public void saveUser(User user) {
		userDAO.beginTransaction();
		userDAO.save(user);
		userDAO.commitAndCloseTransaction();
	}
	
	@Override
	public List<User> getAllUsers() {
		userDAO.beginTransaction();
		List<User> users = userDAO.findAll();
		userDAO.closeTransaction();
		return users;
	}

	@Override
	public User getUserByUsername(String username) {
		userDAO.beginTransaction();
		User user;
		try {
			user = (User) userDAO.getEntityManager().createNamedQuery("User.findByUsername")
					.setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			user = null;
		}
		userDAO.closeTransaction();
		return user;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllUsernames() {
		userDAO.beginTransaction();
		List<String> usernames;
		try {
			usernames = (List<String>) userDAO.getEntityManager()
					.createNamedQuery("User.findAllUsernames").getResultList();
		} catch (NoResultException e) {
			usernames = null;
		}
		userDAO.closeTransaction();
		return usernames;
	}
	
}