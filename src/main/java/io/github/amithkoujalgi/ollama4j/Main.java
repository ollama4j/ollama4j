package io.github.amithkoujalgi.ollama4j;

public class Main {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.pullModel(OllamaModel.LLAMA2);
        OllamaAsyncResultCallback ollamaAsyncResultCallback = ollamaAPI.runAsync(OllamaModel.LLAMA2, "Who are you?");
        while (true) {
            if (ollamaAsyncResultCallback.isComplete()) {
                System.out.println(ollamaAsyncResultCallback.getResponse());
                break;
            }
            Thread.sleep(1000);
        }
    }
}


