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
	public User signUp(User user) {
		userFacade.saveUser(user);
		user.addPermission(permissionFacade.getPermissionByPermissionAndContextName("user", "tpro"));
		userFacade.updateUser(user);
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
		admin.setEmail("admin@tpro.de");
		user.setEmail("user@tpro.de");
		admin.setPrename("Maximilian");
		admin.setSurname("Mustemann");
		user.setPrename("Maxime");
		user.setSurname("Musterfrau");
		/*User pluginInstaller = new User("Plugininstallateur", "1234");
		User serviceProvider1 = new User("Dienstanbieter1", "1234");
		User serviceProvider2 = new User("Dienstanbieter2", "1234");
		User serviceConsumer1 = new User("Dienstkonsument1", "1234");
		User serviceConsumer2 = new User("Dienstkonsument2", "1234");*/
		
		// create and save global dummy context and permissions
		Context tproContext = new Context("tpro");
		tproContext.addPermission(new Permission("admin"));
		tproContext.addPermission(new Permission("user"));
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
				
		// add dummy users with permissions
		userFacade.saveUser(admin);
		userFacade.saveUser(user);
		
		admin.addPermission(permissionFacade.getPermissionByPermissionAndContextName("admin", "tpro"));
		user.addPermission(permissionFacade.getPermissionByPermissionAndContextName("user", "tpro"));

		userFacade.updateUser(admin);
		userFacade.updateUser(user);
		
		/*userFacade.saveUser(pluginInstaller);
		userFacade.saveUser(serviceProvider1);
		userFacade.saveUser(serviceProvider2);
		userFacade.saveUser(serviceConsumer1);
		userFacade.saveUser(serviceConsumer2);*/
	}

}
