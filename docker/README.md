# docker-dev-environment
TickTag development environment.

## Initial Setup:
Install docker and docker-compose and its dependencies. You either have to add your user to the docker group or you will have to run the docker commands with sudo. From the project root:
```
cd ./docker/
docker-compose up
```
Don't forget to add the following line to your /etc/hosts file:
```
127.0.0.1        dev.ticktag
```
### Take it down:
 From the project root:
```
cd ./docker/
docker-compose down
sudo rm -r ./volumes
```

## Adresses


Mailhog UI
```
dev.ticktag:8025/mail
```
SMTP
```
dev.ticktag:2525
```
OpenLDAP
```
dev.ticktag:389
```
Postgresql
```
dev.ticktag:5432
```
## Known issues:
+ None?
