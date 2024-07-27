### Ollama4j

<p align="center">
  <img src='https://raw.githubusercontent.com/ollama4j/ollama4j/65a9d526150da8fcd98e2af6a164f055572bf722/ollama4j.jpeg' width='100' alt="ollama4j-icon">
</p>


A Java library (wrapper/binding) for [Ollama](https://ollama.ai/) server.

Find more details on the [website](https://ollama4j.github.io/ollama4j/).

![GitHub stars](https://img.shields.io/github/stars/ollama4j/ollama4j)
![GitHub forks](https://img.shields.io/github/forks/ollama4j/ollama4j)
![GitHub watchers](https://img.shields.io/github/watchers/ollama4j/ollama4j)
![Contributors](https://img.shields.io/github/contributors/ollama4j/ollama4j?style=social)
![GitHub License](https://img.shields.io/github/license/ollama4j/ollama4j)

[//]: # (![GitHub repo size]&#40;https://img.shields.io/github/repo-size/ollama4j/ollama4j&#41;)

[//]: # (![GitHub top language]&#40;https://img.shields.io/github/languages/top/ollama4j/ollama4j&#41;)


[//]: # (![JitPack Downloads This Month Badge]&#40;https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fjitpack.io%2Fapi%2Fdownloads%2Fio.github.ollama4j%2Follama4j&query=%24.month&label=JitPack%20Downloads%20-%20This%20Month&#41;)

[//]: # (![JitPack Downloads This Week Badge]&#40;https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fjitpack.io%2Fapi%2Fdownloads%2Fio.github.ollama4j%2Follama4j&query=%24.week&label=JitPack%20Downloads%20-%20This%20Week&#41;)

[//]: # (![JitPack Downloads Per Month Badge]&#40;https://jitpack.io/v/ollama4j/ollama4j/month.svg&#41;)

[//]: # (![GitHub Downloads &#40;all assets, all releases&#41;]&#40;https://img.shields.io/github/downloads/ollama4j/ollama4j/total?label=GitHub%20Package%20Downloads&#41;)

![GitHub last commit](https://img.shields.io/github/last-commit/ollama4j/ollama4j?color=green)
[![codecov](https://codecov.io/gh/ollama4j/ollama4j/graph/badge.svg?token=U0TE7BGP8L)](https://codecov.io/gh/ollama4j/ollama4j)
![Build Status](https://github.com/ollama4j/ollama4j/actions/workflows/maven-publish.yml/badge.svg)


[//]: # (![Hits]&#40;https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Follama4j%2Follama4j&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false&#41;)

[//]: # (![GitHub language count]&#40;https://img.shields.io/github/languages/count/ollama4j/ollama4j&#41;)

## Table of Contents

- [How does it work?](#how-does-it-work)
- [Requirements](#requirements)
- [Installation](#installation)
- [API Spec](https://ollama4j.github.io/ollama4j/category/apis---model-management)
- [Javadoc](https://ollama4j.github.io/ollama4j/apidocs/)
- [Development](#development)
- [Contributions](#get-involved)
- [References](#references)

#### How does it work?

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

#### Requirements

![Java](https://img.shields.io/badge/Java-11_+-green.svg?style=for-the-badge&labelColor=gray&label=Java&color=orange)


<table>
<tr>
<td> 

[![][ollama-shield]][ollama-link]

</td> 

<td> 

[![][ollama-docker-shield]][ollama-docker]

</td>
</tr>
<tr>
<td>



macOS

https://ollama.com/download/Ollama-darwin.zip

Linux

```shell 
curl -fsSL https://ollama.com/install.sh \| sh
```

Windows

https://ollama.com/download/OllamaSetup.exe


</td>
<td>



CPU only

```shell
docker run -d \
  -v ollama:/root/.ollama \
  -p 11434:11434 \
  --name ollama \
  ollama/ollama
```

NVIDIA GPU

```shell
docker run -d \
  --gpus=all \
  -v ollama:/root/.ollama \
  -p 11434:11434 \
  --name ollama \
  ollama/ollama
```

</td>
</tr>
</table>

[ollama-link]: https://ollama.ai/

[ollama-shield]: https://img.shields.io/badge/Ollama-Local_Installation-blue.svg?style=for-the-badge&labelColor=gray

[ollama-docker]: https://hub.docker.com/r/ollama/ollama

[ollama-docker-shield]: https://img.shields.io/badge/Ollama-Docker-blue.svg?style=for-the-badge&labelColor=gray

## Installation

> [!NOTE]
> We are now publishing the artifacts to both Maven Central and GitHub package repositories.
>
> Track the releases [here](https://github.com/ollama4j/ollama4j/releases) and update the dependency version
> according to your requirements.

### For Maven

#### Using [Maven Central](https://central.sonatype.com/)

[![][ollama4j-mvn-releases-shield]][ollama4j-mvn-releases-link]

[ollama4j-mvn-releases-link]: https://central.sonatype.com/artifact/io.github.ollama4j/ollama4j/overview

[ollama4j-mvn-releases-shield]: https://img.shields.io/maven-central/v/io.github.ollama4j/ollama4j?display_name=release&style=for-the-badge&label=From%20Maven%20Central

In your Maven project, add this dependency:

```xml

<dependency>
    <groupId>io.github.ollama4j</groupId>
    <artifactId>ollama4j</artifactId>
    <version>1.0.78</version>
</dependency>
```

#### Using GitHub's Maven Package Repository

[![][ollama4j-releases-shield]][ollama4j-releases-link]

[ollama4j-releases-link]: https://github.com/ollama4j/ollama4j/releases

[ollama4j-releases-shield]: https://img.shields.io/github/v/release/ollama4j/ollama4j?display_name=release&style=for-the-badge&label=From%20GitHub%20Packages

1. Add `GitHub Maven Packages` repository to your project's `pom.xml` or your `settings.xml`:

```xml

<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/ollama4j/ollama4j</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

2. Add `GitHub` server to settings.xml. (Usually available at ~/.m2/settings.xml)

```xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <username>YOUR-USERNAME</username>
            <password>YOUR-TOKEN</password>
        </server>
    </servers>
</settings>
```

3. In your Maven project, add this dependency:

```xml

<dependency>
    <groupId>io.github.ollama4j</groupId>
    <artifactId>ollama4j</artifactId>
    <version>1.0.78</version>
</dependency>
```

##### For Gradle

1. Add the dependency

```groovy
dependencies {
  implementation 'com.github.ollama4j:ollama4j:1.0.78'
}
```

[//]: # (Latest release:)

[//]: # ()

[//]: # (![Maven Central]&#40;https://img.shields.io/maven-central/v/io.github.ollama4j/ollama4j&#41;)

[//]: # ()

[//]: # ([![][lib-shield]][lib])

[lib]: https://central.sonatype.com/artifact/io.github.ollama4j/ollama4j

[lib-shield]: https://img.shields.io/badge/ollama4j-get_latest_version-blue.svg?style=just-the-message&labelColor=gray

#### API Spec

> [!TIP]
> Find the full API specifications on the [website](https://ollama4j.github.io/ollama4j/).

#### Development

Build:

```shell
make build
```

Run unit tests:

```shell
make unit-tests
```

Run integration tests:

```shell
make integration-tests
```

#### Releases

Newer artifacts are published via GitHub Actions CI workflow when a new release is created from `main` branch.

#### Who's using Ollama4j?

- `Datafaker`: a library to generate fake data
    - https://github.com/datafaker-net/datafaker-experimental/tree/main/ollama-api
- `Vaadin Web UI`: UI-Tester for Interactions with Ollama via ollama4j
    - https://github.com/TEAMPB/ollama4j-vaadin-ui
- `ollama-translator`: Minecraft 1.20.6 spigot plugin allows to easily break language barriers by using ollama on the
  server to translate all messages into a specfic target language.
    - https://github.com/liebki/ollama-translator

#### Traction

[![Star History Chart](https://api.star-history.com/svg?repos=ollama4j/ollama4j&type=Date)](https://star-history.com/#ollama4j/ollama4j&Date)

### Areas of improvement

- [x] Use Java-naming conventions for attributes in the request/response models instead of the
  snake-case conventions. (
  possibly with Jackson-mapper's `@JsonProperty`)
- [x] Fix deprecated HTTP client code
- [x] Setup logging
- [x] Use lombok
- [x] Update request body creation with Java objects
- [ ] Async APIs for images
- [ ] Support for function calling with models like Mistral
    - [x] generate in sync mode
    - [ ] generate in async mode
- [ ] Add custom headers to requests
- [x] Add additional params for `ask` APIs such as:
    - [x] `options`: additional model parameters for the Modelfile such as `temperature` -
      Supported [params](https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values).
    - [x] `system`: system prompt to (overrides what is defined in the Modelfile)
    - [x] `template`: the full prompt or prompt template (overrides what is defined in the Modelfile)
    - [x] `context`: the context parameter returned from a previous request, which can be used to keep a
      short
      conversational memory
    - [x] `stream`: Add support for streaming responses from the model
- [ ] Add test cases
- [ ] Handle exceptions better (maybe throw more appropriate exceptions)

### Get Involved

<div align="center">

<a href="">![Open Issues](https://img.shields.io/github/issues-raw/ollama4j/ollama4j)</a>
<a href="">![Closed Issues](https://img.shields.io/github/issues-closed-raw/ollama4j/ollama4j)</a>
<a href="">![Open PRs](https://img.shields.io/github/issues-pr-raw/ollama4j/ollama4j)</a>
<a href="">![Closed PRs](https://img.shields.io/github/issues-pr-closed-raw/ollama4j/ollama4j)</a>
<a href="">![Discussions](https://img.shields.io/github/discussions/ollama4j/ollama4j)</a>

</div>


[//]: # (![GitHub Issues or Pull Requests]&#40;https://img.shields.io/github/issues-raw/ollama4j/ollama4j&#41;)

[//]: # (![GitHub Issues or Pull Requests]&#40;https://img.shields.io/github/issues-closed-raw/ollama4j/ollama4j&#41;)

[//]: # (![GitHub Issues or Pull Requests]&#40;https://img.shields.io/github/issues-pr-raw/ollama4j/ollama4j&#41;)

[//]: # (![GitHub Issues or Pull Requests]&#40;https://img.shields.io/github/issues-pr-closed-raw/ollama4j/ollama4j&#41;)

[//]: # (![GitHub Discussions]&#40;https://img.shields.io/github/discussions/ollama4j/ollama4j&#41;)


Contributions are most welcome! Whether it's reporting a bug, proposing an enhancement, or helping
with code - any sort
of contribution is much appreciated.

### References

- [Ollama REST APIs](https://github.com/jmorganca/ollama/blob/main/docs/api.md)

### Credits

The nomenclature and the icon have been adopted from the incredible [Ollama](https://ollama.ai/)
project.

**Thanks to the amazing contributors**

<p align="center">
  <a href="https://github.com/ollama4j/ollama4j/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=ollama4j/ollama4j" />
  </a>
</p>

### Appreciate my work?

<p align="center">
  <a href="https://www.buymeacoffee.com/amithkoujalgi" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>
</p>
