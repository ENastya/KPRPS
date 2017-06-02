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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author User
 */
@Entity
@Table(name = "estimate")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estimate.findAll", query = "SELECT e FROM Estimate e"),
    @NamedQuery(name = "Estimate.findById", query = "SELECT e FROM Estimate e WHERE e.id = :id"),
    @NamedQuery(name = "Estimate.findByCDate", query = "SELECT e FROM Estimate e WHERE e.cDate = :cDate")})
public class Estimate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cDate;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private User userId;
    @JoinColumn(name = "ProjectId", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Project projectId;

    public Estimate() {
    }

    public Estimate(Integer id) {
        this.id = id;
    }

    public Estimate(Integer id, Date cDate) {
        this.id = id;
        this.cDate = cDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCDate() {
        return cDate;
    }

    public void setCDate(Date cDate) {
        this.cDate = cDate;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
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
        if (!(object instanceof Estimate)) {
            return false;
        }
        Estimate other = (Estimate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "models.Estimate[ id=" + id + " ]";
    }
    
}
