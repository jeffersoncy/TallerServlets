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
import edu.unicauca.apliweb.persistence.entities.Invoice;
import edu.unicauca.apliweb.persistence.entities.Invoiceline;
import edu.unicauca.apliweb.persistence.entities.Track;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Danny
 */
public class InvoicelineJpaController implements Serializable {

    public InvoicelineJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Invoiceline invoiceline) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice invoiceId = invoiceline.getInvoiceId();
            if (invoiceId != null) {
                invoiceId = em.getReference(invoiceId.getClass(), invoiceId.getInvoiceId());
                invoiceline.setInvoiceId(invoiceId);
            }
            Track trackId = invoiceline.getTrackId();
            if (trackId != null) {
                trackId = em.getReference(trackId.getClass(), trackId.getTrackId());
                invoiceline.setTrackId(trackId);
            }
            em.persist(invoiceline);
            if (invoiceId != null) {
                invoiceId.getInvoicelineList().add(invoiceline);
                invoiceId = em.merge(invoiceId);
            }
            if (trackId != null) {
                trackId.getInvoicelineList().add(invoiceline);
                trackId = em.merge(trackId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findInvoiceline(invoiceline.getInvoiceLineId()) != null) {
                throw new PreexistingEntityException("Invoiceline " + invoiceline + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Invoiceline invoiceline) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoiceline persistentInvoiceline = em.find(Invoiceline.class, invoiceline.getInvoiceLineId());
            Invoice invoiceIdOld = persistentInvoiceline.getInvoiceId();
            Invoice invoiceIdNew = invoiceline.getInvoiceId();
            Track trackIdOld = persistentInvoiceline.getTrackId();
            Track trackIdNew = invoiceline.getTrackId();
            if (invoiceIdNew != null) {
                invoiceIdNew = em.getReference(invoiceIdNew.getClass(), invoiceIdNew.getInvoiceId());
                invoiceline.setInvoiceId(invoiceIdNew);
            }
            if (trackIdNew != null) {
                trackIdNew = em.getReference(trackIdNew.getClass(), trackIdNew.getTrackId());
                invoiceline.setTrackId(trackIdNew);
            }
            invoiceline = em.merge(invoiceline);
            if (invoiceIdOld != null && !invoiceIdOld.equals(invoiceIdNew)) {
                invoiceIdOld.getInvoicelineList().remove(invoiceline);
                invoiceIdOld = em.merge(invoiceIdOld);
            }
            if (invoiceIdNew != null && !invoiceIdNew.equals(invoiceIdOld)) {
                invoiceIdNew.getInvoicelineList().add(invoiceline);
                invoiceIdNew = em.merge(invoiceIdNew);
            }
            if (trackIdOld != null && !trackIdOld.equals(trackIdNew)) {
                trackIdOld.getInvoicelineList().remove(invoiceline);
                trackIdOld = em.merge(trackIdOld);
            }
            if (trackIdNew != null && !trackIdNew.equals(trackIdOld)) {
                trackIdNew.getInvoicelineList().add(invoiceline);
                trackIdNew = em.merge(trackIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoiceline.getInvoiceLineId();
                if (findInvoiceline(id) == null) {
                    throw new NonexistentEntityException("The invoiceline with id " + id + " no longer exists.");
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
            Invoiceline invoiceline;
            try {
                invoiceline = em.getReference(Invoiceline.class, id);
                invoiceline.getInvoiceLineId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoiceline with id " + id + " no longer exists.", enfe);
            }
            Invoice invoiceId = invoiceline.getInvoiceId();
            if (invoiceId != null) {
                invoiceId.getInvoicelineList().remove(invoiceline);
                invoiceId = em.merge(invoiceId);
            }
            Track trackId = invoiceline.getTrackId();
            if (trackId != null) {
                trackId.getInvoicelineList().remove(invoiceline);
                trackId = em.merge(trackId);
            }
            em.remove(invoiceline);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Invoiceline> findInvoicelineEntities() {
        return findInvoicelineEntities(true, -1, -1);
    }

    public List<Invoiceline> findInvoicelineEntities(int maxResults, int firstResult) {
        return findInvoicelineEntities(false, maxResults, firstResult);
    }

    private List<Invoiceline> findInvoicelineEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invoiceline.class));
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

    public Invoiceline findInvoiceline(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Invoiceline.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoicelineCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invoiceline> rt = cq.from(Invoiceline.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
