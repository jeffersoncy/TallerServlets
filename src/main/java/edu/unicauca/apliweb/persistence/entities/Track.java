/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "track")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Track.findAll", query = "SELECT t FROM Track t"),
    @NamedQuery(name = "Track.findByTrackId", query = "SELECT t FROM Track t WHERE t.trackId = :trackId"),
    @NamedQuery(name = "Track.findByName", query = "SELECT t FROM Track t WHERE t.name = :name"),
    @NamedQuery(name = "Track.findByComposer", query = "SELECT t FROM Track t WHERE t.composer = :composer"),
    @NamedQuery(name = "Track.findByMilliseconds", query = "SELECT t FROM Track t WHERE t.milliseconds = :milliseconds"),
    @NamedQuery(name = "Track.findByBytes", query = "SELECT t FROM Track t WHERE t.bytes = :bytes"),
    @NamedQuery(name = "Track.findByUnitPrice", query = "SELECT t FROM Track t WHERE t.unitPrice = :unitPrice")})
public class Track implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "TrackId")
    private Integer trackId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "Name")
    private String name;
    @Size(max = 220)
    @Column(name = "Composer")
    private String composer;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Milliseconds")
    private int milliseconds;
    @Column(name = "Bytes")
    private Integer bytes;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;
    @ManyToMany(mappedBy = "trackList")
    private List<Playlist> playlistList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trackId")
    private List<Invoiceline> invoicelineList;
    @JoinColumn(name = "AlbumId", referencedColumnName = "AlbumId")
    @ManyToOne
    private Album albumId;
    @JoinColumn(name = "GenreId", referencedColumnName = "GenreId")
    @ManyToOne
    private Genre genreId;
    @JoinColumn(name = "MediaTypeId", referencedColumnName = "MediaTypeId")
    @ManyToOne(optional = false)
    private Mediatype mediaTypeId;

    public Track() {
    }

    public Track(Integer trackId) {
        this.trackId = trackId;
    }

    public Track(Integer trackId, String name, int milliseconds, BigDecimal unitPrice) {
        this.trackId = trackId;
        this.name = name;
        this.milliseconds = milliseconds;
        this.unitPrice = unitPrice;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @XmlTransient
    public List<Playlist> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(List<Playlist> playlistList) {
        this.playlistList = playlistList;
    }

    @XmlTransient
    public List<Invoiceline> getInvoicelineList() {
        return invoicelineList;
    }

    public void setInvoicelineList(List<Invoiceline> invoicelineList) {
        this.invoicelineList = invoicelineList;
    }

    public Album getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Album albumId) {
        this.albumId = albumId;
    }

    public Genre getGenreId() {
        return genreId;
    }

    public void setGenreId(Genre genreId) {
        this.genreId = genreId;
    }

    public Mediatype getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Mediatype mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (trackId != null ? trackId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Track)) {
            return false;
        }
        Track other = (Track) object;
        if ((this.trackId == null && other.trackId != null) || (this.trackId != null && !this.trackId.equals(other.trackId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.unicauca.apliweb.persistence.entities.Track[ trackId=" + trackId + " ]";
    }
    
}
