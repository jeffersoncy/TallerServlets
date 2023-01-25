/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.jpa;

import edu.unicauca.apliweb.persistence.entities.Album;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import edu.unicauca.apliweb.persistence.entities.Artist;
import edu.unicauca.apliweb.persistence.entities.Track;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Danny
 */
public class AlbumJpaController implements Serializable {

    public AlbumJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Album album) throws PreexistingEntityException, Exception {
        if (album.getTrackList() == null) {
            album.setTrackList(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId = em.getReference(artistId.getClass(), artistId.getArtistId());
                album.setArtistId(artistId);
            }
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : album.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getTrackId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            album.setTrackList(attachedTrackList);
            em.persist(album);
            if (artistId != null) {
                artistId.getAlbumList().add(album);
                artistId = em.merge(artistId);
            }
            for (Track trackListTrack : album.getTrackList()) {
                Album oldAlbumIdOfTrackListTrack = trackListTrack.getAlbumId();
                trackListTrack.setAlbumId(album);
                trackListTrack = em.merge(trackListTrack);
                if (oldAlbumIdOfTrackListTrack != null) {
                    oldAlbumIdOfTrackListTrack.getTrackList().remove(trackListTrack);
                    oldAlbumIdOfTrackListTrack = em.merge(oldAlbumIdOfTrackListTrack);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAlbum(album.getAlbumId()) != null) {
                throw new PreexistingEntityException("Album " + album + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Album album) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album persistentAlbum = em.find(Album.class, album.getAlbumId());
            Artist artistIdOld = persistentAlbum.getArtistId();
            Artist artistIdNew = album.getArtistId();
            List<Track> trackListOld = persistentAlbum.getTrackList();
            List<Track> trackListNew = album.getTrackList();
            if (artistIdNew != null) {
                artistIdNew = em.getReference(artistIdNew.getClass(), artistIdNew.getArtistId());
                album.setArtistId(artistIdNew);
            }
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getTrackId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            album.setTrackList(trackListNew);
            album = em.merge(album);
            if (artistIdOld != null && !artistIdOld.equals(artistIdNew)) {
                artistIdOld.getAlbumList().remove(album);
                artistIdOld = em.merge(artistIdOld);
            }
            if (artistIdNew != null && !artistIdNew.equals(artistIdOld)) {
                artistIdNew.getAlbumList().add(album);
                artistIdNew = em.merge(artistIdNew);
            }
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    trackListOldTrack.setAlbumId(null);
                    trackListOldTrack = em.merge(trackListOldTrack);
                }
            }
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    Album oldAlbumIdOfTrackListNewTrack = trackListNewTrack.getAlbumId();
                    trackListNewTrack.setAlbumId(album);
                    trackListNewTrack = em.merge(trackListNewTrack);
                    if (oldAlbumIdOfTrackListNewTrack != null && !oldAlbumIdOfTrackListNewTrack.equals(album)) {
                        oldAlbumIdOfTrackListNewTrack.getTrackList().remove(trackListNewTrack);
                        oldAlbumIdOfTrackListNewTrack = em.merge(oldAlbumIdOfTrackListNewTrack);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = album.getAlbumId();
                if (findAlbum(id) == null) {
                    throw new NonexistentEntityException("The album with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getAlbumId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            Artist artistId = album.getArtistId();
            if (artistId != null) {
                artistId.getAlbumList().remove(album);
                artistId = em.merge(artistId);
            }
            List<Track> trackList = album.getTrackList();
            for (Track trackListTrack : trackList) {
                trackListTrack.setAlbumId(null);
                trackListTrack = em.merge(trackListTrack);
            }
            em.remove(album);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Album> findAlbumEntities() {
        return findAlbumEntities(true, -1, -1);
    }

    public List<Album> findAlbumEntities(int maxResults, int firstResult) {
        return findAlbumEntities(false, maxResults, firstResult);
    }

    private List<Album> findAlbumEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Album.class));
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

    public Album findAlbum(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Album.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlbumCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Album> rt = cq.from(Album.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
