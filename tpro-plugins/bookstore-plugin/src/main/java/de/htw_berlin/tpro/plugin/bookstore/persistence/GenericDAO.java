package de.htw_berlin.tpro.plugin.bookstore.persistence;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
 
public abstract class GenericDAO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // TODO: Warum muss die Persistence Unit hier einen anderen Namen haben aber vom Inhalt komplett identisch sein, damit die Entities gemappt werden???
    private EntityManagerFactory emf = 
    		Persistence.createEntityManagerFactory("tpro-bookstore-database");
    private EntityManager em;
 
    private Class<T> entityClass;
    
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
 
    public EntityManager getEntityManager() {
    	return em;
    }
 
    public EntityManagerFactory getEntityManagerFactory() {
    	return emf;
    }
    
    public void setEntityManagerFactory(EntityManagerFactory emf) {
    	this.emf = emf;
    }
    
    public void beginTransaction() {
        em = emf.createEntityManager();
 
        em.getTransaction().begin();
    }
 
    public void commit() {
        em.getTransaction().commit();
    }
 
    public void rollback() {
        em.getTransaction().rollback();
    }
 
    public void closeTransaction() {
        em.close();
    }
 
    public void commitAndCloseTransaction() {
        commit();
        closeTransaction();
    }
 
    public void flush() {
        em.flush();
    }
 
    public void save(T entity) {
        em.persist(entity);
    }
 
    protected void delete(Object id, Class<T> classe) {
        T entityToBeRemoved = em.getReference(classe, id);
 
        em.remove(entityToBeRemoved);
    }
 
    public T update(T entity) {
        return em.merge(entity);
    }
 
    public T find(int entityID) {
        return em.find(entityClass, entityID);
    }
 
    public T findReferenceOnly(int entityID) {
        return em.getReference(entityClass, entityID);
    }
      
    @SuppressWarnings({ "unchecked"})
    public List<T> findByQuery(String q) {	
        return em.createNamedQuery(q).getResultList();
    }
 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<T> findAll() {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }
 
}
