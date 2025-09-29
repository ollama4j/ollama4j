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

build: apply-formatting
	@echo "\033[0;34mBuilding project (GPG skipped)...\033[0m"
	@mvn -B clean install -Dgpg.skip=true -Dmaven.javadoc.skip=true

full-build: apply-formatting
	@echo "\033[0;34mPerforming full build...\033[0m"
	@mvn -B clean install

unit-tests: apply-formatting
	@echo "\033[0;34mRunning unit tests...\033[0m"
	@mvn clean test -Punit-tests

integration-tests-all: apply-formatting
	@echo "\033[0;34mRunning integration tests (local - all)...\033[0m"
	@export USE_EXTERNAL_OLLAMA_HOST=false && mvn clean verify -Pintegration-tests

integration-tests-basic: apply-formatting
	@echo "\033[0;34mRunning integration tests (local - basic)...\033[0m"
	@export USE_EXTERNAL_OLLAMA_HOST=false && mvn clean verify -Pintegration-tests -Dit.test=WithAuth

integration-tests-remote: apply-formatting
	@echo "\033[0;34mRunning integration tests (remote - all)...\033[0m"
	@export USE_EXTERNAL_OLLAMA_HOST=true && export OLLAMA_HOST=http://192.168.29.229:11434 && mvn clean verify -Pintegration-tests -Dgpg.skip=true

doxygen:
	@echo "\033[0;34mGenerating documentation with Doxygen...\033[0m"
	@doxygen Doxyfile

javadoc:
	@echo "\033[0;34mGenerating Javadocs into '$(javadocfolder)'...\033[0m"
	@mvn clean javadoc:javadoc
	@if [ -f "target/reports/apidocs/index.html" ]; then \
		echo "\033[0;32mJavadocs generated in target/reports/apidocs/index.html\033[0m"; \
	else \
		echo "\033[0;31mFailed to generate Javadocs in target/reports/apidocs\033[0m"; \
		exit 1; \
	fi

list-releases:
	@echo "\033[0;34mListing latest releases...\033[0m"
	@curl 'https://central.sonatype.com/api/internal/browse/component/versions?sortField=normalizedVersion&sortDirection=desc&page=0&size=20&filter=namespace%3Aio.github.ollama4j%2Cname%3Aollama4j' \
      --compressed \
      --silent | jq -r '.components[].version'

docs-build:
	@echo "\033[0;34mBuilding documentation site...\033[0m"
	@cd ./docs && npm ci --no-audit --fund=false && npm run build

docs-serve:
	@echo "\033[0;34mServing documentation site...\033[0m"
	@cd ./docs && npm install && npm run start

start-cpu:
	@echo "\033[0;34mStarting Ollama (CPU mode)...\033[0m"
	@docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama

start-gpu:
	@echo "\033[0;34mStarting Ollama (GPU mode)...\033[0m"
	@docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama