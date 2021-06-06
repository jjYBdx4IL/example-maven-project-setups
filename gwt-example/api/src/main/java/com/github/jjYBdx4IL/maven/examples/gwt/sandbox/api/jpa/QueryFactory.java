package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author jjYBdx4IL
 */
public class QueryFactory {

    private QueryFactory() {}
    
    public static TypedQuery<ExampleItem> getByData1(EntityManager em, String data1) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ExampleItem> criteriaQuery = criteriaBuilder.createQuery(ExampleItem.class);
        final Root<ExampleItem> userRoot = criteriaQuery.from(ExampleItem.class);
        Predicate predicateEmail = criteriaBuilder.equal(
                userRoot.get(ExampleItem_.data1),
                data1);
        criteriaQuery.where(predicateEmail);
        return em.createQuery(criteriaQuery);
    }
    
    public static TypedQuery<ExampleItem> getAll(EntityManager em) {
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        final CriteriaQuery<ExampleItem> criteriaQuery = criteriaBuilder.createQuery(ExampleItem.class);
        criteriaQuery.from(ExampleItem.class);
        return em.createQuery(criteriaQuery);
    }
}
