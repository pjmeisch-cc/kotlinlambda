# Kotlin Lambda test project

This is a minimal project that is a lambda function in AWS which is triggered by an event to an S3 bucket. When invoked, the data from the bucket is loaded (no check if it was added, updatet or deleted). If the content-type of the data is _application/json_ the data is sent to the Kafka cluster specified in the lambda environment.

## Used libraries:
* http4k for the S3 event handling and S3 access
* Kafka client libraries to send to Kafka

## manual setup in AWS

There is no CDK or other setup code in this demo

* the bucket was created in the AWS console
* the lambda execution role was manually created, the arn is needed in the install/update script
* the access policy for the lambda role to read the bucket was added
* after deployment of the lambda, having the s3 bucket trigger the lambda was manually set up
* environment entries for Kafka were added after deployment

## build and deploy

to build the distribution library

```
gradle buildLambdaZip
```
To deploy, it is necessary to log into the AWS account on the command line. After that, the lambda is deployed or updated with

```asciidoc
./create-lambda.sh

# or
./update-lambda.sh
```
