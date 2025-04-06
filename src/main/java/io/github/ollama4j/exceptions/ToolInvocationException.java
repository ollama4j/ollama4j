package io.github.ollama4j.exceptions;

public class ToolInvocationException extends Exception {

    public ToolInvocationException(String s) {
        super(s);
    }

    public ToolInvocationException(String s, Exception e) {
        super(s, e);
    }
}
