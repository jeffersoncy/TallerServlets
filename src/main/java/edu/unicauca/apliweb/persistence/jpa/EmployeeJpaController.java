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
import edu.unicauca.apliweb.persistence.entities.Employee;
import java.util.ArrayList;
import java.util.List;
import edu.unicauca.apliweb.persistence.entities.Customer;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Danny
 */
public class EmployeeJpaController implements Serializable {

    public EmployeeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Employee employee) throws PreexistingEntityException, Exception {
        if (employee.getEmployeeList() == null) {
            employee.setEmployeeList(new ArrayList<Employee>());
        }
        if (employee.getCustomerList() == null) {
            employee.setCustomerList(new ArrayList<Customer>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Employee reportsTo = employee.getReportsTo();
            if (reportsTo != null) {
                reportsTo = em.getReference(reportsTo.getClass(), reportsTo.getEmployeeId());
                employee.setReportsTo(reportsTo);
            }
            List<Employee> attachedEmployeeList = new ArrayList<Employee>();
            for (Employee employeeListEmployeeToAttach : employee.getEmployeeList()) {
                employeeListEmployeeToAttach = em.getReference(employeeListEmployeeToAttach.getClass(), employeeListEmployeeToAttach.getEmployeeId());
                attachedEmployeeList.add(employeeListEmployeeToAttach);
            }
            employee.setEmployeeList(attachedEmployeeList);
            List<Customer> attachedCustomerList = new ArrayList<Customer>();
            for (Customer customerListCustomerToAttach : employee.getCustomerList()) {
                customerListCustomerToAttach = em.getReference(customerListCustomerToAttach.getClass(), customerListCustomerToAttach.getCustomerId());
                attachedCustomerList.add(customerListCustomerToAttach);
            }
            employee.setCustomerList(attachedCustomerList);
            em.persist(employee);
            if (reportsTo != null) {
                reportsTo.getEmployeeList().add(employee);
                reportsTo = em.merge(reportsTo);
            }
            for (Employee employeeListEmployee : employee.getEmployeeList()) {
                Employee oldReportsToOfEmployeeListEmployee = employeeListEmployee.getReportsTo();
                employeeListEmployee.setReportsTo(employee);
                employeeListEmployee = em.merge(employeeListEmployee);
                if (oldReportsToOfEmployeeListEmployee != null) {
                    oldReportsToOfEmployeeListEmployee.getEmployeeList().remove(employeeListEmployee);
                    oldReportsToOfEmployeeListEmployee = em.merge(oldReportsToOfEmployeeListEmployee);
                }
            }
            for (Customer customerListCustomer : employee.getCustomerList()) {
                Employee oldSupportRepIdOfCustomerListCustomer = customerListCustomer.getSupportRepId();
                customerListCustomer.setSupportRepId(employee);
                customerListCustomer = em.merge(customerListCustomer);
                if (oldSupportRepIdOfCustomerListCustomer != null) {
                    oldSupportRepIdOfCustomerListCustomer.getCustomerList().remove(customerListCustomer);
                    oldSupportRepIdOfCustomerListCustomer = em.merge(oldSupportRepIdOfCustomerListCustomer);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEmployee(employee.getEmployeeId()) != null) {
                throw new PreexistingEntityException("Employee " + employee + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Employee employee) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Employee persistentEmployee = em.find(Employee.class, employee.getEmployeeId());
            Employee reportsToOld = persistentEmployee.getReportsTo();
            Employee reportsToNew = employee.getReportsTo();
            List<Employee> employeeListOld = persistentEmployee.getEmployeeList();
            List<Employee> employeeListNew = employee.getEmployeeList();
            List<Customer> customerListOld = persistentEmployee.getCustomerList();
            List<Customer> customerListNew = employee.getCustomerList();
            if (reportsToNew != null) {
                reportsToNew = em.getReference(reportsToNew.getClass(), reportsToNew.getEmployeeId());
                employee.setReportsTo(reportsToNew);
            }
            List<Employee> attachedEmployeeListNew = new ArrayList<Employee>();
            for (Employee employeeListNewEmployeeToAttach : employeeListNew) {
                employeeListNewEmployeeToAttach = em.getReference(employeeListNewEmployeeToAttach.getClass(), employeeListNewEmployeeToAttach.getEmployeeId());
                attachedEmployeeListNew.add(employeeListNewEmployeeToAttach);
            }
            employeeListNew = attachedEmployeeListNew;
            employee.setEmployeeList(employeeListNew);
            List<Customer> attachedCustomerListNew = new ArrayList<Customer>();
            for (Customer customerListNewCustomerToAttach : customerListNew) {
                customerListNewCustomerToAttach = em.getReference(customerListNewCustomerToAttach.getClass(), customerListNewCustomerToAttach.getCustomerId());
                attachedCustomerListNew.add(customerListNewCustomerToAttach);
            }
            customerListNew = attachedCustomerListNew;
            employee.setCustomerList(customerListNew);
            employee = em.merge(employee);
            if (reportsToOld != null && !reportsToOld.equals(reportsToNew)) {
                reportsToOld.getEmployeeList().remove(employee);
                reportsToOld = em.merge(reportsToOld);
            }
            if (reportsToNew != null && !reportsToNew.equals(reportsToOld)) {
                reportsToNew.getEmployeeList().add(employee);
                reportsToNew = em.merge(reportsToNew);
            }
            for (Employee employeeListOldEmployee : employeeListOld) {
                if (!employeeListNew.contains(employeeListOldEmployee)) {
                    employeeListOldEmployee.setReportsTo(null);
                    employeeListOldEmployee = em.merge(employeeListOldEmployee);
                }
            }
            for (Employee employeeListNewEmployee : employeeListNew) {
                if (!employeeListOld.contains(employeeListNewEmployee)) {
                    Employee oldReportsToOfEmployeeListNewEmployee = employeeListNewEmployee.getReportsTo();
                    employeeListNewEmployee.setReportsTo(employee);
                    employeeListNewEmployee = em.merge(employeeListNewEmployee);
                    if (oldReportsToOfEmployeeListNewEmployee != null && !oldReportsToOfEmployeeListNewEmployee.equals(employee)) {
                        oldReportsToOfEmployeeListNewEmployee.getEmployeeList().remove(employeeListNewEmployee);
                        oldReportsToOfEmployeeListNewEmployee = em.merge(oldReportsToOfEmployeeListNewEmployee);
                    }
                }
            }
            for (Customer customerListOldCustomer : customerListOld) {
                if (!customerListNew.contains(customerListOldCustomer)) {
                    customerListOldCustomer.setSupportRepId(null);
                    customerListOldCustomer = em.merge(customerListOldCustomer);
                }
            }
            for (Customer customerListNewCustomer : customerListNew) {
                if (!customerListOld.contains(customerListNewCustomer)) {
                    Employee oldSupportRepIdOfCustomerListNewCustomer = customerListNewCustomer.getSupportRepId();
                    customerListNewCustomer.setSupportRepId(employee);
                    customerListNewCustomer = em.merge(customerListNewCustomer);
                    if (oldSupportRepIdOfCustomerListNewCustomer != null && !oldSupportRepIdOfCustomerListNewCustomer.equals(employee)) {
                        oldSupportRepIdOfCustomerListNewCustomer.getCustomerList().remove(customerListNewCustomer);
                        oldSupportRepIdOfCustomerListNewCustomer = em.merge(oldSupportRepIdOfCustomerListNewCustomer);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = employee.getEmployeeId();
                if (findEmployee(id) == null) {
                    throw new NonexistentEntityException("The employee with id " + id + " no longer exists.");
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
            Employee employee;
            try {
                employee = em.getReference(Employee.class, id);
                employee.getEmployeeId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The employee with id " + id + " no longer exists.", enfe);
            }
            Employee reportsTo = employee.getReportsTo();
            if (reportsTo != null) {
                reportsTo.getEmployeeList().remove(employee);
                reportsTo = em.merge(reportsTo);
            }
            List<Employee> employeeList = employee.getEmployeeList();
            for (Employee employeeListEmployee : employeeList) {
                employeeListEmployee.setReportsTo(null);
                employeeListEmployee = em.merge(employeeListEmployee);
            }
            List<Customer> customerList = employee.getCustomerList();
            for (Customer customerListCustomer : customerList) {
                customerListCustomer.setSupportRepId(null);
                customerListCustomer = em.merge(customerListCustomer);
            }
            em.remove(employee);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Employee> findEmployeeEntities() {
        return findEmployeeEntities(true, -1, -1);
    }

    public List<Employee> findEmployeeEntities(int maxResults, int firstResult) {
        return findEmployeeEntities(false, maxResults, firstResult);
    }

    private List<Employee> findEmployeeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Employee.class));
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

    public Employee findEmployee(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Employee.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmployeeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Employee> rt = cq.from(Employee.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
