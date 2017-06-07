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
import models.Text;
import sb.AbstractFacade;

/**
 *
 * @author Настя
 */
@Stateless
public class TextFacade extends AbstractFacade<Text> {

    @PersistenceContext(unitName = "kurs-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Text> textByUser(int id) {
        TypedQuery<Text> q;
        q = em.createNamedQuery("Text.findByUser", Text.class).setParameter("userid", id);
        return q.getResultList();
    }
    
    public List<Text> textByProj(int id) {
        TypedQuery<Text> q;
        q = em.createNamedQuery("Text.findByUser", Text.class).setParameter("projid", id);
        return q.getResultList();
    }
    
    public TextFacade() {
        super(Text.class);
    }
    
}
