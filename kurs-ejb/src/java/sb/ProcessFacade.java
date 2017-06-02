/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import models.Process;

/**
 *
 * @author Настя
 */
@Stateless
public class ProcessFacade extends AbstractFacade<Process> {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
     public List<Process> userTaskByDate(int id) {
        TypedQuery <Process> q;
        q = em.createNamedQuery("Process.findByUser", Process.class).setParameter("userid", id);
       return q.getResultList();
    }
     
      public List<Process> procByProj(int id) {
        TypedQuery <Process> q;
        q = em.createNamedQuery("Process.findByProject", Process.class).setParameter("projid", id);
       return q.getResultList();
    }
      
    public ProcessFacade() {
        super(Process.class);
    }
    
}
