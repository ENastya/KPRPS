/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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
 * @author Настя
 */
@Entity
@Table(name = "process")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Process.findAll", query = "SELECT p FROM Process p"),
    @NamedQuery(name = "Process.findById", query = "SELECT p FROM Process p WHERE p.id = :id"),
    @NamedQuery(name = "Process.findByBTime", query = "SELECT p FROM Process p WHERE p.bTime = :bTime"),
    @NamedQuery(name = "Process.findByETime", query = "SELECT p FROM Process p WHERE p.eTime = :eTime"),
    @NamedQuery(name = "Process.findByFullTime", query = "SELECT p FROM Process p WHERE p.fullTime = :fullTime")})
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "BTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ETime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FullTime")
    @Temporal(TemporalType.TIME)
    private Date fullTime;
    @JoinColumn(name = "TaskId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Task taskId;

    public Process() {
    }

    public Process(Integer id) {
        this.id = id;
    }

    public Process(Integer id, Date bTime, Date eTime, Date fullTime) {
        this.id = id;
        this.bTime = bTime;
        this.eTime = eTime;
        this.fullTime = fullTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getBTime() {
        return bTime;
    }

    public void setBTime(Date bTime) {
        this.bTime = bTime;
    }

    public Date getETime() {
        return eTime;
    }

    public void setETime(Date eTime) {
        this.eTime = eTime;
    }

    public Date getFullTime() {
        return fullTime;
    }

    public void setFullTime(Date fullTime) {
        this.fullTime = fullTime;
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
        if (!(object instanceof Process)) {
            return false;
        }
        Process other = (Process) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dao.Process[ id=" + id + " ]";
    }
    
}
