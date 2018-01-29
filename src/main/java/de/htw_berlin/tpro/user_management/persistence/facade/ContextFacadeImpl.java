package de.htw_berlin.tpro.user_management.persistence.facade;

import java.util.List;

import javax.persistence.NoResultException;

import de.htw_berlin.tpro.user_management.model.Context;
import de.htw_berlin.tpro.user_management.persistence.dao.GenericDAO;
import de.htw_berlin.tpro.user_management.persistence.dao.ContextDAO;

public class ContextFacadeImpl implements ContextFacade {

	private static final long serialVersionUID = 1L;
	
	// TODO: WHY IS THE INJECTION NOT WORKING ???
	//		 @Inject @DefaultContextDAO
	private GenericDAO<Context> contextDAO = new ContextDAO();
	
	@Override
	public void updateAllContexts(List<Context> contexts) {
		contextDAO.beginTransaction();
		contexts.forEach(contextDAO::update);
		contextDAO.flush();
		contextDAO.commitAndCloseTransaction();
	}
	
	@Override
	public void updateContext(Context context) {
		contextDAO.beginTransaction();
		contextDAO.update(context);
		contextDAO.commitAndCloseTransaction();
	}
	
	@Override
	public void saveContext(Context context) {
		contextDAO.beginTransaction();
		contextDAO.save(context);
		contextDAO.commitAndCloseTransaction();
	}
	
	@Override
	public List<Context> getAllContexts() {
		contextDAO.beginTransaction();
		List<Context> contexts = contextDAO.findAll();
		contextDAO.closeTransaction();
		return contexts;
	}

	@Override
	public Context getContextByName(String name) {
		contextDAO.beginTransaction();
		Context context;
		try {
			context = (Context) contextDAO.getEntityManager().createNamedQuery("Context.findByName")
					.setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			context = null;
		}
		contextDAO.closeTransaction();
		return context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllNames() {
		contextDAO.beginTransaction();
		List<String> names;
		try {
			names = (List<String>) contextDAO.getEntityManager()
					.createNamedQuery("Context.findAllNames").getResultList();
		} catch (NoResultException e) {
			names = null;
		}
		contextDAO.closeTransaction();
		return names;
	}
	
}