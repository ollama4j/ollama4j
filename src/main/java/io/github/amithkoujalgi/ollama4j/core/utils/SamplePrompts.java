package io.github.amithkoujalgi.ollama4j.core.utils;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;

import java.io.InputStream;
import java.util.Scanner;

public class SamplePrompts {
    public static String getSampleDatabasePromptWithQuestion(String question) throws Exception {
        ClassLoader classLoader = OllamaAPI.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("sample-db-prompt-template.txt");
        if (inputStream != null) {
            Scanner scanner = new Scanner(inputStream);
            StringBuilder stringBuffer = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuffer.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return stringBuffer.toString().replaceAll("<question>", question);
        } else {
            throw new Exception("Sample database question file not found.");
        }
    }

}
