# Server Setup

## Integration Server Setup

```shell
git clone https://github.com/jeffwmair/searchengine.git && cd searchengine/server-setup && ./integrationserver-setup.sh
```

## Simple Build/Test Server Setup

```shell
git clone https://github.com/jeffwmair/searchengine.git && cd searchengine/server-setup && ./simple-setup.sh
```
With the simple-setup script run, cd back to the searchengine root and run the following one-liner to build/test/install and run the web application:

```shell
cd searchservice/ && mvn install && cd ../indexer/ && mvn install && cd ../webapp/ && mvn tomcat7:run
```
