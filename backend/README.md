# Ticktag Backend

To override the configuration create a partial config file at the location given below and launch the instance with the corresponding VM options.
In IntelliJ the VM options can be set by going to "Edit Configurations" (dropdown left of the "Run" Button) and editing the "VM Options" field.
In Maven they can simply be added as a command line argument.


## Run
### Config
`src/main/resources/dev-application.properties`
```
db.url=jdbc:postgresql://localhost:5432/ticktag
db.user=ticktag
db.password=ticktag
```

### VM options
`-DTICKTAG_CONFIG=src/main/resources/dev-application.properties`

## Test
### Config
`src/test/resources/dev-application.properties`
```
db.url=jdbc:postgresql://localhost:5432/ticktag-test
db.user=ticktag
db.password=ticktag
```

### VM options
`-DTICKTAG_CONFIG=src/test/resources/dev-application.properties`