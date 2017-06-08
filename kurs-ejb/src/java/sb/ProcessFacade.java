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
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByUser", Process.class).setParameter("userid", id);
        return q.getResultList();
    }

    public List<Process> procByProj(int id) {
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByProject", Process.class).setParameter("projid", id);
        return q.getResultList();
    }

    public List<Process> findEndedProcess(int id) {
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByTask", Process.class).setParameter("id", id).setParameter("ended", true);
        return q.getResultList();
    }

    public Process findActiveProcess(int id) {
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByTask", Process.class).setParameter("id", id).setParameter("ended", false);

        Process res = null;
        try {
            res = q.getSingleResult();
        } catch (Exception e) {

        }
        return res;
    }

    public void closeActiveProcess(int id) {
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByTask", Process.class).setParameter("id", id).setParameter("ended", false);
        List<Process> activeList = q.getResultList();
        for (Process item : activeList) {
            item.setEnded(true);
            em.merge(item);
        }
    }

    public ProcessFacade() {
        super(Process.class);
    }

    public Process findUserActiveProcess(int id) {
        TypedQuery<Process> q;
        q = em.createNamedQuery("Process.findByUserActive", Process.class).setParameter("id", id).setParameter("ended", false);
        Process res = null;
        try {
            res = q.getSingleResult();
        } catch (Exception e) {

        }
        return res;
    }
}
