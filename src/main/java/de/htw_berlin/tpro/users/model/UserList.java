package de.htw_berlin.tpro.users.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<User> users;
	
	public UserList() {
		super();
		users = new ArrayList<User>();
		
		User pluginInstaller = new User();
		User serviceProvider1 = new User();
		User serviceProvider2 = new User();
		User serviceConsumer1 = new User();
		User serviceConsumer2 = new User();
		
		// create dummy users
		pluginInstaller.setId(1);
		pluginInstaller.setUsername("Plugininstallateur");
		pluginInstaller.setPassword("1234");
		serviceProvider1.setId(2);
		serviceProvider1.setUsername("Dienstanbieter1");
		serviceProvider1.setPassword("1234");
		serviceProvider2.setId(3);
		serviceProvider2.setUsername("Dienstanbieter2");
		serviceProvider2.setPassword("1234");
		serviceConsumer1.setId(4);
		serviceConsumer1.setUsername("Dienstkonsument1");
		serviceConsumer1.setPassword("1234");
		serviceConsumer2.setId(5);
		serviceConsumer2.setUsername("Dienstkonsument2");
		serviceConsumer2.setPassword("1234");

		// add dummy user permissions with contexts
		Context pluginContext1 = new Context();
		Context pluginContext2 = new Context();
		Permission pluginProviderPermission1 = new Permission();
		Permission pluginProviderPermission2 = new Permission();
		Permission pluginConsumerPermission1 = new Permission();
		Permission pluginConsumerPermission2 = new Permission();
		pluginContext1.setId(1);
		pluginContext1.setName("plugin1");
		pluginContext2.setId(2);
		pluginContext2.setName("plugin2");
		pluginProviderPermission1.setId(1);
		pluginProviderPermission1.setName("provider");
		pluginProviderPermission1.setContext(pluginContext1);
		pluginProviderPermission2.setId(2);
		pluginProviderPermission2.setName("provider");
		pluginProviderPermission2.setContext(pluginContext2);
		pluginConsumerPermission1.setId(1);
		pluginConsumerPermission1.setName("consumer");
		pluginConsumerPermission1.setContext(pluginContext1);
		pluginConsumerPermission2.setId(2);
		pluginConsumerPermission2.setName("consumer");
		pluginConsumerPermission2.setContext(pluginContext2);
		
		// add dummy users
		users.add(pluginInstaller);
		users.add(serviceProvider1);
		users.add(serviceProvider2);
		users.add(serviceConsumer1);
		users.add(serviceConsumer2);
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
}
