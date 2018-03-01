package de.htw_berlin.tpro.framework;

import java.io.Serializable;
import java.util.Set;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.model.Permission;

public class DefaultPlugin extends Plugin implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public DefaultPlugin(String author, String name, String version, String title, String description, String thumbnailResource,
			Context context, Set<Permission> permissions) {
		super(author, name, version, title, description, thumbnailResource, context, permissions);
	}

}
