## Contributing to Ollama4j

Thanks for your interest in contributing! This guide explains how to set up your environment, make changes, and submit pull requests.

### Code of Conduct

By participating, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

### Quick Start

Prerequisites:

- Java 11+
- Maven 3.8+
- Docker (required for integration tests)
- Make (for convenience targets)
- pre-commit (for Git hooks)

Setup:

```bash
# 1) Fork the repo and clone your fork
git clone https://github.com/<your-username>/ollama4j.git
cd ollama4j

# 2) Install and enable git hooks
pre-commit install --hook-type pre-commit --hook-type commit-msg

# 3) Prepare dev environment (installs husk deps/tools if needed)
make dev
```

Build and test:

```bash
# Build
make build

# Run unit tests
make unit-tests

# Run integration tests (requires Docker running)
make integration-tests
```

If you prefer raw Maven:

```bash
# Unit tests profile
mvn -P unit-tests clean test

# Integration tests profile (Docker required)
mvn -P integration-tests -DskipUnitTests=true clean verify
```

### Commit Style

We use Conventional Commits. Commit messages and PR titles should follow:

```
<type>(optional scope): <short summary>

[optional body]
[optional footer(s)]
```

Common types: `feat`, `fix`, `docs`, `refactor`, `test`, `build`, `chore`.

Commit message formatting is enforced via `commitizen` through `pre-commit` hooks.

### Pre-commit Hooks

Before pushing, run:

```bash
pre-commit run -a
```

Hooks will check for merge conflicts, large files, YAML/XML/JSON validity, line endings, and basic formatting. Fix reported issues before opening a PR.

### Coding Guidelines

- Target Java 11+; match existing style and formatting.
- Prefer clear, descriptive names over abbreviations.
- Add Javadoc for public APIs and non-obvious logic.
- Include meaningful tests for new features and bug fixes.
- Avoid introducing new dependencies without discussion.

### Tests

- Unit tests: place under `src/test/java/**/unittests/`.
- Integration tests: place under `src/test/java/**/integrationtests/` (uses Testcontainers; ensure Docker is running).

### Documentation

- Update `README.md`, Javadoc, and `docs/` when you change public APIs or user-facing behavior.
- Add example snippets where useful. Keep API references consistent with the website content when applicable.

### Pull Requests

Before opening a PR:

- Ensure `make build` and all tests pass locally.
- Run `pre-commit run -a` and fix any issues.
- Keep PRs focused and reasonably small. Link related issues (e.g., "Closes #123").
- Describe the change, rationale, and any trade-offs in the PR description.

Review process:

- Maintainers will review for correctness, scope, tests, and docs.
- You may be asked to iterate; please be responsive to comments.

### Security

If you discover a security issue, please do not open a public issue. Instead, email the maintainer at `koujalgi.amith@gmail.com` with details.

### License

By contributing, you agree that your contributions will be licensed under the project’s [MIT License](LICENSE).

### Questions and Discussion

Have questions or ideas? Open a GitHub Discussion or issue. We welcome feedback and proposals!


