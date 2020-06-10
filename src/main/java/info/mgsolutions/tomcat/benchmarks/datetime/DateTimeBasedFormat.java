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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class to generate HTTP dates.
 */
public final class DateTimeBasedFormat {

    private static final int CACHE_SIZE =
        Integer.parseInt(System.getProperty("org.apache.tomcat.util.http.FastHttpDateFormat.CACHE_SIZE", "1000"));


    // HTTP date formats
    private static final String DATE_RFC5322 = "EEE, d MMM yyyy HH:mm:ss z";
//    private static final String DATE_OBSOLETE_RFC850 = "EEEEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String DATE_OBSOLETE_RFC850 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String DATE_OBSOLETE_ASCTIME = "EEE MMMM d HH:mm:ss yyyy";

    private static final DateTimeFormatter FORMAT_RFC5322;
    private static final DateTimeFormatter FORMAT_OBSOLETE_RFC850;
    private static final DateTimeFormatter FORMAT_OBSOLETE_ASCTIME;

    private static final DateTimeFormatter[] httpParseFormats;
    // All the formats that use a timezone use GMT
    private static final ZoneId GMT_ZONE_ID = ZoneId.of("GMT");

    static {
        FORMAT_RFC5322 = DateTimeFormatter.ofPattern(DATE_RFC5322, Locale.US).withZone(GMT_ZONE_ID);
        FORMAT_OBSOLETE_RFC850 = DateTimeFormatter.ofPattern(DATE_OBSOLETE_RFC850, Locale.US).withZone(GMT_ZONE_ID);
        FORMAT_OBSOLETE_ASCTIME = DateTimeFormatter.ofPattern(DATE_OBSOLETE_ASCTIME, Locale.US).withZone(GMT_ZONE_ID);

        httpParseFormats = new DateTimeFormatter[] {
                FORMAT_RFC5322, FORMAT_OBSOLETE_RFC850, FORMAT_OBSOLETE_ASCTIME };
    }

    /**
     * Instant on which the currentDate object was generated.
     */
    private static final AtomicReference<LocalDateTime> CURRENT_DATE_GENERATED =
            new AtomicReference<>(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));


    /**
     * Current formatted date.
     */
    private static String currentDate = null;


    /**
     * Formatter cache.
     */
    private static final Map<Long, String> FORMAT_CACHE = new ConcurrentHashMap<>(CACHE_SIZE);


    /**
     * Parser cache.
     */
    private static final Map<String, Long> PARSE_CACHE = new ConcurrentHashMap<>(CACHE_SIZE);


    // --------------------------------------------------------- Public Methods


    /**
     * Get the current date in HTTP format.
     * @return the HTTP date
     */
    public static final String getCurrentDate() {
        final ZonedDateTime now = ZonedDateTime.now(GMT_ZONE_ID);
        final LocalDateTime old = CURRENT_DATE_GENERATED.get();

        if (old.until(now, ChronoUnit.MILLIS) > 1000) {
            currentDate = FORMAT_RFC5322.format(now);
            CURRENT_DATE_GENERATED.compareAndSet(old, now.toLocalDateTime());
        }
        return currentDate;
    }


    /**
     * Get the HTTP format of the specified date.
     * @param value The date
     * @return the HTTP date
     */
    public static final String formatDate(long value) {
        Long longValue = Long.valueOf(value);
        String cachedDate = FORMAT_CACHE.get(longValue);
        if (cachedDate != null) {
            return cachedDate;
        }

        final LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(value), GMT_ZONE_ID);
        String newDate = FORMAT_RFC5322.format(dateTime);
        updateFormatCache(longValue, newDate);
        return newDate;
    }


    /**
     * Try to parse the given date as an HTTP date.
     * @param value The HTTP date
     * @return the date as a long or <code>-1</code> if the value cannot be
     *         parsed
     */
    public static final long parseDate(String value) {

        Long cachedDate = PARSE_CACHE.get(value);
        if (cachedDate != null) {
            return cachedDate.longValue();
        }

        long date = -1;
        for (int i = 0; (date == -1) && (i < httpParseFormats.length); i++) {
            try {
                final DateTimeFormatter dateTimeFormatter = httpParseFormats[i];
                final TemporalAccessor temporalAccessor = dateTimeFormatter.parse(value);
                date = Instant.from(temporalAccessor).toEpochMilli();
                updateParseCache(value, Long.valueOf(date));
            } catch (DateTimeParseException e) {
//                e.printStackTrace();
            }
        }

        return date;
    }


    /**
     * Update cache.
     */
    private static void updateFormatCache(Long key, String value) {
        if (value == null) {
            return;
        }
        if (FORMAT_CACHE.size() > CACHE_SIZE) {
            FORMAT_CACHE.clear();
        }
        FORMAT_CACHE.put(key, value);
    }


    /**
     * Update cache.
     */
    private static void updateParseCache(String key, Long value) {
        if (value == null) {
            return;
        }
        if (PARSE_CACHE.size() > CACHE_SIZE) {
            PARSE_CACHE.clear();
        }
        PARSE_CACHE.put(key, value);
    }


}
