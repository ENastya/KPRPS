/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import models.Task;

/**
 *
 * @author Настя
 */
@Stateless
public class TaskFacade extends AbstractFacade<Task> {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public List<Task> findTaskRange(int id, int[] range) {
        /*javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);*/
        TypedQuery <Task> q;
        
       /* if (active) {
            q = em.createNamedQuery("Task.findActive", Task.class).setParameter("userid", id);
        } 
        else {*/
            q = em.createNamedQuery("Task.findByUser", Task.class).setParameter("userid", id);
        //}
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }
    
    public List<Task> findActive(int id){
        TypedQuery <Task> q;
        q = em.createNamedQuery("Task.findActive", Task.class).setParameter("userid", id);
        return q.getResultList();
    }
    
    public TaskFacade() {
        super(Task.class);
    }
    
   /* public List getByUserActive () {
        
        return getByUser(id);
    }
    
    
    public List getByUser(int id){
        TypedQuery <Task> q = em.createNamedQuery("Task.findByUser", Task.class).setParameter("userid", id);
        List mylist = q.getResultList();
        return q.getResultList();
    }
    
     public List getByActive(int id){
        TypedQuery <Task> q = em.createNamedQuery("Task.findActive", Task.class).setParameter("userid", id);
        List mylist = q.getResultList();
        return q.getResultList();
    }*/
    
}
