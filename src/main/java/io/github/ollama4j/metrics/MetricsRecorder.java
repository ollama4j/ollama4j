/*
 * Ollama4j - Java library for interacting with Ollama server.
 * Copyright (c) 2025 Amith Koujalgi and contributors.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
*/
package io.github.ollama4j.metrics;

import com.google.common.base.Throwables;
import io.github.ollama4j.models.request.ThinkMode;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import java.util.Map;

public class MetricsRecorder {

    // Corrected: Removed duplicate "format" label and ensured label count matches usage
    private static final Counter requests =
            Counter.build()
                    .name("ollama_api_requests_total")
                    .help("Total requests to Ollama API")
                    .labelNames(
                            "endpoint",
                            "model",
                            "raw",
                            "streaming",
                            "thinking",
                            "http_status",
                            "options",
                            "format")
                    .register();

    private static final Histogram requestLatency =
            Histogram.build()
                    .name("ollama_api_request_duration_seconds")
                    .help("Request latency in seconds")
                    .labelNames(
                            "endpoint",
                            "model",
                            "raw",
                            "streaming",
                            "thinking",
                            "http_status",
                            "options",
                            "format")
                    .register();

    private static final Histogram responseSize =
            Histogram.build()
                    .name("ollama_api_response_size_bytes")
                    .help("Response size in bytes")
                    .labelNames("endpoint", "model", "options")
                    .register();

    public static void record(
            String endpoint,
            String model,
            boolean raw,
            ThinkMode thinkMode,
            boolean streaming,
            Map<String, Object> options,
            Object format,
            long startTime,
            int responseHttpStatus,
            Object response) {
        long endTime = System.currentTimeMillis();

        String httpStatus = String.valueOf(responseHttpStatus);

        String formatString = "";
        if (format instanceof String) {
            formatString = (String) format;
        } else if (format instanceof Map) {
            formatString = mapToString((Map<String, Object>) format);
        } else if (format != null) {
            formatString = format.toString();
        }

        // Ensure the number of labels matches the labelNames above (8 labels)
        requests.labels(
                        endpoint,
                        safe(model),
                        String.valueOf(raw),
                        String.valueOf(streaming),
                        String.valueOf(thinkMode),
                        httpStatus,
                        safe(mapToString(options)),
                        safe(formatString))
                .inc();
        double durationSeconds = (endTime - startTime) / 1000.0;

        // Ensure the number of labels matches the labelNames above (8 labels)
        requestLatency
                .labels(
                        endpoint,
                        safe(model),
                        String.valueOf(raw),
                        String.valueOf(streaming),
                        String.valueOf(thinkMode),
                        httpStatus,
                        safe(mapToString(options)),
                        safe(formatString))
                .observe(durationSeconds);

        // Record response size (only if response is a string or json-like object)
        if (response != null) {
            if (response instanceof Exception) {
                response = Throwables.getStackTraceAsString((Throwable) response);
            }
            int size = response.toString().length();
            responseSize.labels(endpoint, safe(model), safe(mapToString(options))).observe(size);
        }
    }

    // Utility method to convert options Map to string (you can adjust this for more detailed
    // representation)
    private static String mapToString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "none";
        }
        // Convert the map to a string (can be customized to fit the use case)
        return map.toString();
    }

    private static String safe(String value) {
        return (value == null || value.isEmpty()) ? "none" : value;
    }
}
