#!/usr/bin/env bash

aws lambda update-function-code \
  --function-name KotlinLambda \
  --zip-file fileb://build/distributions/kotlinlambda-1.0-SNAPSHOT.zip \
  --publish
