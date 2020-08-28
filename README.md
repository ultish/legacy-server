# legacy-server
This mimics a server that is using Hibernate 4 and an older version of Spring (less important). It will be the source of the RDBMS data to be hopefully streamed into Kafka and onto [graphql-server](https://github.com/ultish/graphql-server).

Will also test out Hibernate Envers for Auditing/Historical Record keeping to see if it can be used to rollback Entity versions and restore them. 

This is a basic spring-boot (1.4) application. You can load the [postgres DB schema](sql/schema.sql) into a standard Postgres install or the Postgres Docker container. It's expecting a database called *legacy* with default user. 
