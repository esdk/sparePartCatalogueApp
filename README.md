# Example abas Essentials App
This is the source code of the sparePartsCatalogueApp and example abas Essentials App build on the abas Essentials SDK.

## General setup
Add a gradle.properties file to your $GRADLE_USER_HOME.

```
#If you use a proxy add it here
systemProp.http.proxyHost=<your_proxy>
systemProp.http.proxyPort=<your_proxy_port>
systemProp.https.proxyHost=<your_proxy>
systemProp.https.proxyPort=<your_proxy_port>

esdkSnapshotURL=https://registry.abas.sh/repository/abas.esdk.snapshots/
esdkReleaseURL=https://registry.abas.sh/repository/abas.esdk.releases/
nexusUser=<extranet username>
nexusPassword=<extranet password>
```

To create the common development setup for eclipse run
```shell
./gradlew eclipse
```

## Installation
To install the project make sure you are running the docker-compose.yml file or else change the gradle.properties file accordingly to use another erp client.

To use the project's docker-compose.yml file, in the project's root directory run:
```shell
docker login intra.registry.abas.sh -u <extranet user> -p <extranet password>
docker-compose up
```

Once it's up, initialize the gradle.properties with the appropriate values by running:
```shell
./initGradleProperties.sh
```

Now, you need to load all the $HOMEDIR/java/lib dependencies into the local repository:
```shell
./gradlew publishHomeDirJars
```

Now you can install the project as follows:
```shell
./gradlew fullInstall
```
## Development
If you want to make changes to the project before installing you still need to run the docker-compose.yml file.

Then run
```shell
./gradlew publishHomeDirJars
```

You also need to run
```shell
./gradlew publishClientDirJars
./gradlew eclipse
```
to upload the $MANDANTDIR/java/lib dependencies to the local repository and set eclipse up to work with the uploaded dependencies.

After that the code should compile both with gradle and in eclipse, and you are set up to work on the code or resource files as you want.
