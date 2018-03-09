package de.htw_berlin.tpro.user_management.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Group;
import de.htw_berlin.tpro.user_management.model.Role;
import de.htw_berlin.tpro.user_management.model.User;

@Dependent
@DefaultRoleFacade
public class RoleFacadeImpl implements RoleFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultRoleDAO	
	GenericDAO<Role> roleDAO;
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Inject @DefaultGroupFacade
	GroupFacade groupFacade;

	@Override
	public void updateAllRoles(List<Role> roles) {
		try {
			roleDAO.beginTransaction();
			
			roles.forEach(roleDAO::update);
			roleDAO.flush();

			roleDAO.commit();
		}
		catch ( Exception e ) {
			EntityTransaction txn = roleDAO.getEntityManager().getTransaction();
			if ( txn != null && txn.isActive() ) 
				roleDAO.rollback();
		    throw e;
		    // handle the underlying error
		}
		finally {
			roleDAO.closeTransaction();
		}
	}

	@Override
	public void updateRole(Role role) throws PersistenceException {
		if (!roleCanBeSaved(role)) throw new PersistenceException();
		
		try {
			roleDAO.beginTransaction();
			
			roleDAO.update(role);

			roleDAO.commit();
		}
		catch ( Exception e ) {
			EntityTransaction txn = roleDAO.getEntityManager().getTransaction();
			if ( txn != null && txn.isActive() ) 
				roleDAO.rollback();
		    throw e;
		    // handle the underlying error
		}
		finally {
			roleDAO.closeTransaction();
		}
	}
	
	@Override 
	public void saveRole(Role role) throws PersistenceException {
		if (!roleCanBeSaved(role)) throw new PersistenceException();
		
		Context roleContext = role.getContext();
		roleContext.addRole(role);
		contextFacade.updateContext(roleContext);
	}
	
	private boolean roleCanBeSaved(Role role) {
		if (role == null) 
			return false;
		
		if (role.getContext().getId() == null) 
			return false;

		Role existingRole = 
				getRoleByRoleAndContextName(role.getName(), role.getContext().getName());
		if (existingRole != null)
			return false;
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Role> getAllRoles() {
		roleDAO.beginTransaction();
		ArrayList<Role> roles;
		try {
			roles = (ArrayList<Role>) roleDAO.getEntityManager()
					.createNamedQuery("Role.findAll").getResultList();
		} catch (NoResultException e) {
			roles = null;
		}
		roleDAO.commitAndCloseTransaction();
		return roles;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Role> getRolesByContextName(String context) {
		roleDAO.beginTransaction();
		ArrayList<Role> roles;
		try {
			roles = (ArrayList<Role>) roleDAO.getEntityManager()
					.createNamedQuery("Role.findByContextName")
					.setParameter("context", context)
					.getResultList();
		} catch (NoResultException e) {
			roles = null;
		}
		roleDAO.commitAndCloseTransaction();
		return roles;
	}

	@Override
	public Role getRoleByRoleAndContextName(String name, String context) {
		roleDAO.beginTransaction();
		Role role;
		try {
			role = (Role) roleDAO.getEntityManager()
					.createNamedQuery("Role.findByRoleAndContextName")
					.setParameter("name", name)
					.setParameter("context", context)
					.getSingleResult();
		} catch (NoResultException e) {
			role = null;
		}
		roleDAO.commitAndCloseTransaction();
		return role;
	}

	@Override // TODO: DeleteBy in andere Facades übernehmen und Tests anpassen
	public void deleteRoleByRoleAndContextName(String name, String context) {
		Role role = getRoleByRoleAndContextName(name, context);
		if (role != null) {
			Context roleContext = role.getContext();
			ArrayList<User> linkedUsers = getUsersRoleIsAssignedTo(role);
			ArrayList<Group> linkedGroups = getGroupsRoleIsAssignedTo(role);
			
			deleteRoleFromUsers(role, linkedUsers);
			deleteRoleFromGroups(role, linkedGroups);
			
			roleContext.removeRole(role);
			// delete role by updating context
			contextFacade.updateContext(roleContext);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	private ArrayList<User> getUsersRoleIsAssignedTo(Role role) {
		String roleName = role.getName();
		String contextName = role.getContext().getName();
		
		ArrayList<User> users = 
				(ArrayList<User>) userFacade.getUsersByRoleAndContextName(roleName, contextName);
		
		return users;
	}
	
	private ArrayList<Group> getGroupsRoleIsAssignedTo(Role role) {
		String roleName = role.getName();
		String contextName = role.getContext().getName();
		
		ArrayList<Group> groups = 
				(ArrayList<Group>) groupFacade.getGroupsByRoleAndContextName(roleName, contextName);
		
		return groups;
	}
	
	private void deleteRoleFromUsers(Role role, List<User> users) {
	    if (users.size() > 0) {
	    	users.forEach(user -> user.removeRole(role));
		    userFacade.updateAllUsers(users);
	    }
	}
	
	private void deleteRoleFromGroups(Role role, List<Group> groups) {
		if (groups.size() > 0) {
			groups.forEach(group -> group.removeRole(role));
			groupFacade.updateAllGroups(groups);
		}
	}
	
	@Override
	public void deleteAllRoles() {
		List<Role> roles = getAllRoles();
		roles.forEach(role -> {
			String contextName = role.getContext().getName();
			deleteRoleByRoleAndContextName(role.getName(), contextName);
		});
	}
	
}