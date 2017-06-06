/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import models.Estimate;
import models.Project;
import models.User;

/**
 *
 * @author Настя
 */
@Stateless
public class EstimateFacade extends AbstractFacade<Estimate> {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void LoadUserEst(int userid) {
        User user = em.find(User.class, userid);
        Estimate est = new Estimate();
        est.setPreDate(user.getLastEstimate());
        long curTime = System.currentTimeMillis();
        Date curDate = new Date(curTime);
        est.setCDate(curDate);
        est.setUserId(user);
        em.persist(est);
        user.setLastEstimate(curDate);
        em.merge(user);
    }

    public void LoadProjEst(int projid) {
        Project proj = em.find(Project.class, projid);
        Estimate est = new Estimate();
        est.setPreDate(proj.getLastEstimate());
        long curTime = System.currentTimeMillis();
        Date curDate = new Date(curTime);
        est.setCDate(curDate);
        est.setProjectId(proj);
        em.persist(est);
        proj.setLastEstimate(curDate);
        em.merge(proj);
    }

    public EstimateFacade() {
        super(Estimate.class);
    }

}
