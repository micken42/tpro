package de.htw_berlin.tpro.user_management.persistence.dao;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.persistence.dao.DefaultPermissionDAO;
import de.htw_berlin.tpro.user_management.persistence.dao.GenericDAO;

@Dependent
@DefaultPermissionFacade
public class PermissionFacadeImpl implements PermissionFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultPermissionDAO	
	GenericDAO<Permission> permissionDAO;

	@Override
	public void updateAllPermissions(List<Permission> permissions) {
		permissionDAO.beginTransaction();
		permissions.forEach(permissionDAO::update);
		permissionDAO.flush();
		permissionDAO.commitAndCloseTransaction();
	}

	@Override
	public void updatePermission(Permission permission) {
		permissionDAO.beginTransaction();
		permissionDAO.update(permission);
		permissionDAO.commitAndCloseTransaction();
	}

	@Override
	public void savePermission(Permission permission) {
		permissionDAO.beginTransaction();
		permissionDAO.save(permission);
		permissionDAO.commitAndCloseTransaction();
	}

	@Override
	public List<Permission> getAllPermissions() {
		permissionDAO.beginTransaction();
		List<Permission> permissions = permissionDAO.findAll();
		permissionDAO.closeTransaction();
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
		permissionDAO.closeTransaction();
		return permission;
	}
	
}