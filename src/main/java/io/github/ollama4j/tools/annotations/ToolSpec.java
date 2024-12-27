package io.github.ollama4j.tools.annotations;

import io.github.ollama4j.OllamaAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates Methods of classes that should be registered as tools by {@link OllamaAPI#registerAnnotatedTools()}
 * automatically.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolSpec {

    /**
     * @return tool-name that the method should be used as. Defaults to the methods name.
     */
    String name() default "";

    /**
     * @return a detailed description of the method that can be interpreted by the llm, whether it should call the tool
     * or not.
     */
    String desc();
}
