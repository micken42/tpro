package de.htw_berlin.tpro.plugin.hello_user.persistence;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import de.htw_berlin.tpro.plugin.hello_user.model.Visitor;

@Dependent
@DefaultVisitorFacade
public class VisitorFacadeImpl implements VisitorFacade {

	private static final long serialVersionUID = 1L;

	@Inject @DefaultVisitorDAO
	GenericDAO<Visitor> visitorDAO;

	@SuppressWarnings("unchecked")
	@Override
	public List<Visitor> getAllVisitors() {
		visitorDAO.beginTransaction();
		List<Visitor> visitors;
		try {
			visitors = (List<Visitor>) visitorDAO.getEntityManager().createNamedQuery("Visitor.findAll")
					.getResultList();
		} catch (NoResultException e) {
			visitors = null;
		}
		visitorDAO.commitAndCloseTransaction();
		return visitors;
	}

	@Override
	public void saveVisitor(Visitor visitor) {
		try {
			visitorDAO.beginTransaction();

			visitorDAO.save(visitor);

			visitorDAO.commit();
		} catch (Exception e) {
			EntityTransaction txn = visitorDAO.getEntityManager().getTransaction();
			if (txn != null && txn.isActive())
				visitorDAO.rollback();
			throw e;
			// handle the underlying error
		} finally {
			visitorDAO.closeTransaction();
		}
	}
	
	@Override
	public Visitor getVisitorByFullname(String fullname) {
		visitorDAO.beginTransaction();
		Visitor user;
		try {
			user = (Visitor) visitorDAO.getEntityManager().createNamedQuery("Visitor.findByFullname")
					.setParameter("fullname", fullname).getSingleResult();
		} catch (NoResultException e) {
			user = null;
		}
		visitorDAO.commitAndCloseTransaction();
		return user;
	}

}