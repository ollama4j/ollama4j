ut:
	mvn clean test -Punit-tests

it:
	mvn clean verify -Pintegration-tests

build:
	mvn clean test install -Punit-tests