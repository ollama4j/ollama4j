build:
	mvn -B clean install

ut:
	mvn clean test -Punit-tests

it:
	mvn clean verify -Pintegration-tests