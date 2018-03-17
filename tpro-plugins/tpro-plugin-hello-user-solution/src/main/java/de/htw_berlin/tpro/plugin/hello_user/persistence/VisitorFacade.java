package de.htw_berlin.tpro.plugin.hello_user.persistence;

import java.io.Serializable;
import java.util.List;

import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;

public interface VisitorFacade extends Serializable {
	
	public List<Visitor> getAllVisitors();
		
	public void saveVisitor(Visitor visitor);

	public Visitor getVisitorByFullname(String fullname);
}
