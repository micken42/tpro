package de.htw_berlin.tpro.user_management.persistence.dao;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Permission;

public interface PermissionFacade extends Serializable {

	public void updateAllPermissions(List<Permission> permissions);
	
	public void updatePermission(Permission permission);
	
	public void savePermission(Permission permission);
	
	public List<Permission> getAllPermissions();
	
	public List<Permission> getPermissionsByContextName(String context);

	public Permission getPermissionByPermissionAndContextName(String name, String context);

	public void deletePermission(Permission permission);
		
}
