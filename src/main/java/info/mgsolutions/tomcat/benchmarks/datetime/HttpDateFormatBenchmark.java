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
package info.mgsolutions.tomcat.benchmarks.datetime;

import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.All)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OperationsPerInvocation(HttpDateFormatBenchmark.CALL_COUNT)
public class HttpDateFormatBenchmark {

    private static final int THREAD_COUNT = 8;
    public static final int CALL_COUNT = 1_000_000;

    /**
     * Tomcat pool of {@link java.text.SimpleDateFormat}s
     */
    @Benchmark
    @Group("tomcat")
    @GroupThreads(THREAD_COUNT)
    public void tomcatFastHttpDateFormat(Blackhole blackhole) {
        for (int i = 0; i < CALL_COUNT; i++) {
            blackhole.consume(FastHttpDateFormat.getCurrentDate());
        }
    }

    /**
     * Java 8 {@link java.time.format.DateTimeFormatter}
     */
    @Benchmark
    @Group("datetime")
    @GroupThreads(THREAD_COUNT)
    public void javaDateTimeFormat(Blackhole blackhole) {
        for (int i = 0; i < CALL_COUNT; i++) {
            blackhole.consume(DateTimeBasedFormat.getCurrentDate());
        }
    }
}
