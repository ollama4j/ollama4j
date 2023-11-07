package io.github.amithkoujalgi.ollama4j;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.types.OllamaModelType;
import io.github.amithkoujalgi.ollama4j.core.utils.SamplePrompts;

public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        String prompt1 = SamplePrompts.getSampleDatabasePromptWithQuestion("List all customer names who have bought one or more products");
        String response1 = ollamaAPI.ask(OllamaModelType.LLAMA2, prompt1);
        System.out.println(response1);

        String prompt2 = "Give me a list of world cup cricket teams.";
        String response2 = ollamaAPI.ask(OllamaModelType.LLAMA2, prompt2);
        System.out.println(response2);
    }
}
