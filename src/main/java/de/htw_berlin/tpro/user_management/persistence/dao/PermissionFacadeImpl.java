package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import de.htw_berlin.tpro.user_management.model.Context;
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
		Permission existingPermission = 
				getPermissionByPermissionAndContextName(permission.getName(), permission.getContext().getName());
		if (existingPermission != null) throw new PersistenceException();
		
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
		Context permissionContext = permission.getContext();
		Permission existingPermission = 
				getPermissionByPermissionAndContextName(permission.getName(), permissionContext.getName());
		if (existingPermission != null) throw new PersistenceException();
		
		permissionContext.addPermission(permission);
		contextFacade.updateContext(permissionContext);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Permission> getAllPermissions() {
		permissionDAO.beginTransaction();
		ArrayList<Permission> permissions;
		try {
			permissions = (ArrayList<Permission>) permissionDAO.getEntityManager()
					.createNamedQuery("Context.findAll").getResultList();
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

	@Override
	public void deletePermission(Permission permission) {
		Context permissionContext = permission.getContext();
		ArrayList<User> linkedUsers = 
				(ArrayList<User>) getUsersPermissionIsAssignedTo(permission);
		if (linkedUsers != null) {
			deletePermissionFromUsers(permission, linkedUsers);
		}
		permissionContext.removePermission(permission);
		contextFacade.updateContext(permissionContext);
	}
	
	private List<User> getUsersPermissionIsAssignedTo(Permission permission) {
		String permissionName = permission.getName();
		String contextName = permission.getContext().getName();
		
		ArrayList<User> users = 
				(ArrayList<User>) userFacade.getUsersByPermissionAndContextName(permissionName, contextName);
		
		return users;
	}
	
	private void deletePermissionFromUsers(Permission permission, List<User> users) {
	    users.forEach(user -> user.removePermission(permission));
	    userFacade.updateAllUsers(users);
	}
	
}