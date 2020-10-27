package com.aspirecsl.json.io;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import static java.lang.String.format;

/**
 * Reads the value of a node from the specified JSON object.
 *
 * @author anoopr
 * @version 1C2
 * @since 1C2
 */
public class JsonReader {

    /**
     * Class level logger
     */
    private static final Logger log = LoggerFactory.getLogger(JsonReader.class);

    /**
     * Returns the <tt>String</tt> value of the node specified by the <tt>jsonPath</tt> from the JSON <tt>input</tt>.
     *
     * @param input    the JSON string to read a value from
     * @param jsonPath the path of the node in the JSON <tt>input</tt> whose text value is returned
     * @return the <tt>String</tt> value associated with the specified <tt>jsonPath</tt> in the given JSON <tt>input</tt>
     * @throws RuntimeException if the <tt>jsonPath</tt> doesn't correspond to a value node in the JSON <tt>input</tt>
     */
    public String readValueAsText(JsonNode input, String jsonPath) {
        JsonNode node = readNode(input, jsonPath, JsonNode.class);
        if (node.isValueNode()) {
            return node.asText();
        } else {
            log.error("JSON input does not contain a value node at '{}'", jsonPath);
            throw new RuntimeException(format("JSON input does not contain a value node at '%s'", jsonPath));
        }
    }

    /**
     * Returns the node specified by the <tt>jsonPath</tt> from the JSON <tt>input</tt>.
     *
     * @param input    the JSON string to read a value from
     * @param jsonPath the path of the node to be returned from the JSON <tt>input</tt>
     * @param <T>      the class corresponding to the type of the node returned (sub class of <tt>JsonNode</tt>)
     * @param nodeType the actual type of the node that is returned (sub class of <tt>JsonNode</tt>)
     * @return the <tt>String</tt> value associated with the specified <tt>jsonPath</tt> in the given JSON <tt>input</tt>
     * @throws RuntimeException         if a node with the specified <tt>jsonPath</tt> doesn't exist in the JSON <tt>input</tt>
     * @throws IllegalArgumentException if the specified <tt>jsonPath</tt> is invalid
     */
    public <T extends JsonNode> T readNode(JsonNode input, String jsonPath, Class<T> nodeType) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(jsonPath);
        if (!jsonPath.matches("^[a-zA-Z][-a-zA-Z0-9_]*(?:\\.[a-zA-Z][-a-zA-Z0-9_]*)*$")) {
            log.error("<{}> is not a valid json path", jsonPath);
            throw new IllegalArgumentException(format("<%s> is not a valid json path", jsonPath));
        }
        final String[] nodeNames = jsonPath.split("\\.");
        JsonNode node = input;
        String nodeFqn = "";
        for (String nodeName : nodeNames) {
            nodeFqn = nodeFqn.isEmpty() ? nodeName : format("%s.%s", nodeFqn, nodeName);
            if (node.has(nodeName)) {
                node = node.get(nodeName);
            } else {
                log.error("JSON input does not contain a node with the path <{}>", nodeFqn);
                throw new RuntimeException(format("JSON input does not contain a node with the path <%s>", nodeFqn));
            }
        }
        return nodeType.cast(node);
    }
}
