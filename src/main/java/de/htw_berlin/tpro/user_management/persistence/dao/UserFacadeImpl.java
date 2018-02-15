package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;

@Dependent
@DefaultUserFacade
public class UserFacadeImpl implements UserFacade {

	private static final long serialVersionUID = 1L;
	
	@Inject @DefaultUserDAO
	GenericDAO<User> userDAO;
	
	@Override
	public void updateAllUsers(List<User> users) {
		try {
			userDAO.beginTransaction();

			users.forEach(userDAO::update);
			userDAO.flush();

			userDAO.commit();
		} catch (Exception e) {
			EntityTransaction txn = userDAO.getEntityManager().getTransaction();
			if (txn != null && txn.isActive())
				userDAO.rollback();
			throw e;
			// handle the underlying error
		} finally {
			userDAO.closeTransaction();
		}
	}
	
	@Override
	public void updateUser(User user) {
		try {
			userDAO.beginTransaction();

			userDAO.update(user);

			userDAO.commit();
		} catch (Exception e) {
			EntityTransaction txn = userDAO.getEntityManager().getTransaction();
			if (txn != null && txn.isActive())
				userDAO.rollback();
			throw e;
			// handle the underlying error
		} finally {
			userDAO.closeTransaction();
		}
	}
	
	@Override
	public void saveUser(User user) {
		try {
			userDAO.beginTransaction();

			userDAO.save(user);

			userDAO.commit();
		} catch (Exception e) {
			EntityTransaction txn = userDAO.getEntityManager().getTransaction();
			if (txn != null && txn.isActive())
				userDAO.rollback();
			throw e;
			// handle the underlying error
		} finally {
			userDAO.closeTransaction();
		}
	}
	
	@Override
	public List<User> getAllUsers() {
		userDAO.beginTransaction();
		List<User> users = userDAO.findAll();
		userDAO.commitAndCloseTransaction();
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
		userDAO.commitAndCloseTransaction();
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
		userDAO.commitAndCloseTransaction();
		return usernames;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersByGroupName(String name) {
		userDAO.beginTransaction();
		List<User> users;
		try {
			users = userDAO.getEntityManager()
					.createNamedQuery("Group.findAllByGroupName")
					.setParameter("name", name)
					.getResultList();
		} catch (NoResultException e) {
			users = null;
		}
		userDAO.commitAndCloseTransaction();
		return users;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUsersByPermissionAndContextName(String permission, String context) {
		userDAO.beginTransaction();
		List<User> users;
		try {
			users = userDAO.getEntityManager()
					.createNamedQuery("User.findAllByPermissionAndContextName")
					.setParameter("permission", permission)
					.setParameter("context", context)
					.getResultList();
		} catch (NoResultException e) {
			users = null;
		}
		userDAO.commitAndCloseTransaction();
		return users;
	}
	
	@Override
	public void deleteUserByUsername(String username) {
		User user = getUserByUsername(username);
		if (user == null) throw new EntityNotFoundException();

		user.setPermissions(new HashSet<Permission>());
		user.getGroups().forEach(group -> user.removeGroup(group));
		updateUser(user);
		
		Integer id = user.getId();
		try {
			userDAO.beginTransaction();
			
			userDAO.delete(id, User.class);
			
			userDAO.commit();
		} catch (Exception e) {
			EntityTransaction txn = userDAO.getEntityManager().getTransaction();
			if (txn != null && txn.isActive())
				userDAO.rollback();
			throw e;
			// handle the underlying error
		} finally {
			userDAO.closeTransaction();
		}
	}

	@Override
	public void deleteAllUsers() {
		List<User> users = getAllUsers();
		users.forEach(user -> deleteUserByUsername(user.getUsername()));
	}
}