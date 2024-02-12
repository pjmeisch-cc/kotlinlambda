#!/usr/bin/env bash

aws lambda create-function \
  --function-name KotlinLambda \
  --runtime "java21" \
  --environment "Variables={JAVA_TOOL_OPTIONS=-XX:+TieredCompilation -XX:TieredStopAtLevel=1}" \
  --zip-file fileb://build/distributions/kotlinlambda-1.0-SNAPSHOT.zip \
  --handler de.codecentric.pjmeisch.kotlinlambda.KotlinLambdaEventFunction::handleRequest \
  --role "arn:aws:iam::344681872827:role/kotlinlambda-role" \
  --memory-size 2048 \
  --snap-start ApplyOn=PublishedVersions \
  --publish
