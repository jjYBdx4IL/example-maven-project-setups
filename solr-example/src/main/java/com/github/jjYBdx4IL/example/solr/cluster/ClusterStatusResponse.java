package com.github.jjYBdx4IL.example.solr.cluster;

import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//CHECKSTYLE:OFF
@SuppressWarnings("unchecked")
public class ClusterStatusResponse {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterStatusResponse.class);

    private List<String> liveNodes = null;
    private final Map<String, CollectionStatus> collections = new HashMap<>();

    /**
     * If you use this for monitoring purposes, make sure you catch Exception
     * in order to catch all the exceptions due to incomplete status code handling or
     * due to new status codes returned. You should treat those cases as monitoring
     * problems and have a closer look at the stack trace to see what's actually
     * going on.
     * 
     * Work in progress.
     */
    public ClusterStatusResponse(NamedList<Object> response) {
        if (response == null) {
            throw new IllegalStateException("response must not be null");
        }
        if (LOG.isDebugEnabled()) {
            dumpNamedList(response);
        }

        // check response status code
        int statusCode = (int) response.findRecursive("responseHeader", "status");
        if (statusCode != 0) {
            throw new IllegalStateException("unexpected status code " + statusCode);
        }

        // parse cluster information
        NamedList<Object> cluster = (NamedList<Object>) response.get("cluster");
        if (cluster == null) {
            throw new IllegalStateException("no cluster section found in response");
        }
        if (LOG.isDebugEnabled()) {
            dumpNamedList(cluster);
        }

        NamedList<Object> collections = (NamedList<Object>) response.findRecursive("cluster", "collections");
        if (collections == null) {
            throw new IllegalStateException("no collections section found in response");
        }
        if (LOG.isDebugEnabled()) {
            dumpNamedList(collections);
        }
        for (int i = 0; i < collections.size(); i++) {
            String collectionName = collections.getName(i);
            NamedList<Object> collectionValue = (NamedList<Object>) collections.getVal(i);
            if (collectionName == null || collectionValue == null || collectionName.isEmpty()) {
                throw new IllegalStateException();
            }
            if (this.collections.containsKey(collectionName)) {
                throw new IllegalStateException();
            }
            this.collections.put(collectionName, new CollectionStatus(collectionValue));
        }

        List<String> liveNodes = (List<String>) response.findRecursive("cluster", "live_nodes");
        if (liveNodes == null) {
            throw new IllegalStateException("no live_nodes section found in response");
        }
        this.liveNodes = new ArrayList<>(liveNodes);
    }

    public List<String> getLiveNodes() {
        return Collections.unmodifiableList(liveNodes);
    }

    public Map<String, CollectionStatus> getCollections() {
        return Collections.unmodifiableMap(collections);
    }

    public static class CollectionStatus {
        private final int pullReplicas;
        private final int replicationFactor;
        private final int maxShardsPerNode;
        private final boolean autoAddReplicas;
        private final int nrtReplicas;
        private final int tlogReplicas;
        private final int znodeVersion;
        private final String configName;
        private final Map<String, ShardStatus> shards = new HashMap<>();

        public CollectionStatus(NamedList<Object> nl) {
            if (nl == null) {
                throw new IllegalStateException("argument must not be null");
            }

            nl = nl.clone();

            this.pullReplicas = Integer.parseInt((String) nl.get("pullReplicas"));
            nl.remove("pullReplicas");
            this.replicationFactor = Integer.parseInt((String) nl.get("replicationFactor"));
            nl.remove("replicationFactor");
            nl.remove("router"); // ignored
            this.maxShardsPerNode = Integer.parseInt((String) nl.get("maxShardsPerNode"));
            nl.remove("maxShardsPerNode");
            this.autoAddReplicas = Boolean.parseBoolean((String) nl.get("autoAddReplicas"));
            nl.remove("autoAddReplicas");
            this.nrtReplicas = Integer.parseInt((String) nl.get("nrtReplicas"));
            nl.remove("nrtReplicas");
            this.tlogReplicas = Integer.parseInt((String) nl.get("tlogReplicas"));
            nl.remove("tlogReplicas");
            this.znodeVersion = (int) nl.get("znodeVersion");
            nl.remove("znodeVersion");
            this.configName = (String) nl.get("configName");
            if (this.configName == null || this.configName.isEmpty()) {
                throw new IllegalStateException();
            }
            nl.remove("configName");

            NamedList<Object> shards = (NamedList<Object>) nl.get("shards");
            if (shards == null) {
                throw new IllegalStateException("no shards section found in response");
            }
            if (LOG.isDebugEnabled()) {
                dumpNamedList(shards);
            }
            for (int i = 0; i < shards.size(); i++) {
                String shardName = shards.getName(i);
                NamedList<Object> shardValue = (NamedList<Object>) shards.getVal(i);
                if (shardName == null || shardValue == null || shardName.isEmpty()) {
                    throw new IllegalStateException();
                }
                if (CollectionStatus.this.shards.containsKey(shardName)) {
                    throw new IllegalStateException();
                }
                CollectionStatus.this.shards.put(shardName, new ShardStatus(shardName, shardValue));
            }
            nl.remove("shards");

            if (nl.size() != 0) {
                LOG.warn("CollectionStatus parse is incomplete: " + nl.getName(0));
            }
        }

        public int getPullReplicas() {
            return pullReplicas;
        }

        public int getReplicationFactor() {
            return replicationFactor;
        }

        public int getMaxShardsPerNode() {
            return maxShardsPerNode;
        }

        public boolean isAutoAddReplicas() {
            return autoAddReplicas;
        }

        public int getNrtReplicas() {
            return nrtReplicas;
        }

        public int getTlogReplicas() {
            return tlogReplicas;
        }

        public int getZnodeVersion() {
            return znodeVersion;
        }

        public String getConfigName() {
            return configName;
        }

        public Map<String, ShardStatus> getShards() {
            return Collections.unmodifiableMap(shards);
        }

        /**
         * Generally speaking, this also catches most DOWN hosts because that usually also
         * implies a degradation of data redundancy.
         * 
         * This is a very simple implementation.
         * It's doesn't care about anything else than replicationFactor. It also requires
         * replicas to be on different hosts in order to count towards the replicationFactor.
         * To determine if they are on different hosts, we simply use the replica's hostName.
         * We don't process that hostName, so different port numbers/paths etc. will lead
         * to this function believing that replicas are located on different hosts, so don't
         * do that in production.
         */
        public RedundancyStatus getRedundancyStatus() {
            RedundancyStatus status = new RedundancyStatus();
            if (shards.values().isEmpty()) {
                status.apply(RedundancyState.FAILED, "no shards");
                return status;
            }
            for (Map.Entry<String, ShardStatus> shard : shards.entrySet()) {
                status.apply(shard.getValue().getRedundancyState(replicationFactor), shard.getValue().toString());
            }
            return status;
        }

    }

    public static class ShardStatus {
        private final String name;
        private final ShardState state;
        private final String range;
        private final Map<String, ReplicaStatus> replicas = new HashMap<>();

        public ShardStatus(String name, NamedList<Object> nl) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("name must neither be null nor empty");
            }
            this.name = name;

            if (nl == null) {
                throw new IllegalStateException("argument must not be null");
            }
            if (LOG.isDebugEnabled()) {
                dumpNamedList(nl);
            }

            nl = nl.clone();

            this.state = ShardState.valueOf(((String) nl.get("state")).toUpperCase());
            nl.remove("state");
            this.range = (String) nl.get("range");
            nl.remove("range");

            NamedList<Object> replicas = (NamedList<Object>) nl.get("replicas");
            if (replicas == null) {
                throw new IllegalStateException("no replicas section found in response");
            }
            if (LOG.isDebugEnabled()) {
                dumpNamedList(replicas);
            }
            for (int i = 0; i < replicas.size(); i++) {
                String replicaName = replicas.getName(i);
                NamedList<Object> replicaValue = (NamedList<Object>) replicas.getVal(i);
                if (replicaName == null || replicaValue == null || replicaName.isEmpty()) {
                    throw new IllegalStateException();
                }
                if (ShardStatus.this.replicas.containsKey(replicaName)) {
                    throw new IllegalStateException();
                }
                ShardStatus.this.replicas.put(replicaName, new ReplicaStatus(replicaValue));
            }
            nl.remove("replicas");

            if (nl.size() != 0) {
                LOG.warn("ShardStatus parse is incomplete: " + nl.getName(0));
            }
        }

        public ShardState getState() {
            return state;
        }

        public String getRange() {
            return range;
        }

        public Map<String, ReplicaStatus> getReplicas() {
            return Collections.unmodifiableMap(replicas);
        }

        /**
         * Also catches where replicas are located on the same host
         * and ignores those towards the replicationFactor count.
         * 
         * @param replicationFactor
         * @return
         */
        public RedundancyState getRedundancyState(int replicationFactor) {
            Set<String> activeHosts = new HashSet<>(replicationFactor);
            for (ReplicaStatus s : replicas.values()) {
                switch (s.getState()) {
                    case ACTIVE:
                        activeHosts.add(s.getNodeName().toLowerCase());
                        break;
                    case RECOVERING:
                    case DOWN:
                }
            }
            
            if (activeHosts.isEmpty()) {
                return RedundancyState.FAILED;
            }
            if (activeHosts.size() < replicationFactor) {
                return RedundancyState.DEGRADED;
            }
            return RedundancyState.HEALTHY;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append("[");
            boolean first = true;
            for (Map.Entry<String, ReplicaStatus> replica : replicas.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(replica.getKey());
                sb.append(":");
                sb.append(replica.getValue().getNodeName());
                sb.append(":");
                sb.append(replica.getValue().getState().name());
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static class ReplicaStatus {
        private final ReplicaState state;
        private final ReplicaType type;
        private final String core;
        private final String baseUrl;
        private final String nodeName;
        private final boolean isLeader;

        public ReplicaStatus(NamedList<Object> nl) {
            if (nl == null) {
                throw new IllegalStateException("argument must not be null");
            }
            if (LOG.isDebugEnabled()) {
                dumpNamedList(nl);
            }

            nl = nl.clone();

            this.state = ReplicaState.valueOf(((String) nl.get("state")).toUpperCase());
            nl.remove("state");
            this.type = ReplicaType.valueOf(((String) nl.get("type")).toUpperCase());
            nl.remove("type");

            this.core = (String) nl.get("core");
            if (this.core == null || this.core.isEmpty()) {
                throw new IllegalStateException();
            }
            nl.remove("core");

            this.baseUrl = (String) nl.get("base_url");
            if (this.baseUrl == null || this.baseUrl.isEmpty()) {
                throw new IllegalStateException();
            }
            nl.remove("base_url");

            this.nodeName = (String) nl.get("node_name");
            if (this.nodeName == null || this.nodeName.isEmpty()) {
                throw new IllegalStateException();
            }
            nl.remove("node_name");

            this.isLeader = Boolean.parseBoolean((String) nl.get("leader"));
            nl.remove("leader");

            if (nl.size() != 0) {
                LOG.warn("ReplicaStatus parse is incomplete: " + nl.getName(0));
            }
        }

        public ReplicaState getState() {
            return state;
        }

        public ReplicaType getType() {
            return type;
        }

        public String getCore() {
            return core;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getNodeName() {
            return nodeName;
        }

        public boolean isLeader() {
            return isLeader;
        }

    }

    public static class RedundancyStatus {
        private StringBuilder longMessage = new StringBuilder();
        private String message = "OK";
        private RedundancyState state = RedundancyState.HEALTHY;

        public RedundancyStatus() {
        }

        public void apply(RedundancyState newState, String newMessage) {
            if (longMessage.length() > 0) {
                longMessage.append(" ");
            }
            longMessage.append(newMessage);
            switch (newState) {
                case HEALTHY:
                    if (state.equals(RedundancyState.HEALTHY)) {
                        message = newMessage;
                    }
                    break;
                case DEGRADED:
                    if (!state.equals(RedundancyState.FAILED)) {
                        message = newMessage;
                        state = newState;
                    }
                    break;
                case FAILED:
                    message = newMessage;
                    if (!state.equals(RedundancyState.FAILED)) {
                        state = newState;
                    }
            }
        }

        public String getLongMessage() {
            return longMessage.toString();
        }

        public String getMessage() {
            return message;
        }

        public RedundancyState getState() {
            return state;
        }
    }

    public static enum RedundancyState {
        HEALTHY, DEGRADED, FAILED;

        public static RedundancyState merge(RedundancyState... states) {
            RedundancyState state = HEALTHY;
            for (RedundancyState s : states) {
                switch (s) {
                    case DEGRADED:
                        if (state.equals(RedundancyState.HEALTHY)) {
                            state = DEGRADED;
                        }
                        break;
                    case FAILED:
                        state = FAILED;
                        break;
                    case HEALTHY:
                }
            }
            return state;
        }
    }

    public static enum ShardState {
        ACTIVE, DEGRADED, DOWN;
    }

    public static enum ReplicaState {
        ACTIVE, RECOVERING, DOWN;
    }

    public static enum ReplicaType {
        NRT, TLOG;
    }

    public static void dumpNamedList(NamedList<Object> nl) {
        for (Map.Entry<String, Object> e : nl.asShallowMap().entrySet()) {
            LOG.info(e.getKey() + " " + e.getValue());
        }
    }
}
