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
    
    public TaskFacade() {
        super(Task.class);
    }
    
    public List getByUser(int id){
        TypedQuery <Task> q = em.createNamedQuery("Task.findByUser", Task.class).setParameter("userid", id);
        List mylist = q.getResultList();
        return q.getResultList();
    }
    
     public List getByActive(){
        TypedQuery <Task> q = em.createNamedQuery("Task.findActive", Task.class);
        List mylist = q.getResultList();
        return q.getResultList();
    }
    
}
