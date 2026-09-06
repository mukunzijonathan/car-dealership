package rw.ac.auca.dealership.dao;

import rw.ac.auca.dealership.model.Car;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class CarDAO {
    public void save(Car car) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (car.getId() == null) em.persist(car);
            else em.merge(car);
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
            Car car = em.find(Car.class, id);
            if (car != null) em.remove(car);
            tx.commit();
        } finally {
            em.close();
        }
    }

    public List<Car> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Car c", Car.class).getResultList();
        } finally {
            em.close();
        }
    }
}