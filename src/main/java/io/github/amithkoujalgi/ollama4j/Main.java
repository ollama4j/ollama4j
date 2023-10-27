package io.github.amithkoujalgi.ollama4j;

public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.pullModel(OllamaModel.LLAMA2);
        String response = ollamaAPI.runSync(OllamaModel.LLAMA2, "Who are you?");
        System.out.println(response);
    }
}


