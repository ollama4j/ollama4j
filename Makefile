dev:
	@echo "Setting up dev environment..."
	@command -v pre-commit >/dev/null 2>&1 || { echo "Error: pre-commit is not installed. Please install it first."; exit 1; }
	@command -v docker >/dev/null 2>&1 || { echo "Error: docker is not installed. Please install it first."; exit 1; }
	pre-commit install
	pre-commit autoupdate
	pre-commit install --install-hooks

build:
	mvn -B clean install -Dgpg.skip=true

full-build:
	mvn -B clean install

unit-tests:
	mvn clean test -Punit-tests

integration-tests:
	export USE_EXTERNAL_OLLAMA_HOST=false && mvn clean verify -Pintegration-tests

integration-tests-remote:
	export USE_EXTERNAL_OLLAMA_HOST=true && export OLLAMA_HOST=http://192.168.29.223:11434 && mvn clean verify -Pintegration-tests -Dgpg.skip=true

doxygen:
	doxygen Doxyfile

list-releases:
	curl 'https://central.sonatype.com/api/internal/browse/component/versions?sortField=normalizedVersion&sortDirection=desc&page=0&size=20&filter=namespace%3Aio.github.ollama4j%2Cname%3Aollama4j' \
      --compressed \
      --silent | jq -r '.components[].version'

docs-build:
	npm i --prefix docs && npm run build --prefix docs

docs-serve:
	npm i --prefix docs && npm run start --prefix docs

start-cpu:
	docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama

start-gpu:
	docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama