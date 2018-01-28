package de.htw_berlin.tpro.users.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.users.model.User;

@ApplicationScoped
public class UserFacade {

	@Inject @UserData
	private GenericDAO<User> userDAO;
	
	public void update(List<User> users) {
		userDAO.beginTransaction();
		users.forEach(userDAO::update);
		userDAO.flush();
		userDAO.commitAndCloseTransaction();
	}
	
	public void save(User user) {
		userDAO.beginTransaction();
		userDAO.save(user);
		userDAO.commitAndCloseTransaction();
	}
	
	public List<User> load() {
		userDAO.beginTransaction();
		List<User> users = userDAO.findAll();
		userDAO.closeTransaction();
		return users;
	}

	public User checkCredentials(String username, String password) {
		userDAO.beginTransaction();
		User user; 
		try {
			user = (User) userDAO.getEntityManager().createNamedQuery("User.findByUsername")
					.setParameter("username", username).getSingleResult(); // same as .getSingleResult
		} catch (NoResultException e) {
			return null;
		}
		userDAO.closeTransaction();
		return (user.getPassword().equals(password)) ? user : null;
	}
	
}