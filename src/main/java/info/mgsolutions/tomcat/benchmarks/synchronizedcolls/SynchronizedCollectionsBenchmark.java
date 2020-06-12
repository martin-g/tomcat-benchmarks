/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package info.mgsolutions.tomcat.benchmarks.synchronizedcolls;

import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OperationsPerInvocation(SynchronizedCollectionsBenchmark.CALL_COUNT)
public class SynchronizedCollectionsBenchmark {

    private static final int THREAD_COUNT = 8;
    public static final int CALL_COUNT = 1_000_000;

    @State(Scope.Group)
    public static class Collections {
        private final SynchronizedQueue<Object> synchronizedQueue = new SynchronizedQueue<>();
        private final ConcurrentLinkedQueue<Object> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();


        private final SynchronizedStack<Object> synchronizedStack = new SynchronizedStack<>();
        private final ConcurrentLinkedDeque<Object> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
    }

    /**
     * Benchmark for {@link SynchronizedQueue}
     */
    @Benchmark
    @Group("synchronizedQueue")
    @GroupThreads(THREAD_COUNT)
    public void tomcatSynchronizedQueue(Collections state) {
        SynchronizedQueue<Object> underTest = state.synchronizedQueue;
        for (int i = 0; i < CALL_COUNT; i++) {
            Object obj = underTest.poll();
            if (obj == null) {
                obj = new Object();
            }
            underTest.offer(obj);
        }
    }

    /**
     * Benchmark for {@link SynchronizedStack}
     */
    @Benchmark
    @Group("synchronizedStack")
    @GroupThreads(THREAD_COUNT)
    public void tomcatSynchronizedStack(Collections state) {
        SynchronizedStack<Object> underTest = state.synchronizedStack;
        for (int i = 0; i < CALL_COUNT; i++) {
            Object obj = underTest.pop();
            if (obj == null) {
                obj = new Object();
            }
            underTest.push(obj);
        }
    }

    /**
     * Benchmark for {@link ConcurrentLinkedQueue}
     */
    @Benchmark
    @Group("concurrentLinkedQueue")
    @GroupThreads(THREAD_COUNT)
    public void concurrentLinkedQueue(Collections state) {
        ConcurrentLinkedQueue<Object> underTest = state.concurrentLinkedQueue;
        for (int i = 0; i < CALL_COUNT; i++) {
            Object obj = underTest.poll();
            if (obj == null) {
                obj = new Object();
            }
            underTest.offer(obj);
        }
    }

    /**
     * Benchmark for {@link ConcurrentLinkedDeque}
     */
    @Benchmark
    @Group("concurrentLinkedDeque")
    @GroupThreads(THREAD_COUNT)
    public void concurrentLinkedDeque(Collections state) {
        ConcurrentLinkedDeque<Object> underTest = state.concurrentLinkedDeque;
        for (int i = 0; i < CALL_COUNT; i++) {
            Object obj = underTest.pollLast();
            if (obj == null) {
                obj = new Object();
            }
            underTest.offerLast(obj);
        }
    }
}
