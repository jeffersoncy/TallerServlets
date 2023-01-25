/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicauca.apliweb.persistence.jpa;

import edu.unicauca.apliweb.persistence.entities.Mediatype;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import edu.unicauca.apliweb.persistence.entities.Track;
import edu.unicauca.apliweb.persistence.jpa.exceptions.IllegalOrphanException;
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
public class MediatypeJpaController implements Serializable {

    public MediatypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mediatype mediatype) throws PreexistingEntityException, Exception {
        if (mediatype.getTrackList() == null) {
            mediatype.setTrackList(new ArrayList<Track>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Track> attachedTrackList = new ArrayList<Track>();
            for (Track trackListTrackToAttach : mediatype.getTrackList()) {
                trackListTrackToAttach = em.getReference(trackListTrackToAttach.getClass(), trackListTrackToAttach.getTrackId());
                attachedTrackList.add(trackListTrackToAttach);
            }
            mediatype.setTrackList(attachedTrackList);
            em.persist(mediatype);
            for (Track trackListTrack : mediatype.getTrackList()) {
                Mediatype oldMediaTypeIdOfTrackListTrack = trackListTrack.getMediaTypeId();
                trackListTrack.setMediaTypeId(mediatype);
                trackListTrack = em.merge(trackListTrack);
                if (oldMediaTypeIdOfTrackListTrack != null) {
                    oldMediaTypeIdOfTrackListTrack.getTrackList().remove(trackListTrack);
                    oldMediaTypeIdOfTrackListTrack = em.merge(oldMediaTypeIdOfTrackListTrack);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMediatype(mediatype.getMediaTypeId()) != null) {
                throw new PreexistingEntityException("Mediatype " + mediatype + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Mediatype mediatype) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Mediatype persistentMediatype = em.find(Mediatype.class, mediatype.getMediaTypeId());
            List<Track> trackListOld = persistentMediatype.getTrackList();
            List<Track> trackListNew = mediatype.getTrackList();
            List<String> illegalOrphanMessages = null;
            for (Track trackListOldTrack : trackListOld) {
                if (!trackListNew.contains(trackListOldTrack)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Track " + trackListOldTrack + " since its mediaTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Track> attachedTrackListNew = new ArrayList<Track>();
            for (Track trackListNewTrackToAttach : trackListNew) {
                trackListNewTrackToAttach = em.getReference(trackListNewTrackToAttach.getClass(), trackListNewTrackToAttach.getTrackId());
                attachedTrackListNew.add(trackListNewTrackToAttach);
            }
            trackListNew = attachedTrackListNew;
            mediatype.setTrackList(trackListNew);
            mediatype = em.merge(mediatype);
            for (Track trackListNewTrack : trackListNew) {
                if (!trackListOld.contains(trackListNewTrack)) {
                    Mediatype oldMediaTypeIdOfTrackListNewTrack = trackListNewTrack.getMediaTypeId();
                    trackListNewTrack.setMediaTypeId(mediatype);
                    trackListNewTrack = em.merge(trackListNewTrack);
                    if (oldMediaTypeIdOfTrackListNewTrack != null && !oldMediaTypeIdOfTrackListNewTrack.equals(mediatype)) {
                        oldMediaTypeIdOfTrackListNewTrack.getTrackList().remove(trackListNewTrack);
                        oldMediaTypeIdOfTrackListNewTrack = em.merge(oldMediaTypeIdOfTrackListNewTrack);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = mediatype.getMediaTypeId();
                if (findMediatype(id) == null) {
                    throw new NonexistentEntityException("The mediatype with id " + id + " no longer exists.");
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
            Mediatype mediatype;
            try {
                mediatype = em.getReference(Mediatype.class, id);
                mediatype.getMediaTypeId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mediatype with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Track> trackListOrphanCheck = mediatype.getTrackList();
            for (Track trackListOrphanCheckTrack : trackListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mediatype (" + mediatype + ") cannot be destroyed since the Track " + trackListOrphanCheckTrack + " in its trackList field has a non-nullable mediaTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(mediatype);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Mediatype> findMediatypeEntities() {
        return findMediatypeEntities(true, -1, -1);
    }

    public List<Mediatype> findMediatypeEntities(int maxResults, int firstResult) {
        return findMediatypeEntities(false, maxResults, firstResult);
    }

    private List<Mediatype> findMediatypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mediatype.class));
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

    public Mediatype findMediatype(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mediatype.class, id);
        } finally {
            em.close();
        }
    }

    public int getMediatypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mediatype> rt = cq.from(Mediatype.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
