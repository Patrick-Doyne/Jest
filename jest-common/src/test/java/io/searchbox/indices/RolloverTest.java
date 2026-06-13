package io.searchbox.indices;

import com.google.gson.Gson;
import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RolloverTest {

    Map<String, Object> rolloverConditions = Collections.unmodifiableMap(
        new LinkedHashMap<String, Object>() {{
            put("max_age", "1d");
            put("max_docs", "10000");
        }}
    );

    Map<String, Object> rolloverSettings = Collections.unmodifiableMap(
        new LinkedHashMap<String, Object>() {{
            put("index.number_of_shards", "2");
        }}
    );

    @Test
    public void testBasicUriGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        assertEquals("POST", rollover.getRestMethodName());
        assertEquals("twitter/_rollover", rollover.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("{\"conditions\":{\"max_age\":\"1d\",\"max_docs\":\"10000\"}}", rollover.getData(new Gson()));
    }

    @Test
    public void testBasicUriWithSettingsGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).settings(rolloverSettings).build();
        assertEquals("POST", rollover.getRestMethodName());
        assertEquals("twitter/_rollover", rollover.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("{\"conditions\":{\"max_age\":\"1d\",\"max_docs\":\"10000\"},\"settings\":{\"index.number_of_shards\":\"2\"}}", rollover.getData(new Gson()));
    }

    @Test
    public void testDryRunUriGeneration() {
        Rollover rollover = new Rollover.Builder("twitter").conditions(rolloverConditions).setDryRun(true).build();
        assertEquals("POST", rollover.getRestMethodName());
        assertEquals("twitter/_rollover?dry_run", rollover.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameDestination() {
        Rollover indexRollover1 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Rollover indexRollover2 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();

        assertEquals(indexRollover1, indexRollover2);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Rollover indexRollover1 = new Rollover.Builder("twitter").conditions(rolloverConditions).build();
        Rollover indexRollover2 = new Rollover.Builder("myspace").conditions(rolloverConditions).build();

        assertNotEquals(indexRollover1, indexRollover2);
    }
}
