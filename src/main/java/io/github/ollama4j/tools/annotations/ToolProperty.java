package io.github.ollama4j.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a Method Parameter in a {@link ToolSpec} annotated Method. A parameter annotated with this annotation will
 * be part of the tool description that is sent to the llm for tool-calling.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ToolProperty {

    /**
     * @return name of the parameter that is used for the tool description. Has to be set as depending on the caller,
     * method name backtracking is not possible with reflection.
     */
    String name();

    /**
     * @return a detailed description of the parameter. This is used by the llm called to specify, which property has
     * to be set by the llm and how this should be filled.
     */
    String desc();

    /**
     * @return tells the llm that it has to set a value for this property.
     */
    boolean required() default true;
}
