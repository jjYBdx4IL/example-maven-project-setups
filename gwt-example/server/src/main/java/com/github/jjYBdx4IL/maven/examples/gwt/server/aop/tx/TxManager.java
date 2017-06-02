package com.github.jjYBdx4IL.maven.examples.gwt.server.aop.tx;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class TxManager {

    private static final Logger LOG = LoggerFactory.getLogger(TxManager.class);

    protected static TxManager txManagerSingleton = null;
    protected static final String DB_URL
            = "jdbc:h2:" + new File(System.getProperty("basedir", "."), "data/h2db").getAbsolutePath();
    protected static final String PU_NAME = "default";

    public static synchronized TxManager getSingleton() {
        if (txManagerSingleton == null) {
            txManagerSingleton = new TxManager();
        }
        return txManagerSingleton;
    }

    protected final Map<String, String> props = new HashMap<>();
    protected EntityManagerFactory entityManagerFactory = null;
    // keep track of who is using the EMF:
    protected Set<Object> emfUsedByRefs = new HashSet<>();

    public TxManager() {
        LOG.info("new " + TxManager.class.getName() + ", db url: " + DB_URL);
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.show_sql", "true");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        props.put("javax.persistence.jdbc.url", DB_URL);
    }

    public synchronized EntityManagerFactory getEntityManagerFactory(Object usedBy) {
        LOG.info("getEntityManagerFactory() for " + usedBy);
        if (entityManagerFactory == null) {
            LOG.info("creating EntityManagerFactory singleton for persistence unit " + PU_NAME);
            entityManagerFactory = Persistence.createEntityManagerFactory(PU_NAME, props);
        }
        emfUsedByRefs.add(usedBy);
        LOG.info("EntityManagerFactory for persistence unit " + PU_NAME + " now in use by " + emfUsedByRefs.size() + " objects");
        return entityManagerFactory;
    }

    public synchronized void releaseEntityManagerFactory(Object usedBy) {
        LOG.info("releaseEntityManagerFactory() for " + usedBy);
        emfUsedByRefs.remove(usedBy);
        LOG.info("EntityManagerFactory for persistence unit " + PU_NAME + " now in use by " + emfUsedByRefs.size() + " objects");
        if (emfUsedByRefs.isEmpty() && entityManagerFactory != null) {
            LOG.info("closing EntityManagerFactory singleton for persistence unit " + PU_NAME + " because it is not in use any more");
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }
}
