# session-event-processor
Processes the JSON events by browser,device and OS


* Starts a [Apache Spark](https://spark.apache.org/) ETL job to perform aggregations on the data using CLI.
* For now user input can be accepted via CLI interface `LocalMain` later same APIS can be extended add REST interface

Currently, supported versions:
* Spark 2.4.5 for Hadoop 2.7 with OpenJDK 8, Scala 2.11

### Pre-Requisites
* docker
* docker-compose
* Java 8
* Scala 2.x

#### Project Details
* src/main/scala - Contains Scala source code
* src/test/scala - Contains Scala test source code 
* src/test/resources - Contains sample test data
* build.sbt - Used for compiling and building the project, adding dependencies using sbt tool
* Dockerfile - Create docker image from the project build and extend Spark App template
* docker-compose.yml - Starts container the Spark master and Spark worker  

#### Application entry points
* LocalMain - ClI which loads the data and accepts the User input to perform aggregation.
* ClusterMain - Entry point to run on the Spark cluster w/o User input. Example for production deployment ready.

#### Steps to run Project on local (LocalMain)
Note : You will need an IDE which can launch Spark job e.g. Intellij IDEA
1. Import the project 
2. `sbt assembly` to build and run unit test cases
3. Run the `LocalMain` main class with VM argument `-Dspark.master=local[1]`
```shell script
java -Dspark.master=local[1] -cp target/scala-2.12/session-event-processor-assembly-1.0.jar entry.LocalMain
```
and follow the instructions

### Start Spark cluster
```shell script
cd session-event-processor
docker-compose up
```

#### Building and Deploying Docker image
1. Build the docker image ``` docker build --rm=true -t localrepo/session-event-processor:latest . ```
2. Deploy the docker image 
```shell script
docker run --name session-event-processor --network stream-net -e ENABLE_INIT_DAEMON=false --link spark-master:spark-master -d localrepo/session-event-processor:latest
``` 
3. `ClusterMain` expects the file to be on local disk `/spark/conf/Teste/` which can be changes to `S3` or `HDFS` location
4. This ETL will perform unique session count by Browser , device and OS and output the JSON to logs
and exit. It's a non interactive Spark job.