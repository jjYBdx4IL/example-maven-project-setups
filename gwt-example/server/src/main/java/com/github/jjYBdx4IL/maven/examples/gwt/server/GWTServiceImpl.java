package com.github.jjYBdx4IL.maven.examples.gwt.server;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.GWTService;
import com.github.jjYBdx4IL.aop.tx.Tx;
import com.github.jjYBdx4IL.aop.tx.TxEM;
import com.github.jjYBdx4IL.aop.tx.TxRO;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa.ExampleItem;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.dto.ExampleItemDTO;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa.QueryFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Tx
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {

    private static final Logger LOG = LoggerFactory.getLogger(GWTServiceImpl.class);
    
    private static final String SESSION_ATTRNAME_USER = "username";
    private static final String SESSION_ATTRNAME_AUTHENTICATED = "authenticated";
   
    @TxEM
    private EntityManager em;
    
    @Override
    public String greetme(String username) {
        Boolean authenticated = (Boolean) getSession().getAttribute(SESSION_ATTRNAME_AUTHENTICATED);
        LOG.info(""+em);
        if (authenticated == null || !authenticated.booleanValue()) {
            return "Hello unauthenticated user!";
        }
        return "Hello " + username.toUpperCase() + "!";
    }

    @Override
    public void forceOOM() {
        Set<byte[]> set = new HashSet<byte[]>();
        while (true) {
            set.add(new byte[1048576]);
        }
    }

    @Override
    public void login(String user, String password) {
        if (!password.equals("secret")) {
            throw new RuntimeException("auth failed");
        }
        
        getSession().setAttribute(SESSION_ATTRNAME_USER, user);
        getSession().setAttribute(SESSION_ATTRNAME_AUTHENTICATED, true);
    }
    
    private HttpSession getSession() {
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        return session;
    }

    @Override
    public void logout() {
        getSession().removeAttribute(SESSION_ATTRNAME_USER);
        getSession().removeAttribute(SESSION_ATTRNAME_AUTHENTICATED);
        
    }

    @TxRO
    @Override
    public List<ExampleItemDTO> getExampleItems() {
        TypedQuery<ExampleItem> itemQuery = QueryFactory.getAll(em);
        List<ExampleItemDTO> resultList = new ArrayList<>();
        for (ExampleItem item : itemQuery.getResultList()) {
            resultList.add(item.toDTO());
        }
        return resultList;
    }

    @Tx
    @Override
    public void addExampleItem(ExampleItemDTO item) {
        em.persist(ExampleItem.fromDTO(item));
    }

}
