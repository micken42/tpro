package de.htw_berlin.tpro.user_management.persistence;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Group;

public interface GroupFacade extends Serializable {

	public void updateAllGroups(List<Group> groups);
	
	public void updateGroup(Group group);
	
	public void saveGroup(Group group);
	
	public List<Group> getAllGroups();
	
	public Group getGroupByName(String name);

	public List<String> getAllNames();

	public List<Group> getGroupsByPermissionAndContextName(String permission, String context);
	
	public List<Group> getGroupsByUsername(String username);

	public void deleteGroupByName(String name);

	public void deleteAllGroups();
		
}
