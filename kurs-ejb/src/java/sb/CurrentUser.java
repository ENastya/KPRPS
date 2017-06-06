/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb;

import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import models.User;

/**
 *
 * @author Настя
 */
@Stateful
@LocalBean
public class CurrentUser {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;
    private static User curUser;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    /**
     * @return the curUser
     */
    public User getCurUser() {
        //curUser  = em.find(User.class, 1);
        return curUser;
    }

    /**
     * @param curUser the curUser to set
     */
    public void setCurUser(User curUser) {
        CurrentUser.curUser = curUser;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
