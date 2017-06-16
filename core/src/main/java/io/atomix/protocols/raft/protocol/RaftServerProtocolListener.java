/*
 * Copyright 2017-present Open Networking Laboratory
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
 * limitations under the License.
 */
package io.atomix.protocols.raft.protocol;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Raft server protocol listener.
 */
public interface RaftServerProtocolListener {

  /**
   * Registers an open session request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerOpenSessionHandler(Function<OpenSessionRequest, CompletableFuture<OpenSessionResponse>> handler);

  /**
   * Unregisters the open session request handler.
   */
  void unregisterOpenSessionHandler();

  /**
   * Registers a close session request callback.
   *
   * @param handler the close session request handler to register
   */
  void registerCloseSessionHandler(Function<CloseSessionRequest, CompletableFuture<CloseSessionResponse>> handler);

  /**
   * Unregisters the close session request handler.
   */
  void unregisterCloseSessionHandler();

  /**
   * Registers a keep alive request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerKeepAliveHandler(Function<KeepAliveRequest, CompletableFuture<KeepAliveResponse>> handler);

  /**
   * Unregisters the keep alive request handler.
   */
  void unregisterKeepAliveHandler();

  /**
   * Registers a query request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerQueryHandler(Function<QueryRequest, CompletableFuture<QueryResponse>> handler);

  /**
   * Unregisters the query request handler.
   */
  void unregisterQueryHandler();

  /**
   * Registers a command request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerCommandHandler(Function<CommandRequest, CompletableFuture<CommandResponse>> handler);

  /**
   * Unregisters the command request handler.
   */
  void unregisterCommandHandler();

  /**
   * Registers a metadata request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerMetadataHandler(Function<MetadataRequest, CompletableFuture<MetadataResponse>> handler);

  /**
   * Unregisters the metadata request handler.
   */
  void unregisterMetadataHandler();

  /**
   * Registers a join request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerJoinHandler(Function<JoinRequest, CompletableFuture<JoinResponse>> handler);

  /**
   * Unregisters the join request handler.
   */
  void unregisterJoinHandler();

  /**
   * Registers a leave request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerLeaveHandler(Function<LeaveRequest, CompletableFuture<LeaveResponse>> handler);

  /**
   * Unregisters the leave request handler.
   */
  void unregisterLeaveHandler();

  /**
   * Registers a configure request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerConfigureHandler(Function<ConfigureRequest, CompletableFuture<ConfigureResponse>> handler);

  /**
   * Unregisters the configure request handler.
   */
  void unregisterConfigureHandler();

  /**
   * Registers a reconfigure request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerReconfigureHandler(Function<ReconfigureRequest, CompletableFuture<ReconfigureResponse>> handler);

  /**
   * Unregisters the reconfigure request handler.
   */
  void unregisterReconfigureHandler();

  /**
   * Registers a install request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerInstallHandler(Function<InstallRequest, CompletableFuture<InstallResponse>> handler);

  /**
   * Unregisters the install request handler.
   */
  void unregisterInstallHandler();

  /**
   * Registers a poll request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerPollHandler(Function<PollRequest, CompletableFuture<PollResponse>> handler);

  /**
   * Unregisters the poll request handler.
   */
  void unregisterPollHandler();

  /**
   * Registers a vote request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerVoteHandler(Function<VoteRequest, CompletableFuture<VoteResponse>> handler);

  /**
   * Unregisters the vote request handler.
   */
  void unregisterVoteHandler();

  /**
   * Registers an append request callback.
   *
   * @param handler the open session request handler to register
   */
  void registerAppendHandler(Function<AppendRequest, CompletableFuture<AppendResponse>> handler);

  /**
   * Unregisters the append request handler.
   */
  void unregisterAppendHandler();

  /**
   * Registers a reset request listener.
   *
   * @param sessionId the session ID for which to register the listener
   * @param listener  the reset request listener to add
   * @param executor  the executor with which to execute the listener
   */
  void registerResetListener(long sessionId, Consumer<ResetRequest> listener, Executor executor);

  /**
   * Unregisters the given reset request listener.
   *
   * @param sessionId the session ID for which to unregister the listener
   */
  void unregisterResetListener(long sessionId);

}
