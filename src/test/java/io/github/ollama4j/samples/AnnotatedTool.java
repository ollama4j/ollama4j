package io.github.ollama4j.samples;

import io.github.ollama4j.tools.annotations.ToolProperty;
import io.github.ollama4j.tools.annotations.ToolSpec;

import java.math.BigDecimal;

public class AnnotatedTool {

    @ToolSpec(desc = "Computes the most important constant all around the globe!")
    public String computeImportantConstant(@ToolProperty(name = "noOfDigits",desc = "Number of digits that shall be returned") Integer noOfDigits ){
        return BigDecimal.valueOf((long)(Math.random()*1000000L),noOfDigits).toString();
    }

    @ToolSpec(desc = "Says hello to a friend!")
    public String sayHello(@ToolProperty(name = "name",desc = "Name of the friend") String name, Integer someRandomProperty, @ToolProperty(name="amountOfHearts",desc = "amount of heart emojis that should be used",  required = false) Integer amountOfHearts) {
        String hearts = amountOfHearts!=null ? "♡".repeat(amountOfHearts) : "";
        return "Hello " + name +" ("+someRandomProperty+") " + hearts;
    }

}
