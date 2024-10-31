build:
	mvn -B clean install

unit-tests:
	mvn clean test -Punit-tests

integration-tests:
	mvn clean verify -Pintegration-tests

doxygen:
	doxygen Doxyfile

list-releases:
	curl 'https://central.sonatype.com/api/internal/browse/component/versions?sortField=normalizedVersion&sortDirection=desc&page=0&size=20&filter=namespace%3Aio.github.ollama4j%2Cname%3Aollama4j' \
      --compressed \
      --silent | jq -r '.components[].version'

build-docs:
	npm i --prefix docs && npm run build --prefix docs

start-docs:
	npm i --prefix docs && npm run start --prefix docs

start-cpu:
	docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama

start-gpu:
	docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama