# Next Generation SFCC Publication

This repository contains a FirstSpirit module that implements a new approach to publish content from FirstSpirit to Salesforce Commerce Cloud.
It was demonstrated during e-Spirit's Devcon in November 2020.

## Setup
1. Add the parameters `artifactory_hosting_username` and `artifactory_password` to your `~/.gradle/gradle.properties` file
2. Configure your Salesforce Commerce Cloud instance in `src/main/resources/de/espirit/sfcc/publication/configuration.properties`
3. Build the module using `./gradlew build`
4. Install the module on your FirstSpirit server
5. Add a new activity to your release workflow that calls the executable `de.espirit.sfcc.publication.PublicationExecutable`
6. Adapt your section templates to output their content directly and not add it to the xml collectors