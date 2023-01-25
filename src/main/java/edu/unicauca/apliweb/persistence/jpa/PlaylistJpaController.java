/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.jpa;

import edu.unicauca.apliweb.persistence.entities.Playlist;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class PlaylistJpaController implements Serializable {

    public PlaylistJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Playlist playlist) throws PreexistingEntityException, Exception {
        if (playlist.getTrackList() == null) {
            playlist.setTrackList(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : playlist.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getTrackId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            playlist.setTrackList(attachedTrackList);
            em.persist(playlist);
            for (Track trackListTrack : playlist.getTrackList()) {
                trackListTrack.getPlaylistList().add(playlist);
                trackListTrack = em.merge(trackListTrack);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPlaylist(playlist.getPlaylistId()) != null) {
                throw new PreexistingEntityException("Playlist " + playlist + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Playlist playlist) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Playlist persistentPlaylist = em.find(Playlist.class, playlist.getPlaylistId());
            List<Track> trackListOld = persistentPlaylist.getTrackList();
            List<Track> trackListNew = playlist.getTrackList();
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getTrackId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            playlist.setTrackList(trackListNew);
            playlist = em.merge(playlist);
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    trackListOldTrack.getPlaylistList().remove(playlist);
                    trackListOldTrack = em.merge(trackListOldTrack);
                }
            }
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    trackListNewTrack.getPlaylistList().add(playlist);
                    trackListNewTrack = em.merge(trackListNewTrack);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = playlist.getPlaylistId();
                if (findPlaylist(id) == null) {
                    throw new NonexistentEntityException("The playlist with id " + id + " no longer exists.");
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
            Playlist playlist;
            try {
                playlist = em.getReference(Playlist.class, id);
                playlist.getPlaylistId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The playlist with id " + id + " no longer exists.", enfe);
            }
            List<Track> trackList = playlist.getTrackList();
            for (Track trackListTrack : trackList) {
                trackListTrack.getPlaylistList().remove(playlist);
                trackListTrack = em.merge(trackListTrack);
            }
            em.remove(playlist);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Playlist> findPlaylistEntities() {
        return findPlaylistEntities(true, -1, -1);
    }

    public List<Playlist> findPlaylistEntities(int maxResults, int firstResult) {
        return findPlaylistEntities(false, maxResults, firstResult);
    }

    private List<Playlist> findPlaylistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Playlist.class));
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

    public Playlist findPlaylist(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Playlist.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlaylistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Playlist> rt = cq.from(Playlist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
