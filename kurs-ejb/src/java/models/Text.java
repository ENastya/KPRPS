/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Настя
 */
@Entity
@Table(name = "text")
@XmlRootElement
@NamedQueries({
    
    @NamedQuery(name = "Text.findByProject", query = "SELECT t FROM Text t WHERE t.taskId.projectId.id = :projid and t.taskId.projectId.lastEstimate < t.date"),
    @NamedQuery(name = "Text.findByUser", query = "SELECT t FROM Text t WHERE t.taskId.userId.id = :userid and t.taskId.userId.lastEstimate < t.date"),
    @NamedQuery(name = "Text.findAll", query = "SELECT t FROM Text t"),
    @NamedQuery(name = "Text.findById", query = "SELECT t FROM Text t WHERE t.id = :id"),
    @NamedQuery(name = "Text.findBySymb", query = "SELECT t FROM Text t WHERE t.symb = :symb"),
    @NamedQuery(name = "Text.findByDate", query = "SELECT t FROM Text t WHERE t.date = :date")})
public class Text implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Symb")
    private int symb;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "task_id", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Task taskId;

    public Text() {
    }

    public Text(Integer id) {
        this.id = id;
    }

    public Text(Integer id, int symb, Date date) {
        this.id = id;
        this.symb = symb;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSymb() {
        return symb;
    }

    public void setSymb(int symb) {
        this.symb = symb;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Task getTaskId() {
        return taskId;
    }

    public void setTaskId(Task taskId) {
        this.taskId = taskId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Text)) {
            return false;
        }
        Text other = (Text) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Text[ id=" + id + " ]";
    }
    
}
