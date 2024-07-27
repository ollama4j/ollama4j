package io.github.ollama4j.models.response;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class OllamaResultStream extends LinkedList<String> implements Queue<String> {
    @Override
    public String poll() {
        StringBuilder tokens = new StringBuilder();
        Iterator<String> iterator = this.listIterator();
        while (iterator.hasNext()) {
            tokens.append(iterator.next());
            iterator.remove();
        }
        return tokens.toString();
    }
}
