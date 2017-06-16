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
 * limitations under the License.
 */
package io.atomix.util.concurrent;

import io.atomix.util.serializer.Serializer;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkState;

/**
 * Thread context.
 * <p>
 * The thread context is used by Catalyst to determine the correct thread on which to execute asynchronous callbacks.
 * All threads created within Catalyst must be instances of {@link AtomixThread}. Once
 * a thread has been created, the context is stored in the thread object via
 * {@link AtomixThread#setContext(ThreadContext)}. This means there is a one-to-one relationship
 * between a context and a thread. That is, a context is representative of a thread and provides an interface for firing
 * events on that thread.
 * <p>
 * In addition to serving as an {@link Executor}, the context also provides thread-local storage
 * for {@link Serializer} serializer instances. All serialization that takes place within a
 * {@link AtomixThread} should use the context
 * <p>
 * Components of the framework that provide custom threads should use {@link AtomixThreadFactory}
 * to allocate new threads and provide a custom {@link ThreadContext} implementation.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public interface ThreadContext extends AutoCloseable, Executor {

  /**
   * Returns the current thread context.
   *
   * @return The current thread context or {@code null} if no context exists.
   */
  static ThreadContext currentContext() {
    Thread thread = Thread.currentThread();
    return thread instanceof AtomixThread ? ((AtomixThread) thread).getContext() : null;
  }

  /**
   * @throws IllegalStateException if the current thread is not a catalyst thread
   */
  static ThreadContext currentContextOrThrow() {
    ThreadContext context = currentContext();
    checkState(context != null, "not on a Catalyst thread");
    return context;
  }

  /**
   * Returns a boolean indicating whether the current thread is in this context.
   *
   * @return Indicates whether the current thread is in this context.
   */
  default boolean isCurrentContext() {
    return currentContext() == this;
  }

  /**
   * Checks that the current thread is the correct context thread.
   */
  default void checkThread() {
    checkState(currentContext() == this, "not on a Catalyst thread");
  }

  /**
   * Returns the context logger.
   *
   * @return The context logger.
   */
  Logger logger();

  /**
   * Returns a boolean indicating whether the context state is blocked.
   *
   * @return Indicates whether the context state is blocked.
   */
  boolean isBlocked();

  /**
   * Sets the context state to blocked.
   */
  void block();

  /**
   * Sets the context state to unblocked.
   */
  void unblock();

  /**
   * Schedules a runnable on the context.
   *
   * @param callback The callback to schedule.
   * @param delay    The delay at which to schedule the runnable.
   */
  Scheduled schedule(Duration delay, Runnable callback);

  /**
   * Schedules a runnable at a fixed rate on the context.
   *
   * @param callback The callback to schedule.
   */
  Scheduled schedule(Duration initialDelay, Duration interval, Runnable callback);

  /**
   * Closes the context.
   */
  @Override
  void close();

}
