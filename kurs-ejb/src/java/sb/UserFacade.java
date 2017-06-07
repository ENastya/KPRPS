/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import models.User;

/**
 *
 * @author Настя
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean existMail(String mail) {
        TypedQuery<User> q = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", mail);
        if (q.getResultList() == null) {
            return false;
        }
        return true;
    }

    public User findByMail(String mail) {
        TypedQuery<User> q = em.createNamedQuery("User.findByEmail", User.class).setParameter("email", mail);
        return q.getSingleResult();
    }

    public UserFacade() {
        super(User.class);
    }

}
