/*
 * Copyright Debezium Authors.
 * 
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.time;

import java.time.LocalTime;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

/**
 * A utility for converting various Java time representations into the {@link SchemaBuilder#int64() INT64} number of
 * <em>microseconds</em> since midnight, and for defining a Kafka Connect {@link Schema} for time values with no date or timezone
 * information.
 * 
 * @author Randall Hauch
 * @see Time
 * @see NanoTime
 */
public class MicroTime {

    public static final String SCHEMA_NAME = "io.debezium.time.MicroTime";

    /**
     * Returns a {@link SchemaBuilder} for a {@link MicroTime}. The resulting schema will describe a field
     * with the {@value #SCHEMA_NAME} as the {@link Schema#name() name} and {@link SchemaBuilder#int64() INT64} for the literal
     * type storing the number of <em>microseconds</em> past midnight.
     * <p>
     * You can use the resulting SchemaBuilder to set or override additional schema settings such as required/optional, default
     * value, and documentation.
     * 
     * @return the schema builder
     */
    public static SchemaBuilder builder() {
        return SchemaBuilder.int64()
                            .name(SCHEMA_NAME)
                            .version(1);
    }

    /**
     * Returns a Schema for a {@link MicroTime} but with all other default Schema settings. The schema describes a field
     * with the {@value #SCHEMA_NAME} as the {@link Schema#name() name} and {@link SchemaBuilder#int64() INT64} for the literal
     * type storing the number of <em>microseconds</em> past midnight.
     * 
     * @return the schema
     * @see #builder()
     */
    public static Schema schema() {
        return builder().build();
    }

    /**
     * Get the number of microseconds past midnight of the given {@link java.time.LocalDateTime}, {@link java.time.LocalDate},
     * {@link java.time.LocalTime}, {@link java.util.Date}, {@link java.sql.Date}, {@link java.sql.Time}, or
     * {@link java.sql.Timestamp}, ignoring any date portions of the supplied value.
     * 
     * @param value the local or SQL date, time, or timestamp value; may not be null
     * @return the microseconds past midnight
     * @throws IllegalArgumentException if the value is not an instance of the acceptable types
     */
    public static long toMicroOfDay(Object value) {
        LocalTime time = Conversions.toLocalTime(value);
        return Math.floorDiv(time.toNanoOfDay(), Conversions.NANOSECONDS_PER_MICROSECOND);
    }

    private MicroTime() {
    }
}
