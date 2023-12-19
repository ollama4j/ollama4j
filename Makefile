build:
	mvn -B clean install

ut:
	mvn clean test -Punit-tests

it:
	mvn clean verify -Pintegration-tests

#mvn -B clean install -Punit-tests -Dgpg.passphrase="${GPG_PASSPHRASE}" -e
#
#release:
#	mvn -B clean install -Punit-tests release:clean release:prepare release:perform -Dgpg.passphrase="${GPG_PASSPHRASE}" -e
#
#update-version:
#	mvn versions:set -DnewVersion=1.0.1
#
#deploy:
#	mvn clean deploy -Punit-tests -Dgpg.passphrase="$GPG_PASSPHRASE" -e