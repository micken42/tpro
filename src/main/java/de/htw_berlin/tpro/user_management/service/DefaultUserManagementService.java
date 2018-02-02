package de.htw_berlin.tpro.user_management.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.persistence.facade.ContextFacade;
import de.htw_berlin.tpro.user_management.persistence.facade.DefaultContextFacade;
import de.htw_berlin.tpro.user_management.persistence.facade.DefaultPermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.facade.DefaultUserFacade;
import de.htw_berlin.tpro.user_management.persistence.facade.PermissionFacade;
import de.htw_berlin.tpro.user_management.persistence.facade.UserFacade;

@ApplicationScoped
@DefaultUserManagement 
public class DefaultUserManagementService implements UserManagementService {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultUserFacade
	UserFacade userFacade;

	@Inject @DefaultPermissionFacade
	PermissionFacade permissionFacade;

	@Inject @DefaultContextFacade
	ContextFacade contextFacade;
	
	@Override
	public User login(String username, String password) {
		User user = userFacade.getUserByUsername(username);
		if (user != null)
			user = (user.getPassword().equals(password)) ? user : null;
		return user;
	}

	@Override
	public User signUp(String username, String password) {
		Set<Permission> initialPerms = new HashSet<Permission>();
		initialPerms.add(permissionFacade.getPermissionByPermissionAndContextName("user", "tpro"));
		User user = new User(username, password, initialPerms);
		userFacade.saveUser(user);
		return user;
	}

	@Override
	public boolean userIsAuthorized(User user, String permission, String context) {
		for (Permission perm : user.getPermissions()) {
			if (perm.getContext().getName().equals(context) 
					&& perm.getName().equals(permission))
				return true;
		}
		return false;
	}
	
	@PostConstruct
	void initMockUsers() {
		System.out.println(" - - - - - - - - I n i t   m o "
				+ "c k   u s e r s - - - - - - - - - ");
		
		// create dummy users 
		User admin = new User("admin", "admin");
		User user = new User("user", "user");
		/*User pluginInstaller = new User("Plugininstallateur", "1234");
		User serviceProvider1 = new User("Dienstanbieter1", "1234");
		User serviceProvider2 = new User("Dienstanbieter2", "1234");
		User serviceConsumer1 = new User("Dienstkonsument1", "1234");
		User serviceConsumer2 = new User("Dienstkonsument2", "1234");*/
		
		// create and save global dummy context and permissions
		Context tproContext = new Context("tpro");
		Permission tproAdminPermission = new Permission("admin", tproContext);
		Permission tproUserPermission = new Permission("user", tproContext);
		HashSet<Permission> contextPerms = new HashSet<Permission>();
		contextPerms.add(tproUserPermission);
		contextPerms.add(tproAdminPermission);
		tproContext.setPermissions(contextPerms);
		contextFacade.saveContext(tproContext);

		// add dummy permissions and (with) contexts for plugin mechanism
		/*Context pluginContext1 = new Context("plugin1");
		Context pluginContext2 = new Context("plugin2");
		Permission pluginProviderPermission1 = new Permission("provider", pluginContext1);
		Permission pluginProviderPermission2 = new Permission("provider", pluginContext2);
		Permission pluginConsumerPermission1 = new Permission("consumer", pluginContext1);
		Permission pluginConsumerPermission2 = new Permission("consumer", pluginContext2);*/
		
		// add global dummy context and permissions 
		/*permissionFacade.savePermission(tproAdminPermission);
		permissionFacade.savePermission(tproUserPermission);
		List<Permission> perms = permissionFacade.getAllPermissions();
		tproUserPermission = perms.stream().filter(perm -> perm.getName().equals("user"))
                .collect(Collectors.toList()).get(0); 
		tproAdminPermission = perms.stream().filter(perm -> perm.getName().equals("admin"))
                .collect(Collectors.toList()).get(0);*/
		
		HashSet<Permission> userPerms = new HashSet<Permission>();
		HashSet<Permission> adminPerms = new HashSet<Permission>();
		adminPerms.add(permissionFacade.getPermissionByPermissionAndContextName("admin", "tpro"));
		userPerms.add(permissionFacade.getPermissionByPermissionAndContextName("user", "tpro"));
		user.setPermissions(userPerms);
		admin.setPermissions(adminPerms);
		
		// add dummy users with permissions
		userFacade.saveUser(admin);
		userFacade.saveUser(user);
		/*userFacade.saveUser(pluginInstaller);
		userFacade.saveUser(serviceProvider1);
		userFacade.saveUser(serviceProvider2);
		userFacade.saveUser(serviceConsumer1);
		userFacade.saveUser(serviceConsumer2);*/
	}

}
