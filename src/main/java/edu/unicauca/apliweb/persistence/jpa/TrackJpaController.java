/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import edu.unicauca.apliweb.persistence.entities.Album;
import edu.unicauca.apliweb.persistence.entities.Genre;
import edu.unicauca.apliweb.persistence.entities.Mediatype;
import edu.unicauca.apliweb.persistence.entities.Playlist;
import java.util.ArrayList;
import java.util.List;
import edu.unicauca.apliweb.persistence.entities.Invoiceline;
import edu.unicauca.apliweb.persistence.entities.Track;
import edu.unicauca.apliweb.persistence.jpa.exceptions.IllegalOrphanException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Danny
 */
public class TrackJpaController implements Serializable {

    public TrackJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Track track) throws PreexistingEntityException, Exception {
        if (track.getPlaylistList() == null) {
            track.setPlaylistList(new ArrayList<Playlist>());
        }
        if (track.getInvoicelineList() == null) {
            track.setInvoicelineList(new ArrayList<Invoiceline>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album albumId = track.getAlbumId();
            if (albumId != null) {
                albumId = em.getReference(albumId.getClass(), albumId.getAlbumId());
                track.setAlbumId(albumId);
            }
            Genre genreId = track.getGenreId();
            if (genreId != null) {
                genreId = em.getReference(genreId.getClass(), genreId.getGenreId());
                track.setGenreId(genreId);
            }
            Mediatype mediaTypeId = track.getMediaTypeId();
            if (mediaTypeId != null) {
                mediaTypeId = em.getReference(mediaTypeId.getClass(), mediaTypeId.getMediaTypeId());
                track.setMediaTypeId(mediaTypeId);
            }
            List<Playlist> attachedPlaylistList = new ArrayList<Playlist>();
            for (Playlist playlistListPlaylistToAttach : track.getPlaylistList()) {
                playlistListPlaylistToAttach = em.getReference(playlistListPlaylistToAttach.getClass(), playlistListPlaylistToAttach.getPlaylistId());
                attachedPlaylistList.add(playlistListPlaylistToAttach);
            }
            track.setPlaylistList(attachedPlaylistList);
            List<Invoiceline> attachedInvoicelineList = new ArrayList<Invoiceline>();
            for (Invoiceline invoicelineListInvoicelineToAttach : track.getInvoicelineList()) {
                invoicelineListInvoicelineToAttach = em.getReference(invoicelineListInvoicelineToAttach.getClass(), invoicelineListInvoicelineToAttach.getInvoiceLineId());
                attachedInvoicelineList.add(invoicelineListInvoicelineToAttach);
            }
            track.setInvoicelineList(attachedInvoicelineList);
            em.persist(track);
            if (albumId != null) {
                albumId.getTrackList().add(track);
                albumId = em.merge(albumId);
            }
            if (genreId != null) {
                genreId.getTrackList().add(track);
                genreId = em.merge(genreId);
            }
            if (mediaTypeId != null) {
                mediaTypeId.getTrackList().add(track);
                mediaTypeId = em.merge(mediaTypeId);
            }
            for (Playlist playlistListPlaylist : track.getPlaylistList()) {
                playlistListPlaylist.getTrackList().add(track);
                playlistListPlaylist = em.merge(playlistListPlaylist);
            }
            for (Invoiceline invoicelineListInvoiceline : track.getInvoicelineList()) {
                Track oldTrackIdOfInvoicelineListInvoiceline = invoicelineListInvoiceline.getTrackId();
                invoicelineListInvoiceline.setTrackId(track);
                invoicelineListInvoiceline = em.merge(invoicelineListInvoiceline);
                if (oldTrackIdOfInvoicelineListInvoiceline != null) {
                    oldTrackIdOfInvoicelineListInvoiceline.getInvoicelineList().remove(invoicelineListInvoiceline);
                    oldTrackIdOfInvoicelineListInvoiceline = em.merge(oldTrackIdOfInvoicelineListInvoiceline);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTrack(track.getTrackId()) != null) {
                throw new PreexistingEntityException("Track " + track + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Track track) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Track persistentTrack = em.find(Track.class, track.getTrackId());
            Album albumIdOld = persistentTrack.getAlbumId();
            Album albumIdNew = track.getAlbumId();
            Genre genreIdOld = persistentTrack.getGenreId();
            Genre genreIdNew = track.getGenreId();
            Mediatype mediaTypeIdOld = persistentTrack.getMediaTypeId();
            Mediatype mediaTypeIdNew = track.getMediaTypeId();
            List<Playlist> playlistListOld = persistentTrack.getPlaylistList();
            List<Playlist> playlistListNew = track.getPlaylistList();
            List<Invoiceline> invoicelineListOld = persistentTrack.getInvoicelineList();
            List<Invoiceline> invoicelineListNew = track.getInvoicelineList();
            List<String> illegalOrphanMessages = null;
            for (Invoiceline invoicelineListOldInvoiceline : invoicelineListOld) {
                if (!invoicelineListNew.contains(invoicelineListOldInvoiceline)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Invoiceline " + invoicelineListOldInvoiceline + " since its trackId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (albumIdNew != null) {
                albumIdNew = em.getReference(albumIdNew.getClass(), albumIdNew.getAlbumId());
                track.setAlbumId(albumIdNew);
            }
            if (genreIdNew != null) {
                genreIdNew = em.getReference(genreIdNew.getClass(), genreIdNew.getGenreId());
                track.setGenreId(genreIdNew);
            }
            if (mediaTypeIdNew != null) {
                mediaTypeIdNew = em.getReference(mediaTypeIdNew.getClass(), mediaTypeIdNew.getMediaTypeId());
                track.setMediaTypeId(mediaTypeIdNew);
            }
            List<Playlist> attachedPlaylistListNew = new ArrayList<Playlist>();
            for (Playlist playlistListNewPlaylistToAttach : playlistListNew) {
                playlistListNewPlaylistToAttach = em.getReference(playlistListNewPlaylistToAttach.getClass(), playlistListNewPlaylistToAttach.getPlaylistId());
                attachedPlaylistListNew.add(playlistListNewPlaylistToAttach);
            }
            playlistListNew = attachedPlaylistListNew;
            track.setPlaylistList(playlistListNew);
            List<Invoiceline> attachedInvoicelineListNew = new ArrayList<Invoiceline>();
            for (Invoiceline invoicelineListNewInvoicelineToAttach : invoicelineListNew) {
                invoicelineListNewInvoicelineToAttach = em.getReference(invoicelineListNewInvoicelineToAttach.getClass(), invoicelineListNewInvoicelineToAttach.getInvoiceLineId());
                attachedInvoicelineListNew.add(invoicelineListNewInvoicelineToAttach);
            }
            invoicelineListNew = attachedInvoicelineListNew;
            track.setInvoicelineList(invoicelineListNew);
            track = em.merge(track);
            if (albumIdOld != null && !albumIdOld.equals(albumIdNew)) {
                albumIdOld.getTrackList().remove(track);
                albumIdOld = em.merge(albumIdOld);
            }
            if (albumIdNew != null && !albumIdNew.equals(albumIdOld)) {
                albumIdNew.getTrackList().add(track);
                albumIdNew = em.merge(albumIdNew);
            }
            if (genreIdOld != null && !genreIdOld.equals(genreIdNew)) {
                genreIdOld.getTrackList().remove(track);
                genreIdOld = em.merge(genreIdOld);
            }
            if (genreIdNew != null && !genreIdNew.equals(genreIdOld)) {
                genreIdNew.getTrackList().add(track);
                genreIdNew = em.merge(genreIdNew);
            }
            if (mediaTypeIdOld != null && !mediaTypeIdOld.equals(mediaTypeIdNew)) {
                mediaTypeIdOld.getTrackList().remove(track);
                mediaTypeIdOld = em.merge(mediaTypeIdOld);
            }
            if (mediaTypeIdNew != null && !mediaTypeIdNew.equals(mediaTypeIdOld)) {
                mediaTypeIdNew.getTrackList().add(track);
                mediaTypeIdNew = em.merge(mediaTypeIdNew);
            }
            for (Playlist playlistListOldPlaylist : playlistListOld) {
                if (!playlistListNew.contains(playlistListOldPlaylist)) {
                    playlistListOldPlaylist.getTrackList().remove(track);
                    playlistListOldPlaylist = em.merge(playlistListOldPlaylist);
                }
            }
            for (Playlist playlistListNewPlaylist : playlistListNew) {
                if (!playlistListOld.contains(playlistListNewPlaylist)) {
                    playlistListNewPlaylist.getTrackList().add(track);
                    playlistListNewPlaylist = em.merge(playlistListNewPlaylist);
                }
            }
            for (Invoiceline invoicelineListNewInvoiceline : invoicelineListNew) {
                if (!invoicelineListOld.contains(invoicelineListNewInvoiceline)) {
                    Track oldTrackIdOfInvoicelineListNewInvoiceline = invoicelineListNewInvoiceline.getTrackId();
                    invoicelineListNewInvoiceline.setTrackId(track);
                    invoicelineListNewInvoiceline = em.merge(invoicelineListNewInvoiceline);
                    if (oldTrackIdOfInvoicelineListNewInvoiceline != null && !oldTrackIdOfInvoicelineListNewInvoiceline.equals(track)) {
                        oldTrackIdOfInvoicelineListNewInvoiceline.getInvoicelineList().remove(invoicelineListNewInvoiceline);
                        oldTrackIdOfInvoicelineListNewInvoiceline = em.merge(oldTrackIdOfInvoicelineListNewInvoiceline);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = track.getTrackId();
                if (findTrack(id) == null) {
                    throw new NonexistentEntityException("The track with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Track track;
            try {
                track = em.getReference(Track.class, id);
                track.getTrackId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The track with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Invoiceline> invoicelineListOrphanCheck = track.getInvoicelineList();
            for (Invoiceline invoicelineListOrphanCheckInvoiceline : invoicelineListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Track (" + track + ") cannot be destroyed since the Invoiceline " + invoicelineListOrphanCheckInvoiceline + " in its invoicelineList field has a non-nullable trackId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Album albumId = track.getAlbumId();
            if (albumId != null) {
                albumId.getTrackList().remove(track);
                albumId = em.merge(albumId);
            }
            Genre genreId = track.getGenreId();
            if (genreId != null) {
                genreId.getTrackList().remove(track);
                genreId = em.merge(genreId);
            }
            Mediatype mediaTypeId = track.getMediaTypeId();
            if (mediaTypeId != null) {
                mediaTypeId.getTrackList().remove(track);
                mediaTypeId = em.merge(mediaTypeId);
            }
            List<Playlist> playlistList = track.getPlaylistList();
            for (Playlist playlistListPlaylist : playlistList) {
                playlistListPlaylist.getTrackList().remove(track);
                playlistListPlaylist = em.merge(playlistListPlaylist);
            }
            em.remove(track);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Track> findTrackEntities() {
        return findTrackEntities(true, -1, -1);
    }

    public List<Track> findTrackEntities(int maxResults, int firstResult) {
        return findTrackEntities(false, maxResults, firstResult);
    }

    private List<Track> findTrackEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Track.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Track findTrack(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Track.class, id);
        } finally {
            em.close();
        }
    }

    public int getTrackCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Track> rt = cq.from(Track.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
