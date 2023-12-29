### Ollama4j

<img src='https://raw.githubusercontent.com/amithkoujalgi/ollama4j/65a9d526150da8fcd98e2af6a164f055572bf722/ollama4j.jpeg' width='100' alt="ollama4j-icon">

A Java library (wrapper/binding)
for [Ollama](https://github.com/jmorganca/ollama/blob/main/docs/api.md) APIs.

```mermaid
  flowchart LR
    o4j[Ollama4j]
    o[Ollama Server]
    o4j -->|Communicates with| o;
    m[Models]
    subgraph Ollama Deployment
        direction TB
        o -->|Manages| m
    end
```

![Build Status](https://github.com/amithkoujalgi/ollama4j/actions/workflows/maven-publish.yml/badge.svg)

![GitHub stars](https://img.shields.io/github/stars/amithkoujalgi/ollama4j?style=social)
![GitHub forks](https://img.shields.io/github/forks/amithkoujalgi/ollama4j?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/amithkoujalgi/ollama4j?style=social)
![GitHub repo size](https://img.shields.io/github/repo-size/amithkoujalgi/ollama4j?style=plastic)
![GitHub language count](https://img.shields.io/github/languages/count/amithkoujalgi/ollama4j?style=plastic)
![GitHub top language](https://img.shields.io/github/languages/top/amithkoujalgi/ollama4j?style=plastic)
![GitHub last commit](https://img.shields.io/github/last-commit/amithkoujalgi/ollama4j?color=red&style=plastic)
![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Famithkoujalgi%2Follama4j&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [API Spec](#api-spec)
- [Demo APIs](#try-out-the-apis-with-ollama-server)
- [Development](#development)
- [Contributions](#get-involved)

#### Requirements

![Java](https://img.shields.io/badge/Java-11_+-green.svg?style=just-the-message&labelColor=gray)

[![][ollama-shield]][ollama] **Or** [![][ollama-docker-shield]][ollama-docker]

[ollama]: https://ollama.ai/

[ollama-shield]: https://img.shields.io/badge/Ollama-Local_Installation-blue.svg?style=just-the-message&labelColor=gray

[ollama-docker]: https://hub.docker.com/r/ollama/ollama

[ollama-docker-shield]: https://img.shields.io/badge/Ollama-Docker-blue.svg?style=just-the-message&labelColor=gray

#### Installation

In your Maven project, add this dependency:

```xml

<dependency>
    <groupId>io.github.amithkoujalgi</groupId>
    <artifactId>ollama4j</artifactId>
    <version>1.0.29</version>
</dependency>
```

Latest release: 

![Maven Central](https://img.shields.io/maven-central/v/io.github.amithkoujalgi/ollama4j)

[![][lib-shield]][lib]

[lib]: https://central.sonatype.com/artifact/io.github.amithkoujalgi/ollama4j

[lib-shield]: https://img.shields.io/badge/ollama4j-get_latest_version-blue.svg?style=just-the-message&labelColor=gray

#### API Spec

Find the full `Javadoc` (API specifications) [here](https://amithkoujalgi.github.io/ollama4j/).

#### Development

Build:

```shell
make build
```

Run unit tests:

```shell
make ut
```

Run integration tests:

```shell
make it
```

#### Releases

Releases (newer artifact versions) are done automatically on pushing the code to the `main` branch through GitHub
Actions CI workflow.

#### Traction

[![Star History Chart](https://api.star-history.com/svg?repos=amithkoujalgi/ollama4j&type=Date)](https://star-history.com/#amithkoujalgi/ollama4j&Date)

### Areas of improvement

- [x] Use Java-naming conventions for attributes in the request/response models instead of the
  snake-case conventions. (
  possibly with Jackson-mapper's `@JsonProperty`)
- [x] Fix deprecated HTTP client code
- [x] Setup logging
- [x] Use lombok
- [x] Update request body creation with Java objects
- [ ] Async APIs for images
- [ ] Add additional params for `ask` APIs such as:
    - `options`: additional model parameters for the Modelfile such as `temperature`
    - `system`: system prompt to (overrides what is defined in the Modelfile)
    - `template`: the full prompt or prompt template (overrides what is defined in the Modelfile)
    - `context`: the context parameter returned from a previous request, which can be used to keep a
      short
      conversational memory
    - `stream`: Add support for streaming responses from the model
- [ ] Add test cases
- [ ] Handle exceptions better (maybe throw more appropriate exceptions)

### Get Involved

Contributions are most welcome! Whether it's reporting a bug, proposing an enhancement, or helping
with code - any sort
of contribution is much appreciated.

### Credits

The nomenclature and the icon have been adopted from the incredible [Ollama](https://ollama.ai/)
project.
