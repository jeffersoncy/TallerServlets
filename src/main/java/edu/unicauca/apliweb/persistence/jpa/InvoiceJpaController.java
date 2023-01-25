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
import edu.unicauca.apliweb.persistence.entities.Customer;
import edu.unicauca.apliweb.persistence.entities.Invoice;
import edu.unicauca.apliweb.persistence.entities.Invoiceline;
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
public class InvoiceJpaController implements Serializable {

    public InvoiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Invoice invoice) throws PreexistingEntityException, Exception {
        if (invoice.getInvoicelineList() == null) {
            invoice.setInvoicelineList(new ArrayList<Invoiceline>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Customer customerId = invoice.getCustomerId();
            if (customerId != null) {
                customerId = em.getReference(customerId.getClass(), customerId.getCustomerId());
                invoice.setCustomerId(customerId);
            }
            List<Invoiceline> attachedInvoicelineList = new ArrayList<Invoiceline>();
            for (Invoiceline invoicelineListInvoicelineToAttach : invoice.getInvoicelineList()) {
                invoicelineListInvoicelineToAttach = em.getReference(invoicelineListInvoicelineToAttach.getClass(), invoicelineListInvoicelineToAttach.getInvoiceLineId());
                attachedInvoicelineList.add(invoicelineListInvoicelineToAttach);
            }
            invoice.setInvoicelineList(attachedInvoicelineList);
            em.persist(invoice);
            if (customerId != null) {
                customerId.getInvoiceList().add(invoice);
                customerId = em.merge(customerId);
            }
            for (Invoiceline invoicelineListInvoiceline : invoice.getInvoicelineList()) {
                Invoice oldInvoiceIdOfInvoicelineListInvoiceline = invoicelineListInvoiceline.getInvoiceId();
                invoicelineListInvoiceline.setInvoiceId(invoice);
                invoicelineListInvoiceline = em.merge(invoicelineListInvoiceline);
                if (oldInvoiceIdOfInvoicelineListInvoiceline != null) {
                    oldInvoiceIdOfInvoicelineListInvoiceline.getInvoicelineList().remove(invoicelineListInvoiceline);
                    oldInvoiceIdOfInvoicelineListInvoiceline = em.merge(oldInvoiceIdOfInvoicelineListInvoiceline);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findInvoice(invoice.getInvoiceId()) != null) {
                throw new PreexistingEntityException("Invoice " + invoice + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Invoice invoice) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice persistentInvoice = em.find(Invoice.class, invoice.getInvoiceId());
            Customer customerIdOld = persistentInvoice.getCustomerId();
            Customer customerIdNew = invoice.getCustomerId();
            List<Invoiceline> invoicelineListOld = persistentInvoice.getInvoicelineList();
            List<Invoiceline> invoicelineListNew = invoice.getInvoicelineList();
            List<String> illegalOrphanMessages = null;
            for (Invoiceline invoicelineListOldInvoiceline : invoicelineListOld) {
                if (!invoicelineListNew.contains(invoicelineListOldInvoiceline)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Invoiceline " + invoicelineListOldInvoiceline + " since its invoiceId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (customerIdNew != null) {
                customerIdNew = em.getReference(customerIdNew.getClass(), customerIdNew.getCustomerId());
                invoice.setCustomerId(customerIdNew);
            }
            List<Invoiceline> attachedInvoicelineListNew = new ArrayList<Invoiceline>();
            for (Invoiceline invoicelineListNewInvoicelineToAttach : invoicelineListNew) {
                invoicelineListNewInvoicelineToAttach = em.getReference(invoicelineListNewInvoicelineToAttach.getClass(), invoicelineListNewInvoicelineToAttach.getInvoiceLineId());
                attachedInvoicelineListNew.add(invoicelineListNewInvoicelineToAttach);
            }
            invoicelineListNew = attachedInvoicelineListNew;
            invoice.setInvoicelineList(invoicelineListNew);
            invoice = em.merge(invoice);
            if (customerIdOld != null && !customerIdOld.equals(customerIdNew)) {
                customerIdOld.getInvoiceList().remove(invoice);
                customerIdOld = em.merge(customerIdOld);
            }
            if (customerIdNew != null && !customerIdNew.equals(customerIdOld)) {
                customerIdNew.getInvoiceList().add(invoice);
                customerIdNew = em.merge(customerIdNew);
            }
            for (Invoiceline invoicelineListNewInvoiceline : invoicelineListNew) {
                if (!invoicelineListOld.contains(invoicelineListNewInvoiceline)) {
                    Invoice oldInvoiceIdOfInvoicelineListNewInvoiceline = invoicelineListNewInvoiceline.getInvoiceId();
                    invoicelineListNewInvoiceline.setInvoiceId(invoice);
                    invoicelineListNewInvoiceline = em.merge(invoicelineListNewInvoiceline);
                    if (oldInvoiceIdOfInvoicelineListNewInvoiceline != null && !oldInvoiceIdOfInvoicelineListNewInvoiceline.equals(invoice)) {
                        oldInvoiceIdOfInvoicelineListNewInvoiceline.getInvoicelineList().remove(invoicelineListNewInvoiceline);
                        oldInvoiceIdOfInvoicelineListNewInvoiceline = em.merge(oldInvoiceIdOfInvoicelineListNewInvoiceline);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoice.getInvoiceId();
                if (findInvoice(id) == null) {
                    throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.");
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
            Invoice invoice;
            try {
                invoice = em.getReference(Invoice.class, id);
                invoice.getInvoiceId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Invoiceline> invoicelineListOrphanCheck = invoice.getInvoicelineList();
            for (Invoiceline invoicelineListOrphanCheckInvoiceline : invoicelineListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Invoice (" + invoice + ") cannot be destroyed since the Invoiceline " + invoicelineListOrphanCheckInvoiceline + " in its invoicelineList field has a non-nullable invoiceId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer customerId = invoice.getCustomerId();
            if (customerId != null) {
                customerId.getInvoiceList().remove(invoice);
                customerId = em.merge(customerId);
            }
            em.remove(invoice);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Invoice> findInvoiceEntities() {
        return findInvoiceEntities(true, -1, -1);
    }

    public List<Invoice> findInvoiceEntities(int maxResults, int firstResult) {
        return findInvoiceEntities(false, maxResults, firstResult);
    }

    private List<Invoice> findInvoiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invoice.class));
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

    public Invoice findInvoice(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Invoice.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invoice> rt = cq.from(Invoice.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
