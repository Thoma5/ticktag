#!/bin/sh

# This script pulls the api spec from the local server and generates the Angular2 bindings to app/api
# Make sure the server is running on localhost:8080

java -jar swagger-codegen-cli.jar generate -i "http://localhost:8080/v2/api-docs" -l "typescript-angular2" -o app/api
