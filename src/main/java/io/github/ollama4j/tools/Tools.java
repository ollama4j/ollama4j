/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Tools {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tool {
        @JsonProperty("function")
        private ToolSpec toolSpec;

        private String type = "function";
        @JsonIgnore private ToolFunction toolFunction;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolSpec {
        private String name;
        private String description;
        private Parameters parameters;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private Map<String, Property> properties;
        private List<String> required = new ArrayList<>();

        public static Parameters of(Map<String, Property> properties) {
            Parameters params = new Parameters();
            params.setProperties(properties);
            // Optionally, populate required from properties' required flags
            if (properties != null) {
                for (Map.Entry<String, Property> entry : properties.entrySet()) {
                    if (entry.getValue() != null && entry.getValue().isRequired()) {
                        params.getRequired().add(entry.getKey());
                    }
                }
            }
            return params;
        }

        @Override
        public String toString() {
            ObjectNode node =
                    com.fasterxml.jackson.databind.json.JsonMapper.builder()
                            .build()
                            .createObjectNode();
            node.put("type", "object");
            if (properties != null) {
                ObjectNode propsNode = node.putObject("properties");
                for (Map.Entry<String, Property> entry : properties.entrySet()) {
                    ObjectNode propNode = propsNode.putObject(entry.getKey());
                    Property prop = entry.getValue();
                    propNode.put("type", prop.getType());
                    propNode.put("description", prop.getDescription());
                    if (prop.getEnumValues() != null) {
                        propNode.putArray("enum")
                                .addAll(
                                        prop.getEnumValues().stream()
                                                .map(
                                                        com.fasterxml.jackson.databind.node.TextNode
                                                                ::new)
                                                .collect(java.util.stream.Collectors.toList()));
                    }
                }
            }
            if (required != null && !required.isEmpty()) {
                node.putArray("required")
                        .addAll(
                                required.stream()
                                        .map(com.fasterxml.jackson.databind.node.TextNode::new)
                                        .collect(java.util.stream.Collectors.toList()));
            }
            return node.toPrettyString();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {
        private String type;
        private String description;

        @JsonProperty("enum")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<String> enumValues;

        @JsonIgnore private boolean required;
    }
}
