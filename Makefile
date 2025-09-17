dev:
	@echo "Setting up dev environment..."
	@command -v pre-commit >/dev/null 2>&1 || { echo "Error: pre-commit is not installed. Please install it first."; exit 1; }
	@command -v docker >/dev/null 2>&1 || { echo "Error: docker is not installed. Please install it first."; exit 1; }
	@pre-commit install
	@pre-commit autoupdate
	@pre-commit install --install-hooks

check-formatting:
	@echo "\033[0;34mChecking code formatting...\033[0m"
	@mvn spotless:check

apply-formatting:
	@echo "\033[0;32mApplying code formatting...\033[0m"
	@mvn spotless:apply
	# pre-commit run --all-files

build: apply-formatting
	@echo "\033[0;34mBuilding project (GPG skipped)...\033[0m"
	@mvn -B clean install -Dgpg.skip=true

full-build: apply-formatting
	@echo "\033[0;34mPerforming full build...\033[0m"
	@mvn -B clean install

unit-tests:
	@echo "\033[0;34mRunning unit tests...\033[0m"
	@mvn clean test -Punit-tests

integration-tests:
	@echo "\033[0;34mRunning integration tests (local)...\033[0m"
	@export USE_EXTERNAL_OLLAMA_HOST=false && mvn clean verify -Pintegration-tests

integration-tests-remote:
	@echo "\033[0;34mRunning integration tests (remote)...\033[0m"
	@export USE_EXTERNAL_OLLAMA_HOST=true && export OLLAMA_HOST=http://192.168.29.223:11434 && mvn clean verify -Pintegration-tests -Dgpg.skip=true

doxygen:
	@echo "\033[0;34mGenerating documentation with Doxygen...\033[0m"
	@doxygen Doxyfile

list-releases:
	@echo "\033[0;34mListing latest releases...\033[0m"
	@curl 'https://central.sonatype.com/api/internal/browse/component/versions?sortField=normalizedVersion&sortDirection=desc&page=0&size=20&filter=namespace%3Aio.github.ollama4j%2Cname%3Aollama4j' \
      --compressed \
      --silent | jq -r '.components[].version'

docs-build:
	@echo "\033[0;34mBuilding documentation site...\033[0m"
	@cd ./docs && npm install --prefix && npm run build

docs-serve:
	@echo "\033[0;34mServing documentation site...\033[0m"
	@cd ./docs && npm install && npm run start

start-cpu:
	@echo "\033[0;34mStarting Ollama (CPU mode)...\033[0m"
	@docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama

start-gpu:
	@echo "\033[0;34mStarting Ollama (GPU mode)...\033[0m"
	@docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama