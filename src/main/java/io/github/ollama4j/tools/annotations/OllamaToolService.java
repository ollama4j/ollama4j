package io.github.ollama4j.tools.annotations;

import io.github.ollama4j.OllamaAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class that calls {@link io.github.ollama4j.OllamaAPI} such that the Method
 * {@link OllamaAPI#registerAnnotatedTools()} can be used to auto-register all provided classes (resp. all
 * contained Methods of the provider classes annotated with {@link ToolSpec}).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OllamaToolService {

    /**
     * @return Classes with no-arg constructor that will be used for tool-registration.
     */
    Class<?>[] providers();
}
