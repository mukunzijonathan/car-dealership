package rw.ac.auca.dealership.dao;

import rw.ac.auca.dealership.model.Customer;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class CustomerDAO {
    public void save(Customer customer) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (customer.getId() == null) em.persist(customer);
            else em.merge(customer);
            tx.commit();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Customer customer = em.find(Customer.class, id);
            if (customer != null) em.remove(customer);
            tx.commit();
        } finally {
            em.close();
        }
    }

    public List<Customer> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Customer c", Customer.class).getResultList();
        } finally {
            em.close();
        }
    }
}