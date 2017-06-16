/*
 * Copyright 2015-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package io.atomix.protocols.raft;

import io.atomix.cluster.NodeId;
import io.atomix.protocols.raft.impl.DefaultRaftClient;
import io.atomix.protocols.raft.protocol.RaftClientProtocol;
import io.atomix.protocols.raft.proxy.RaftProxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides an interface for submitting operations to the Copycat cluster.
 */
public interface RaftClient {

  /**
   * Returns a new Copycat client builder.
   * <p>
   * The provided set of members will be used to connect to the Copycat cluster. The members list does not have to represent
   * the complete list of servers in the cluster, but it must have at least one reachable member that can communicate with
   * the cluster's leader.
   *
   * @return The client builder.
   */
  @SuppressWarnings("unchecked")
  static Builder builder() {
    return builder(Collections.EMPTY_LIST);
  }

  /**
   * Returns a new Copycat client builder.
   * <p>
   * The provided set of members will be used to connect to the Copycat cluster. The members list does not have to represent
   * the complete list of servers in the cluster, but it must have at least one reachable member that can communicate with
   * the cluster's leader.
   *
   * @param cluster The cluster to which to connect.
   * @return The client builder.
   */
  static Builder builder(NodeId... cluster) {
    return builder(Arrays.asList(cluster));
  }

  /**
   * Returns a new Copycat client builder.
   * <p>
   * The provided set of members will be used to connect to the Copycat cluster. The members list does not have to represent
   * the complete list of servers in the cluster, but it must have at least one reachable member that can communicate with
   * the cluster's leader.
   *
   * @param cluster The cluster to which to connect.
   * @return The client builder.
   */
  static Builder builder(Collection<NodeId> cluster) {
    return new Builder(cluster);
  }

  /**
   * Returns the globally unique client identifier.
   *
   * @return the globally unique client identifier
   */
  String id();

  /**
   * Returns the Copycat metadata.
   *
   * @return The Copycat metadata.
   */
  RaftMetadataClient metadata();

  /**
   * Returns a new proxy builder.
   *
   * @return A new proxy builder.
   */
  RaftProxy.Builder proxyBuilder();

  /**
   * Connects the client to Copycat cluster via the default server address.
   * <p>
   * If the client was built with a default cluster list, the default server addresses will be used. Otherwise, the client
   * will attempt to connect to localhost:8700.
   * <p>
   * The client will connect to servers in the cluster according to the pattern specified by the configured
   * {@link CommunicationStrategy}.
   *
   * @return A completable future to be completed once the client is registered.
   */
  default CompletableFuture<RaftClient> connect() {
    return connect((Collection<NodeId>) null);
  }

  /**
   * Connects the client to Copycat cluster via the provided server addresses.
   * <p>
   * The client will connect to servers in the cluster according to the pattern specified by the configured
   * {@link CommunicationStrategy}.
   *
   * @param members A set of server addresses to which to connect.
   * @return A completable future to be completed once the client is registered.
   */
  default CompletableFuture<RaftClient> connect(NodeId... members) {
    if (members == null || members.length == 0) {
      return connect();
    } else {
      return connect(Arrays.asList(members));
    }
  }

  /**
   * Connects the client to Copycat cluster via the provided server addresses.
   * <p>
   * The client will connect to servers in the cluster according to the pattern specified by the configured
   * {@link CommunicationStrategy}.
   *
   * @param members A set of server addresses to which to connect.
   * @return A completable future to be completed once the client is registered.
   */
  CompletableFuture<RaftClient> connect(Collection<NodeId> members);

  /**
   * Closes the client.
   *
   * @return A completable future to be completed once the client has been closed.
   */
  CompletableFuture<Void> close();

  /**
   * Builds a new Copycat client.
   * <p>
   * New client builders should be constructed using the static {@link #builder()} factory method.
   * <pre>
   *   {@code
   *     CopycatClient client = CopycatClient.builder(new Address("123.456.789.0", 5000), new Address("123.456.789.1", 5000)
   *       .withTransport(new NettyTransport())
   *       .build();
   *   }
   * </pre>
   */
  final class Builder implements io.atomix.util.Builder<RaftClient> {
    private final Collection<NodeId> cluster;
    private String clientId = UUID.randomUUID().toString();
    private NodeId nodeId;
    private RaftClientProtocol protocol;
    private int threadPoolSize = Runtime.getRuntime().availableProcessors();

    private Builder(Collection<NodeId> cluster) {
      this.cluster = checkNotNull(cluster, "cluster cannot be null");
    }

    /**
     * Sets the client ID.
     * <p>
     * The client ID is a name that should be unique among all clients. The ID will be used to resolve
     * and recover sessions.
     *
     * @param clientId The client ID.
     * @return The client builder.
     * @throws NullPointerException if {@code clientId} is null
     */
    public Builder withClientId(String clientId) {
      this.clientId = checkNotNull(clientId, "clientId cannot be null");
      return this;
    }

    /**
     * Sets the local node identifier.
     *
     * @param nodeId The local node identifier.
     * @return The client builder.
     * @throws NullPointerException if {@code nodeId} is null
     */
    public Builder withNodeId(NodeId nodeId) {
      this.nodeId = checkNotNull(nodeId, "nodeId cannot be null");
      return this;
    }

    /**
     * Sets the client protocol.
     *
     * @param protocol the client protocol
     * @return the client builder
     * @throws NullPointerException if the protocol is null
     */
    public Builder withProtocol(RaftClientProtocol protocol) {
      this.protocol = checkNotNull(protocol, "protocol cannot be null");
      return this;
    }

    /**
     * Sets the client thread pool size.
     *
     * @param threadPoolSize The client thread pool size.
     * @return The client builder.
     * @throws IllegalArgumentException if the thread pool size is not positive
     */
    public Builder withThreadPoolSize(int threadPoolSize) {
      checkArgument(threadPoolSize > 0, "threadPoolSize must be positive");
      this.threadPoolSize = threadPoolSize;
      return this;
    }

    @Override
    public RaftClient build() {
      checkNotNull(nodeId, "nodeId cannot be null");
      ScheduledExecutorService executor = Executors.newScheduledThreadPool(threadPoolSize);
      return new DefaultRaftClient(clientId, nodeId, cluster, protocol, executor);
    }
  }
}
