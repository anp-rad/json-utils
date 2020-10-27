package com.aspirecsl.json.io;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Unit test for {@link JsonReader} class
 */
public class JsonReaderTest {
    private static final String JSON =
            "{\"root\": {\"branch\":{\"leaf\":\"green\"}, \"branches\": [{\"branch\": {\"leaf\": \"blue\"}},{\"branch\": {\"leaf\": \"yellow\"}}]}}";

    private static final JsonNode NODE;

    static {
        try {
            NODE = new ObjectMapper().readTree(JSON);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private final JsonReader jsonReader = new JsonReader();

    @Test
    public void readsNodeValueTextCorrectly() {
        assertThat(jsonReader.readValueAsText(NODE, "root.branch.leaf"))
                .as("GWResponseReader.readValueAsText(JsonNode,String) reads the node value text correctly")
                .isEqualTo("green");
    }

    @Test
    public void readsNodeCorrectly() {
        final JsonNode node = jsonReader.readNode(NODE, "root.branch", JsonNode.class);
        assertThat(node.isValueNode())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads the node correctly")
                .isFalse();
        assertThat(node.get("leaf").asText())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads the node correctly")
                .isEqualTo("green");
    }

    @Test
    public void readsArrayNodeCorrectly() {
        final JsonNode node = jsonReader.readNode(NODE, "root.branches", ArrayNode.class);
        assertThat(node.isValueNode())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads an array node correctly")
                .isFalse();
        assertThat(node.getClass())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads an array node correctly")
                .isEqualTo(ArrayNode.class);
        assertThat(node.size())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads an array node correctly")
                .isEqualTo(2);
        assertThat(node.get(0).get("branch").get("leaf").asText())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads an array node correctly")
                .isEqualTo("blue");
        assertThat(node.get(1).get("branch").get("leaf").asText())
                .as("GWResponseReader.readNode(JsonNode,String,Class) reads an array node correctly")
                .isEqualTo("yellow");
    }

    @Test
    public void throwsRuntimeExceptionIfReadValueAsTextInvokedOnANonValueNode() {
        assertThrows(RuntimeException.class,
                () -> jsonReader.readValueAsText(NODE, "root.branch"));
    }

    @Test
    public void throwsRuntimeExceptionIfNodeIsAbsent() {
        assertThrows(RuntimeException.class,
                () -> jsonReader.readNode(NODE, "root.branch.undefined", JsonNode.class));
    }

    @Test
    public void throwsIllegalArgumentExceptionInvalidJsonPathIsSpecified() {
        assertThrows(IllegalArgumentException.class,
                () -> jsonReader.readNode(NODE, "1nvalid.path", JsonNode.class));
        assertThrows(IllegalArgumentException.class,
                () -> jsonReader.readNode(NODE, "also.invalid.path.", JsonNode.class));
    }

    @Test
    public void throwsNullPointerExceptionIfTheSpecifiedJsonOrJsonPathIsNull() {
        assertThrows(NullPointerException.class,
                () -> jsonReader.readNode(NODE, null, JsonNode.class));
        assertThrows(NullPointerException.class,
                () -> jsonReader.readNode(null, "root.branch", JsonNode.class));
    }

}