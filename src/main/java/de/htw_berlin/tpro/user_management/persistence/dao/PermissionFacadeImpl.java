package de.htw_berlin.tpro.user_management.persistence.dao;

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
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;

@Dependent
@DefaultPermissionFacade
public class PermissionFacadeImpl implements PermissionFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultPermissionDAO	
	GenericDAO<Permission> permissionDAO;
	
	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Inject @DefaultUserFacade
	UserFacade userFacade;
	
	@Inject @DefaultGroupFacade
	GroupFacade groupFacade;

	@Override
	public void updateAllPermissions(List<Permission> permissions) {
		try {
			permissionDAO.beginTransaction();
			
			permissions.forEach(permissionDAO::update);
			permissionDAO.flush();

			permissionDAO.commit();
		}
		catch ( Exception e ) {
			EntityTransaction txn = permissionDAO.getEntityManager().getTransaction();
			if ( txn != null && txn.isActive() ) 
				permissionDAO.rollback();
		    throw e;
		    // handle the underlying error
		}
		finally {
			permissionDAO.closeTransaction();
		}
	}

	@Override
	public void updatePermission(Permission permission) throws PersistenceException {
		if (!permissionCanBeSaved(permission)) throw new PersistenceException();
		
		try {
			permissionDAO.beginTransaction();
			
			permissionDAO.update(permission);

			permissionDAO.commit();
		}
		catch ( Exception e ) {
			EntityTransaction txn = permissionDAO.getEntityManager().getTransaction();
			if ( txn != null && txn.isActive() ) 
				permissionDAO.rollback();
		    throw e;
		    // handle the underlying error
		}
		finally {
			permissionDAO.closeTransaction();
		}
	}
	
	@Override
	public void savePermission(Permission permission) throws PersistenceException {
		if (!permissionCanBeSaved(permission)) throw new PersistenceException();
		
		Context permissionContext = permission.getContext();
		permissionContext.addPermission(permission);
		contextFacade.updateContext(permissionContext);
	}
	
	private boolean permissionCanBeSaved(Permission permission) {
		if (permission == null) 
			return false;
		
		if (permission.getContext().getId() == null) 
			return false;

		Permission existingPermission = 
				getPermissionByPermissionAndContextName(permission.getName(), permission.getContext().getName());
		if (existingPermission != null)
			return false;
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Permission> getAllPermissions() {
		permissionDAO.beginTransaction();
		ArrayList<Permission> permissions;
		try {
			permissions = (ArrayList<Permission>) permissionDAO.getEntityManager()
					.createNamedQuery("Permission.findAll").getResultList();
		} catch (NoResultException e) {
			permissions = null;
		}
		permissionDAO.commitAndCloseTransaction();
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Permission> getPermissionsByContextName(String context) {
		permissionDAO.beginTransaction();
		ArrayList<Permission> permissions;
		try {
			permissions = (ArrayList<Permission>) permissionDAO.getEntityManager()
					.createNamedQuery("Permission.findByContextName")
					.setParameter("context", context)
					.getResultList();
		} catch (NoResultException e) {
			permissions = null;
		}
		permissionDAO.commitAndCloseTransaction();
		return permissions;
	}

	@Override
	public Permission getPermissionByPermissionAndContextName(String name, String context) {
		permissionDAO.beginTransaction();
		Permission permission;
		try {
			permission = (Permission) permissionDAO.getEntityManager()
					.createNamedQuery("Permission.findByPermissionAndContextName")
					.setParameter("name", name)
					.setParameter("context", context)
					.getSingleResult();
		} catch (NoResultException e) {
			permission = null;
		}
		permissionDAO.commitAndCloseTransaction();
		return permission;
	}

	@Override // TODO: DeleteBy in andere Facades übernehmen und Tests anpassen
	public void deletePermissionByPermissionAndContextName(String name, String context) {
		Permission permission = getPermissionByPermissionAndContextName(name, context);
		if (permission != null) {
			Context permissionContext = permission.getContext();
			ArrayList<User> linkedUsers = getUsersPermissionIsAssignedTo(permission);
			ArrayList<Group> linkedGroups = getGroupsPermissionIsAssignedTo(permission);
			
			deletePermissionFromUsers(permission, linkedUsers);
			deletePermissionFromGroups(permission, linkedGroups);
			
			permissionContext.removePermission(permission);
			// delete permission by updating context
			contextFacade.updateContext(permissionContext);
		} else {
			throw new EntityNotFoundException();
		}
	}
	
	private ArrayList<User> getUsersPermissionIsAssignedTo(Permission permission) {
		String permissionName = permission.getName();
		String contextName = permission.getContext().getName();
		
		ArrayList<User> users = 
				(ArrayList<User>) userFacade.getUsersByPermissionAndContextName(permissionName, contextName);
		
		return users;
	}
	
	private ArrayList<Group> getGroupsPermissionIsAssignedTo(Permission permission) {
		String permissionName = permission.getName();
		String contextName = permission.getContext().getName();
		
		ArrayList<Group> groups = 
				(ArrayList<Group>) groupFacade.getGroupsByPermissionAndContextName(permissionName, contextName);
		
		return groups;
	}
	
	private void deletePermissionFromUsers(Permission permission, List<User> users) {
	    if (users.size() > 0) {
	    	users.forEach(user -> user.removePermission(permission));
		    userFacade.updateAllUsers(users);
	    }
	}
	
	private void deletePermissionFromGroups(Permission permission, List<Group> groups) {
		if (groups.size() > 0) {
			groups.forEach(group -> group.removePermission(permission));
			groupFacade.updateAllGroups(groups);
		}
	}
	
	@Override
	public void deleteAllPermissions() {
		List<Permission> permissions = getAllPermissions();
		permissions.forEach(permission -> {
			String contextName = permission.getContext().getName();
			deletePermissionByPermissionAndContextName(permission.getName(), contextName);
		});
	}
	
}