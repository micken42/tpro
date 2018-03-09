package de.htw_berlin.tpro.user_management.persistence;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Role;

public interface RoleFacade extends Serializable {

	public void updateAllRoles(List<Role> roles);
	
	public void updateRole(Role role);
	
	public void saveRole(Role role);
	
	public List<Role> getAllRoles();
	
	public List<Role> getRolesByContextName(String context);

	public Role getRoleByRoleAndContextName(String name, String context);

	public void deleteRoleByRoleAndContextName(String name, String context);

	public void deleteAllRoles();
		
}
