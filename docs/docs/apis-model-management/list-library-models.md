---
sidebar_position: 6
---

# List Models from Library

This API retrieves a list of models from the Ollama library. It fetches available models directly from the Ollama
library page, including details such as the model's name, pull count, popular tags, tag count, and the last update time.

```java title="ListLibraryModels.java"
import io.github.ollama4j.OllamaAPI;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ListModels {

    public static void main(String[] args) {

        String host = "http://localhost:11434/";

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(ollamaAPI.listModelsFromLibrary()));
    }
}
```

The following is the sample response:

```json
[
  {
    "name": "llama3.2-vision",
    "pullCount": "16K",
    "numTags": 9,
    "popularTags": [
      "vision",
      "11b",
      "90b"
    ],
    "updatedAt": "20 hours ago"
  },
  {
    "name": "llama3.2",
    "pullCount": "2.4M",
    "numTags": 63,
    "popularTags": [
      "tools",
      "1b",
      "3b"
    ],
    "updatedAt": "6 weeks ago"
  },
  {
    "name": "llama3.1",
    "pullCount": "8.6M",
    "numTags": 93,
    "popularTags": [
      "tools",
      "8b",
      "70b",
      "405b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "gemma2",
    "pullCount": "1.8M",
    "numTags": 94,
    "popularTags": [
      "2b",
      "9b",
      "27b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "qwen2.5",
    "pullCount": "1.9M",
    "numTags": 133,
    "popularTags": [
      "tools",
      "0.5b",
      "1.5b",
      "3b",
      "7b",
      "14b",
      "32b",
      "72b"
    ],
    "updatedAt": "7 weeks ago"
  },
  {
    "name": "phi3.5",
    "pullCount": "163.3K",
    "numTags": 17,
    "popularTags": [
      "3.8b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "nemotron-mini",
    "pullCount": "33.6K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "4b"
    ],
    "updatedAt": "7 weeks ago"
  },
  {
    "name": "mistral-small",
    "pullCount": "39.8K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "22b"
    ],
    "updatedAt": "7 weeks ago"
  },
  {
    "name": "mistral-nemo",
    "pullCount": "481.3K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "12b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "deepseek-coder-v2",
    "pullCount": "393.5K",
    "numTags": 64,
    "popularTags": [
      "16b",
      "236b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "mistral",
    "pullCount": "4.9M",
    "numTags": 84,
    "popularTags": [
      "tools",
      "7b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "mixtral",
    "pullCount": "472.8K",
    "numTags": 69,
    "popularTags": [
      "tools",
      "8x7b",
      "8x22b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "codegemma",
    "pullCount": "352.5K",
    "numTags": 85,
    "popularTags": [
      "2b",
      "7b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "command-r",
    "pullCount": "238.1K",
    "numTags": 32,
    "popularTags": [
      "tools",
      "35b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "command-r-plus",
    "pullCount": "103.4K",
    "numTags": 21,
    "popularTags": [
      "tools",
      "104b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "llava",
    "pullCount": "1.7M",
    "numTags": 98,
    "popularTags": [
      "vision",
      "7b",
      "13b",
      "34b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "llama3",
    "pullCount": "6.6M",
    "numTags": 68,
    "popularTags": [
      "8b",
      "70b"
    ],
    "updatedAt": "5 months ago"
  },
  {
    "name": "gemma",
    "pullCount": "4.2M",
    "numTags": 102,
    "popularTags": [
      "2b",
      "7b"
    ],
    "updatedAt": "7 months ago"
  },
  {
    "name": "qwen",
    "pullCount": "4.1M",
    "numTags": 379,
    "popularTags": [
      "0.5b",
      "1.8b",
      "4b",
      "7b",
      "14b",
      "32b",
      "72b",
      "110b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "qwen2",
    "pullCount": "3.9M",
    "numTags": 97,
    "popularTags": [
      "tools",
      "0.5b",
      "1.5b",
      "7b",
      "72b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "phi3",
    "pullCount": "2.7M",
    "numTags": 72,
    "popularTags": [
      "3.8b",
      "14b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "nomic-embed-text",
    "pullCount": "2.3M",
    "numTags": 3,
    "popularTags": [
      "embedding"
    ],
    "updatedAt": "8 months ago"
  },
  {
    "name": "llama2",
    "pullCount": "2.3M",
    "numTags": 102,
    "popularTags": [
      "7b",
      "13b",
      "70b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "codellama",
    "pullCount": "1.5M",
    "numTags": 199,
    "popularTags": [
      "7b",
      "13b",
      "34b",
      "70b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "mxbai-embed-large",
    "pullCount": "539K",
    "numTags": 4,
    "popularTags": [
      "embedding",
      "335m"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "dolphin-mixtral",
    "pullCount": "430.9K",
    "numTags": 87,
    "popularTags": [
      "8x7b",
      "8x22b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "starcoder2",
    "pullCount": "417.5K",
    "numTags": 67,
    "popularTags": [
      "3b",
      "7b",
      "15b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "phi",
    "pullCount": "377.6K",
    "numTags": 18,
    "popularTags": [
      "2.7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "deepseek-coder",
    "pullCount": "364K",
    "numTags": 102,
    "popularTags": [
      "1.3b",
      "6.7b",
      "33b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "llama2-uncensored",
    "pullCount": "351.9K",
    "numTags": 34,
    "popularTags": [
      "7b",
      "70b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "qwen2.5-coder",
    "pullCount": "266.8K",
    "numTags": 67,
    "popularTags": [
      "tools",
      "1.5b",
      "7b"
    ],
    "updatedAt": "4 weeks ago"
  },
  {
    "name": "tinyllama",
    "pullCount": "266.5K",
    "numTags": 36,
    "popularTags": [
      "1.1b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "dolphin-mistral",
    "pullCount": "260.1K",
    "numTags": 120,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "7 months ago"
  },
  {
    "name": "yi",
    "pullCount": "237.6K",
    "numTags": 174,
    "popularTags": [
      "6b",
      "9b",
      "34b"
    ],
    "updatedAt": "5 months ago"
  },
  {
    "name": "dolphin-llama3",
    "pullCount": "234.9K",
    "numTags": 53,
    "popularTags": [
      "8b",
      "70b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "orca-mini",
    "pullCount": "229.6K",
    "numTags": 119,
    "popularTags": [
      "3b",
      "7b",
      "13b",
      "70b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "zephyr",
    "pullCount": "222.1K",
    "numTags": 40,
    "popularTags": [
      "7b",
      "141b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "llava-llama3",
    "pullCount": "208.8K",
    "numTags": 4,
    "popularTags": [
      "vision",
      "8b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "snowflake-arctic-embed",
    "pullCount": "181.7K",
    "numTags": 16,
    "popularTags": [
      "embedding",
      "22m",
      "33m",
      "110m",
      "137m",
      "335m"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "starcoder",
    "pullCount": "164.1K",
    "numTags": 100,
    "popularTags": [
      "1b",
      "3b",
      "7b",
      "15b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "codestral",
    "pullCount": "160K",
    "numTags": 17,
    "popularTags": [
      "22b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "mistral-openorca",
    "pullCount": "159.5K",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "13 months ago"
  },
  {
    "name": "vicuna",
    "pullCount": "155.3K",
    "numTags": 111,
    "popularTags": [
      "7b",
      "13b",
      "33b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "wizardlm2",
    "pullCount": "148.6K",
    "numTags": 22,
    "popularTags": [
      "7b",
      "8x22b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "granite-code",
    "pullCount": "146.1K",
    "numTags": 162,
    "popularTags": [
      "3b",
      "8b",
      "20b",
      "34b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "wizard-vicuna-uncensored",
    "pullCount": "137.2K",
    "numTags": 49,
    "popularTags": [
      "7b",
      "13b",
      "30b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "llama2-chinese",
    "pullCount": "136.1K",
    "numTags": 35,
    "popularTags": [
      "7b",
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "codegeex4",
    "pullCount": "123.2K",
    "numTags": 17,
    "popularTags": [
      "9b"
    ],
    "updatedAt": "4 months ago"
  },
  {
    "name": "all-minilm",
    "pullCount": "121K",
    "numTags": 10,
    "popularTags": [
      "embedding",
      "22m",
      "33m"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "openchat",
    "pullCount": "114.7K",
    "numTags": 50,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "nous-hermes2",
    "pullCount": "114.2K",
    "numTags": 33,
    "popularTags": [
      "10.7b",
      "34b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "aya",
    "pullCount": "112.9K",
    "numTags": 33,
    "popularTags": [
      "8b",
      "35b"
    ],
    "updatedAt": "5 months ago"
  },
  {
    "name": "codeqwen",
    "pullCount": "111.9K",
    "numTags": 30,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "4 months ago"
  },
  {
    "name": "tinydolphin",
    "pullCount": "105K",
    "numTags": 18,
    "popularTags": [
      "1.1b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "wizardcoder",
    "pullCount": "104.4K",
    "numTags": 67,
    "popularTags": [
      "33b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "stable-code",
    "pullCount": "102.8K",
    "numTags": 36,
    "popularTags": [
      "3b"
    ],
    "updatedAt": "7 months ago"
  },
  {
    "name": "openhermes",
    "pullCount": "100.6K",
    "numTags": 35,
    "popularTags": [],
    "updatedAt": "10 months ago"
  },
  {
    "name": "mistral-large",
    "pullCount": "98.5K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "123b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "qwen2-math",
    "pullCount": "98.2K",
    "numTags": 52,
    "popularTags": [
      "1.5b",
      "7b",
      "72b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "bakllava",
    "pullCount": "96.3K",
    "numTags": 17,
    "popularTags": [
      "vision",
      "7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "reflection",
    "pullCount": "95.7K",
    "numTags": 17,
    "popularTags": [
      "70b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "stablelm2",
    "pullCount": "95.3K",
    "numTags": 84,
    "popularTags": [
      "1.6b",
      "12b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "glm4",
    "pullCount": "91K",
    "numTags": 32,
    "popularTags": [
      "9b"
    ],
    "updatedAt": "4 months ago"
  },
  {
    "name": "deepseek-llm",
    "pullCount": "89.4K",
    "numTags": 64,
    "popularTags": [
      "7b",
      "67b"
    ],
    "updatedAt": "11 months ago"
  },
  {
    "name": "llama3-gradient",
    "pullCount": "89.2K",
    "numTags": 35,
    "popularTags": [
      "8b",
      "70b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "wizard-math",
    "pullCount": "88.5K",
    "numTags": 64,
    "popularTags": [
      "7b",
      "13b",
      "70b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "neural-chat",
    "pullCount": "81.5K",
    "numTags": 50,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "smollm",
    "pullCount": "79.4K",
    "numTags": 94,
    "popularTags": [
      "135m",
      "360m",
      "1.7b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "moondream",
    "pullCount": "78.6K",
    "numTags": 18,
    "popularTags": [
      "vision",
      "1.8b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "llama3-chatqa",
    "pullCount": "77.2K",
    "numTags": 35,
    "popularTags": [
      "8b",
      "70b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "xwinlm",
    "pullCount": "77.1K",
    "numTags": 80,
    "popularTags": [
      "7b",
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "sqlcoder",
    "pullCount": "75.6K",
    "numTags": 48,
    "popularTags": [
      "7b",
      "15b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "nous-hermes",
    "pullCount": "75.1K",
    "numTags": 63,
    "popularTags": [
      "7b",
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "phind-codellama",
    "pullCount": "74K",
    "numTags": 49,
    "popularTags": [
      "34b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "yarn-llama2",
    "pullCount": "72K",
    "numTags": 67,
    "popularTags": [
      "7b",
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "dolphincoder",
    "pullCount": "71K",
    "numTags": 35,
    "popularTags": [
      "7b",
      "15b"
    ],
    "updatedAt": "7 months ago"
  },
  {
    "name": "wizardlm",
    "pullCount": "70.6K",
    "numTags": 73,
    "popularTags": [],
    "updatedAt": "12 months ago"
  },
  {
    "name": "deepseek-v2",
    "pullCount": "65.6K",
    "numTags": 34,
    "popularTags": [
      "16b",
      "236b"
    ],
    "updatedAt": "4 months ago"
  },
  {
    "name": "starling-lm",
    "pullCount": "61.6K",
    "numTags": 36,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "7 months ago"
  },
  {
    "name": "samantha-mistral",
    "pullCount": "60.6K",
    "numTags": 49,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "falcon",
    "pullCount": "59.5K",
    "numTags": 38,
    "popularTags": [
      "7b",
      "40b",
      "180b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "solar",
    "pullCount": "57.8K",
    "numTags": 32,
    "popularTags": [
      "10.7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "orca2",
    "pullCount": "56.5K",
    "numTags": 33,
    "popularTags": [
      "7b",
      "13b"
    ],
    "updatedAt": "11 months ago"
  },
  {
    "name": "yi-coder",
    "pullCount": "55.5K",
    "numTags": 67,
    "popularTags": [
      "1.5b",
      "9b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "internlm2",
    "pullCount": "54.3K",
    "numTags": 65,
    "popularTags": [
      "1m",
      "1.8b",
      "7b",
      "20b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "hermes3",
    "pullCount": "54.2K",
    "numTags": 49,
    "popularTags": [
      "tools",
      "8b",
      "70b",
      "405b"
    ],
    "updatedAt": "2 months ago"
  },
  {
    "name": "stable-beluga",
    "pullCount": "54K",
    "numTags": 49,
    "popularTags": [
      "7b",
      "13b",
      "70b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "llava-phi3",
    "pullCount": "49.1K",
    "numTags": 4,
    "popularTags": [
      "vision",
      "3.8b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "dolphin-phi",
    "pullCount": "48.7K",
    "numTags": 15,
    "popularTags": [
      "2.7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "wizardlm-uncensored",
    "pullCount": "45.9K",
    "numTags": 18,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "yarn-mistral",
    "pullCount": "41.5K",
    "numTags": 33,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "llama-pro",
    "pullCount": "41.1K",
    "numTags": 33,
    "popularTags": [],
    "updatedAt": "10 months ago"
  },
  {
    "name": "medllama2",
    "pullCount": "38.6K",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "meditron",
    "pullCount": "38K",
    "numTags": 22,
    "popularTags": [
      "7b",
      "70b"
    ],
    "updatedAt": "11 months ago"
  },
  {
    "name": "nexusraven",
    "pullCount": "37.6K",
    "numTags": 32,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "minicpm-v",
    "pullCount": "36.7K",
    "numTags": 17,
    "popularTags": [
      "vision",
      "8b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "llama3-groq-tool-use",
    "pullCount": "35.6K",
    "numTags": 33,
    "popularTags": [
      "tools",
      "8b",
      "70b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "nous-hermes2-mixtral",
    "pullCount": "34.7K",
    "numTags": 18,
    "popularTags": [
      "8x7b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "codeup",
    "pullCount": "33.2K",
    "numTags": 19,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "everythinglm",
    "pullCount": "31.4K",
    "numTags": 18,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "magicoder",
    "pullCount": "28.5K",
    "numTags": 18,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "11 months ago"
  },
  {
    "name": "stablelm-zephyr",
    "pullCount": "27.8K",
    "numTags": 17,
    "popularTags": [
      "3b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "bge-m3",
    "pullCount": "27.7K",
    "numTags": 3,
    "popularTags": [
      "embedding",
      "567m"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "codebooga",
    "pullCount": "27.5K",
    "numTags": 16,
    "popularTags": [
      "34b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "nemotron",
    "pullCount": "27.4K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "70b"
    ],
    "updatedAt": "3 weeks ago"
  },
  {
    "name": "wizard-vicuna",
    "pullCount": "26.6K",
    "numTags": 17,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "falcon2",
    "pullCount": "26.3K",
    "numTags": 17,
    "popularTags": [
      "11b"
    ],
    "updatedAt": "5 months ago"
  },
  {
    "name": "mistrallite",
    "pullCount": "26K",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "duckdb-nsql",
    "pullCount": "24.8K",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "9 months ago"
  },
  {
    "name": "megadolphin",
    "pullCount": "22.9K",
    "numTags": 19,
    "popularTags": [
      "120b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "notux",
    "pullCount": "21.9K",
    "numTags": 18,
    "popularTags": [
      "8x7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "open-orca-platypus2",
    "pullCount": "21.2K",
    "numTags": 17,
    "popularTags": [
      "13b"
    ],
    "updatedAt": "12 months ago"
  },
  {
    "name": "notus",
    "pullCount": "21.2K",
    "numTags": 18,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "10 months ago"
  },
  {
    "name": "goliath",
    "pullCount": "21.1K",
    "numTags": 16,
    "popularTags": [],
    "updatedAt": "11 months ago"
  },
  {
    "name": "mathstral",
    "pullCount": "20.5K",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "solar-pro",
    "pullCount": "19.5K",
    "numTags": 18,
    "popularTags": [
      "22b"
    ],
    "updatedAt": "7 weeks ago"
  },
  {
    "name": "reader-lm",
    "pullCount": "17.2K",
    "numTags": 33,
    "popularTags": [
      "0.5b",
      "1.5b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "granite3-dense",
    "pullCount": "16.6K",
    "numTags": 33,
    "popularTags": [
      "tools",
      "2b",
      "8b"
    ],
    "updatedAt": "2 weeks ago"
  },
  {
    "name": "dbrx",
    "pullCount": "16.3K",
    "numTags": 7,
    "popularTags": [
      "132b"
    ],
    "updatedAt": "6 months ago"
  },
  {
    "name": "nuextract",
    "pullCount": "16.1K",
    "numTags": 17,
    "popularTags": [
      "3.8b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "firefunction-v2",
    "pullCount": "13.7K",
    "numTags": 17,
    "popularTags": [
      "tools",
      "70b"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "alfred",
    "pullCount": "13K",
    "numTags": 7,
    "popularTags": [
      "40b"
    ],
    "updatedAt": "11 months ago"
  },
  {
    "name": "granite3-moe",
    "pullCount": "11K",
    "numTags": 33,
    "popularTags": [
      "tools",
      "1b",
      "3b"
    ],
    "updatedAt": "2 weeks ago"
  },
  {
    "name": "bge-large",
    "pullCount": "10.7K",
    "numTags": 3,
    "popularTags": [
      "embedding",
      "335m"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "aya-expanse",
    "pullCount": "9,500",
    "numTags": 33,
    "popularTags": [
      "tools",
      "8b",
      "32b"
    ],
    "updatedAt": "13 days ago"
  },
  {
    "name": "bespoke-minicheck",
    "pullCount": "9,087",
    "numTags": 17,
    "popularTags": [
      "7b"
    ],
    "updatedAt": "7 weeks ago"
  },
  {
    "name": "deepseek-v2.5",
    "pullCount": "8,596",
    "numTags": 7,
    "popularTags": [
      "236b"
    ],
    "updatedAt": "8 weeks ago"
  },
  {
    "name": "smollm2",
    "pullCount": "7,910",
    "numTags": 49,
    "popularTags": [
      "tools",
      "135m",
      "360m",
      "1.7b"
    ],
    "updatedAt": "6 days ago"
  },
  {
    "name": "shieldgemma",
    "pullCount": "7,835",
    "numTags": 49,
    "popularTags": [
      "2b",
      "9b",
      "27b"
    ],
    "updatedAt": "3 weeks ago"
  },
  {
    "name": "llama-guard3",
    "pullCount": "7,002",
    "numTags": 33,
    "popularTags": [
      "1b",
      "8b"
    ],
    "updatedAt": "3 weeks ago"
  },
  {
    "name": "paraphrase-multilingual",
    "pullCount": "6,222",
    "numTags": 3,
    "popularTags": [
      "embedding",
      "278m"
    ],
    "updatedAt": "3 months ago"
  },
  {
    "name": "granite3-guardian",
    "pullCount": "937",
    "numTags": 10,
    "popularTags": [
      "2b",
      "8b"
    ],
    "updatedAt": "6 days ago"
  }
]
```