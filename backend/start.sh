#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

RES_DIR=src/main/resources/
PROP_FILE=$RES_DIR/dev-application.properties
DROP_SQL=$RES_DIR/drop.sql
SCHEMA_SQL=$RES_DIR/schema.sql
SAMPLES_SQL=$RES_DIR/samples.sql

DB_URL=$(grep "db.url" $PROP_FILE | awk -F  "=" '{print $2}' | sed 's/jdbc:postgresql:\/\///')
DB_USER=$(grep "db.user" $PROP_FILE | awk -F  "=" '{print $2}')
DB_PASSWORD=$(grep "db.password" $PROP_FILE | awk -F  "=" '{print $2}')

CONN="postgres://"$DB_USER":"$DB_PASSWORD"@"$DB_URL

echo $CONN

RECREATE_DB=false
while test $# != 0
do
    case "$1" in
    -r) RECREATE_DB=true ;;
    --) shift; break;;
    esac
    shift
done

if $RECREATE_DB; then
    psql $CONN -f $DROP_SQL
    psql $CONN -f $SCHEMA_SQL
    psql $CONN -f $SAMPLES_SQL
fi

./mvnw compile
./mvnw -DTICKTAG_CONFIG=$PROP_FILE exec:java -Dexec.mainClass=io.ticktag.TicktagApplicationKt
