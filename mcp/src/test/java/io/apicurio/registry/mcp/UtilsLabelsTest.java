package io.apicurio.registry.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.ToolCallException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UtilsLabelsTest {

    private Utils utils;

    @BeforeEach
    void setUp() throws Exception {
        utils = new Utils();
        Field mapperField = Utils.class.getDeclaredField("mapper");
        mapperField.setAccessible(true);
        mapperField.set(utils, new ObjectMapper());
    }

    @Test
    void toQueryLabelsNullReturnsNull() {
        assertNull(utils.toQueryLabels(null));
    }

    @Test
    void toQueryLabelsConvertsValidJsonMap() {
        String json = "{\"env\":\"prod\",\"tier\":\"backend\"}";
        String[] queryLabels = utils.toQueryLabels(json);

        assertNotNull(queryLabels);
        assertEquals(2, queryLabels.length);
        assertTrue(Arrays.asList(queryLabels).contains("env:prod"));
        assertTrue(Arrays.asList(queryLabels).contains("tier:backend"));
    }

    @Test
    void toQueryLabelsInvalidJsonThrowsToolCallException() {
        assertThrows(ToolCallException.class, () -> utils.toQueryLabels("not-a-json"));
    }

    @Test
    void toQueryLabelsNonStringValueThrowsToolCallException() {
        assertThrows(ToolCallException.class, () -> utils.toQueryLabels("{\"count\": 123}"));
    }

    @Test
    void toLabelsConvertsValidJsonMap() {
        String json = "{\"env\":\"prod\",\"tier\":\"backend\"}";
        var labels = utils.toLabels(json);

        assertNotNull(labels);
        assertEquals("prod", labels.getAdditionalData().get("env"));
        assertEquals("backend", labels.getAdditionalData().get("tier"));
    }
}
