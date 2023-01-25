/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Danny
 */
@Entity
@Table(name = "mediatype")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mediatype.findAll", query = "SELECT m FROM Mediatype m"),
    @NamedQuery(name = "Mediatype.findByMediaTypeId", query = "SELECT m FROM Mediatype m WHERE m.mediaTypeId = :mediaTypeId"),
    @NamedQuery(name = "Mediatype.findByName", query = "SELECT m FROM Mediatype m WHERE m.name = :name")})
public class Mediatype implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "MediaTypeId")
    private Integer mediaTypeId;
    @Size(max = 120)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mediaTypeId")
    private List<Track> trackList;

    public Mediatype() {
    }

    public Mediatype(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public Integer getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mediaTypeId != null ? mediaTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mediatype)) {
            return false;
        }
        Mediatype other = (Mediatype) object;
        if ((this.mediaTypeId == null && other.mediaTypeId != null) || (this.mediaTypeId != null && !this.mediaTypeId.equals(other.mediaTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.unicauca.apliweb.persistence.entities.Mediatype[ mediaTypeId=" + mediaTypeId + " ]";
    }
    
}
