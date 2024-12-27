package io.github.ollama4j.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class ReflectionalToolFunction implements ToolFunction{

    private Object functionHolder;
    private Method function;
    private LinkedHashMap<String,String> propertyDefinition;

    @Override
    public Object apply(Map<String, Object> arguments) {
        LinkedHashMap<String, Object> argumentsCopy = new LinkedHashMap<>(this.propertyDefinition);
        for (Map.Entry<String,String> param : this.propertyDefinition.entrySet()){
            argumentsCopy.replace(param.getKey(),typeCast(arguments.get(param.getKey()),param.getValue()));
        }
        try {
            return function.invoke(functionHolder, argumentsCopy.values().toArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke tool: " + function.getName(), e);
        }
    }

    private Object typeCast(Object inputValue, String className) {
        if(className == null || inputValue == null) {
            return null;
        }
        String inputValueString = inputValue.toString();
        if("java.lang.Integer".equals(className)){
            return Integer.parseInt(inputValueString);
        }
        if("java.lang.Boolean".equals(className)){
            return Boolean.valueOf(inputValueString);
        }
        else {
            return inputValueString;
        }
    }

}
