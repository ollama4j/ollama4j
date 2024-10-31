---
sidebar_position: 8
---

# Custom Roles

Allows to manage custom roles (apart from the base roles) for chat interactions with the models.

_Base roles are `SYSTEM`, `USER`, `ASSISTANT`, `TOOL`._

### Usage

#### Add new role

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        OllamaChatMessageRole customRole = ollamaAPI.addCustomRole("custom-role");
    }
}
```

#### List roles

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<OllamaChatMessageRole> roles = ollamaAPI.listRoles();
    }
}
```

#### Get role

```java
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;

public class Main {

    public static void main(String[] args) {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);

        List<OllamaChatMessageRole> roles = ollamaAPI.getRole("custom-role");
    }
}
```