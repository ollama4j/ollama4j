package io.github.amithkoujalgi.ollama4j.core.exceptions;

public class ToolInvocationException extends Exception {

    public ToolInvocationException(String s, Exception e) {
        super(s, e);
    }
}
