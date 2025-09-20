/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.utils;

public final class Constants {
    private Constants() {}

    public static final class HttpConstants {
        private HttpConstants() {}

        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_XML = "application/xml";
        public static final String TEXT_PLAIN = "text/plain";
        public static final String HEADER_KEY_CONTENT_TYPE = "Content-Type";
        public static final String HEADER_KEY_ACCEPT = "Accept";
    }
}
