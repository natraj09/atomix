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
package io.atomix.storage.journal;

import java.util.concurrent.locks.Lock;

/**
 * Log writer.
 *
 * @author <a href="http://github.com/kuujo>Jordan Halterman</a>
 */
public class SegmentedJournalWriter<E> implements JournalWriter<E> {
  private final SegmentedJournal<E> journal;
  private final Lock lock;
  private volatile JournalSegment<E> currentSegment;
  private volatile JournalSegmentWriter<E> currentWriter;

  public SegmentedJournalWriter(SegmentedJournal<E> journal, Lock lock) {
    this.journal = journal;
    this.lock = lock;
    this.currentSegment = journal.lastSegment();
    this.currentWriter = currentSegment.writer();
  }

  @Override
  public Lock lock() {
    return lock;
  }

  @Override
  public long lastIndex() {
    return currentWriter.lastIndex();
  }

  @Override
  public Indexed<E> lastEntry() {
    return currentWriter.lastEntry();
  }

  @Override
  public long nextIndex() {
    return currentWriter.nextIndex();
  }

  @Override
  public <T extends E> Indexed<T> append(T entry) {
    if (currentWriter.isFull()) {
      currentSegment = journal.nextSegment();
      currentWriter = currentSegment.writer();
    }
    return currentWriter.append(entry);
  }

  @Override
  public void append(Indexed<E> entry) {
    if (currentWriter.isFull()) {
      currentSegment = journal.nextSegment();
      currentWriter = currentSegment.writer();
    }
    currentWriter.append(entry);
  }

  @Override
  public void truncate(long index) {
    // Delete all segments with first indexes greater than the given index.
    while (index < currentWriter.firstIndex() - 1) {
      currentWriter.close();
      journal.removeSegment(currentSegment);
      currentSegment = journal.lastSegment();
      currentWriter = currentSegment.writer();
    }

    // Truncate the current index.
    currentWriter.truncate(index);
  }

  @Override
  public void flush() {
    currentWriter.flush();
  }

  @Override
  public void close() {
    currentWriter.close();
  }
}