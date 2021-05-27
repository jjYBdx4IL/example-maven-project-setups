/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package helloworld.rest;

import java.time.Instant;
import java.time.OffsetDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import helloworld.rest.jpa.Article;


//CHECKSTYLE:OFF
@Path("")
public class Home {

    @PersistenceContext
    EntityManager em;
    
    @Transactional
    @Path("")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() {
        Article ar = new Article();
        ar.setCreated(Instant.now());
        ar.setModified(OffsetDateTime.now());
        ar.setText("Hello, World!");
        em.persist(ar);
        
        Article ar2 = em.find(Article.class, ar.getId());
        
        return Response.ok(ar2.getText()).build();
    }
}
