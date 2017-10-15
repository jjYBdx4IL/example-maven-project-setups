package com.github.jjYBdx4IL.example.solr;

import com.github.jjYBdx4IL.example.solr.beans.FieldConfig;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    public static final int ZK_PORT = 2181;
    public static final String COLLECTION = "gettingstarted";
    public static final Object COLLECTION_CFGNAME = "myconfig";
    public static final boolean IS_CLUSTERED = portIsOpen(ZK_PORT);

    public static SolrClient createClient() {
        LOG.info("is clustered: " + IS_CLUSTERED);

        if (IS_CLUSTERED) {
            String zkHostString = "localhost:" + ZK_PORT + ",localhost:" + (ZK_PORT + 1) + ",localhost:"
                + (ZK_PORT + 2);
            LOG.info("connect: " + zkHostString);
            CloudSolrClient cloudSolr = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
            cloudSolr.setDefaultCollection(COLLECTION);
            cloudSolr.setParser(new XMLResponseParser());
            return cloudSolr;
        } else {
            String urlString = "http://127.0.0.1:8983/solr/" + COLLECTION;
            LOG.info("connect: " + urlString);
            HttpSolrClient httpSolr = new HttpSolrClient.Builder(urlString).build();
            httpSolr.setParser(new XMLResponseParser());
            return httpSolr;
        }
    }

    public static boolean portIsOpen(final int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", port), 1000);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static void collectMethods(Class<?> beanClass, List<Method> methods) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Field.class)) {
                continue;
            }
            methods.add(method);
        }

        if (beanClass.getSuperclass() != null) {
            collectMethods(beanClass.getSuperclass(), methods);
        }
    }

    private static boolean cmp(boolean one, Object two) {
        return Boolean.compare(one, two == null ? false : (boolean) two) == 0;
    }

    public static void verifyOrDisableAutoCreateFields() throws IOException, UnirestException {
        String overlayConfigUrl = "http://127.0.0.1:8983/solr/" + COLLECTION + "/config/overlay?omitHeader=true";
        String reply = Unirest.get(overlayConfigUrl).asString().getBody();
        boolean autoCreateFields = true;
        LOG.info(reply);
        try {
            autoCreateFields = Boolean
                .parseBoolean(JsonPath.parse(reply).read("$['overlay']['userProps']['update.autoCreateFields']"));
        } catch (PathNotFoundException ex) {
        }
        if (autoCreateFields) {
            LOG.info("setting update.autoCreateFields to false");
            Unirest.post(overlayConfigUrl).body("{\"set-user-property\": {\"update.autoCreateFields\":\"false\"}}")
                .asJson();
        }
    }

    public static void verifyOrCreateSchema(Class<?> beanClass) throws SolrServerException, IOException {
        try (SolrClient client = Config.createClient()) {

            List<Method> methods = new ArrayList<>();
            collectMethods(beanClass, methods);

            SchemaRequest.Fields fieldsRequest = new SchemaRequest.Fields();
            SchemaResponse.FieldsResponse response = fieldsRequest.process(client);
            LOG.debug(response.toString());
            List<Map<String, Object>> currentFieldDefList = response.getFields();
            final Map<String, Object> currentFieldDefs = new HashMap<>();
            currentFieldDefList.forEach(map -> currentFieldDefs.put((String) map.get("name"), map));

            Set<String> fieldNameDupes = new HashSet<>();
            
            for (Method method : methods) {
                LOG.trace("" + method);
                String fieldName = method.getAnnotation(Field.class).value();
                if (fieldName == null || fieldName.isEmpty()) {
                    throw new IllegalArgumentException("invalid @Field value for " + method);
                }
                
                if (!fieldNameDupes.add(fieldName)) {
                    throw new IllegalArgumentException("duplicate definition for field " + fieldName
                        + " found in " + beanClass);
                }
                
                FieldConfig beanConfig = method.getAnnotation(FieldConfig.class);
                if (beanConfig == null) {
                    throw new IllegalArgumentException("no @FieldConfig alongside @Field for " + method);
                }

                if (currentFieldDefs.containsKey(fieldName)) {
                    LOG.debug("verifying existing field " + fieldName);
                    LOG.debug("bean config: " + beanConfig);
                    Map<String, Object> currentFieldDef = (Map<String, Object>) currentFieldDefs.get(fieldName);
                    LOG.debug("active config on server: " + currentFieldDef);
                    if (!beanConfig.type().name().equals(currentFieldDef.get("type"))) {
                        throw new IllegalArgumentException("field attribute 'type' for " + fieldName + " currently is "
                            + currentFieldDef.get("type") + ", but " + beanClass + " defines it as "
                            + beanConfig.type().name());
                    }
                    if (!cmp(beanConfig.unique(), currentFieldDef.get("unique"))) {
                        throw new IllegalArgumentException(
                            "field attribute 'unique' for " + fieldName + " currently is "
                                + currentFieldDef.get("unique") + ", but " + beanClass + " defines it as "
                                + beanConfig.unique());
                    }
                    if (!cmp(beanConfig.indexed(), currentFieldDef.get("indexed"))) {
                        throw new IllegalArgumentException(
                            "field attribute 'indexed' for " + fieldName + " currently is "
                                + currentFieldDef.get("indexed") + ", but " + beanClass + " defines it as "
                                + beanConfig.indexed());
                    }
                    if (!cmp(beanConfig.stored(), currentFieldDef.get("stored"))) {
                        throw new IllegalArgumentException(
                            "field attribute 'stored' for " + fieldName + " currently is "
                                + currentFieldDef.get("stored") + ", but " + beanClass + " defines it as "
                                + beanConfig.stored());
                    }
                    if (!cmp(beanConfig.required(), currentFieldDef.get("required"))) {
                        throw new IllegalArgumentException(
                            "field attribute 'required' for " + fieldName + " currently is "
                                + currentFieldDef.get("required") + ", but " + beanClass + " defines it as "
                                + beanConfig.required());
                    }
                    if (!cmp(beanConfig.multiValued(), currentFieldDef.get("multiValued"))) {
                        throw new IllegalArgumentException(
                            "field attribute 'multiValued' for " + fieldName + " currently is "
                                + currentFieldDef.get("multiValued") + ", but " + beanClass + " defines it as "
                                + beanConfig.multiValued());
                    }
                } else {
                    LOG.warn("creating missing field " + fieldName + ": " + beanConfig);
                    Map<String, Object> fieldAttributes = new LinkedHashMap<>();
                    fieldAttributes.put("name", fieldName);
                    fieldAttributes.put("type", beanConfig.type().name());
                    if (beanConfig.unique()) {
                        fieldAttributes.put("unique", beanConfig.unique());
                    }
                    if (beanConfig.indexed()) {
                        fieldAttributes.put("indexed", beanConfig.indexed());
                    }
                    if (beanConfig.stored()) {
                        fieldAttributes.put("stored", beanConfig.stored());
                    }
                    if (beanConfig.required()) {
                        fieldAttributes.put("required", beanConfig.required());
                    }
                    if (beanConfig.multiValued()) {
                        fieldAttributes.put("multiValued", beanConfig.multiValued());
                    }
                    SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(fieldAttributes);
                    UpdateResponse updateResponse = addFieldRequest.process(client);
                    LOG.debug("response: " + updateResponse);
                    Object errObj = updateResponse.getResponse().findRecursive("errors");
                    List errors = errObj != null ? (List) errObj : new ArrayList<>();
                    if (updateResponse.getStatus() != 0 || !errors.isEmpty()) {
                        throw new IOException("failed to update schema: " + updateResponse);
                    }
                    client.commit();
                }
            }
        }

    }
}
