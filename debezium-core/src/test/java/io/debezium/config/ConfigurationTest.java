/*
 * Copyright Debezium Authors.
 * 
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.config;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Randall Hauch
 *
 */
public class ConfigurationTest {

    private Configuration config;
    
    @Before
    public void beforeEach() {
        config = Configuration.create().with("A", "a")
                .with("B", "b")
                .with("1", 1)
                .build();
    }
    
    @Test
    public void shouldConvertFromProperties() {
        Properties props = new Properties();
        props.setProperty("A", "a");
        props.setProperty("B", "b");
        props.setProperty("1", "1");
        config = Configuration.from(props);
        assertThat(config.getString("A")).isEqualTo("a");
        assertThat(config.getString("B")).isEqualTo("b");
        assertThat(config.getString("1")).isEqualTo("1");
        assertThat(config.getInteger("1")).isEqualTo(1);    // converts
        assertThat(config.getBoolean("1")).isNull();    // not a boolean
    }
    
    @Test
    public void shouldCallFunctionOnEachMatchingFieldUsingRegex() {
        config = Configuration.create()
                .with("column.truncate.to.-10.chars", "should-not-be-matched")
                .with("column.truncate.to.10.chars", "10-chars")
                .with("column.truncate.to.20.chars", "20-chars")
                .with("column.mask.with.20.chars", "20-mask")
                .with("column.mask.with.0.chars", "0-mask")
                .with("column.mask.with.chars", "should-not-be-matched")
                .build();
        
        // Use a regex that captures an integer using a regex group ...
        AtomicInteger counter = new AtomicInteger();
        config.forEachMatchingFieldNameWithInteger("column\\.truncate\\.to\\.(\\d+)\\.chars",(value,n)->{
            counter.incrementAndGet();
            assertThat(value).isEqualTo(Integer.toString(n) + "-chars");
        });
        assertThat(counter.get()).isEqualTo(2);
        
        // Use a regex that captures an integer using a regex group ...
        counter.set(0);
        config.forEachMatchingFieldNameWithInteger("column.mask.with.(\\d+).chars",(value,n)->{
            counter.incrementAndGet();
            assertThat(value).isEqualTo(Integer.toString(n) + "-mask");
        });
        assertThat(counter.get()).isEqualTo(2);
        
        // Use a regex that matches the name but also uses a regex group ...
        counter.set(0);
        config.forEachMatchingFieldName("column.mask.with.(\\d+).chars",(name,value)->{
            counter.incrementAndGet();
            assertThat(name).startsWith("column.mask.with.");
            assertThat(name).endsWith(".chars");
            assertThat(value).endsWith("-mask");
        });
        assertThat(counter.get()).isEqualTo(2);
        
        // Use a regex that matches all of our fields ...
        counter.set(0);
        config.forEachMatchingFieldName("column.*",(name,value)->{
            counter.incrementAndGet();
            assertThat(name).startsWith("column.");
            assertThat(name).endsWith(".chars");
            assertThat(value).isNotNull();
        });
        assertThat(counter.get()).isEqualTo(6);
    }

}
