package de.htw_berlin.tpro.user_management.persistence.dao;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.user_management.model.Context;

public interface ContextFacade extends Serializable {

	public void updateAllContexts(List<Context> contexts);
	
	public void updateContext(Context context);
	
	public void saveContext(Context context);
	
	public List<Context> getAllContexts();
	
	public Context getContextByName(String name);

	public List<String> getAllNames();
	
	public void deleteContext(Context context);

	public void deleteAllContexts();
		
}
