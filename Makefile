ut:
	mvn clean test -Punit-tests

it:
	mvn clean verify -Pintegration-tests

build:
	mvn -B clean test install -Punit-tests -Dgpg.passphrase="${GPG_PASSPHRASE}" -e

release:
	mvn clean -Punit-tests release:clean release:prepare release:perform -B

update-version:
	mvn versions:set -DnewVersion=1.0.1

deploy:
	mvn clean deploy -Punit-tests -Dgpg.passphrase="$GPG_PASSPHRASE" -e