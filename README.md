# legacy-server
This mimics a server that is using Hibernate 4 and an older version of Spring (less important). It will be the source of the RDBMS data to be hopefully streamed into Kafka and onto [graphql-server](https://github.com/ultish/graphql-server).

Will also test out Hibernate Envers for Auditing/Historical Record keeping to see if it can be used to rollback Entity versions and restore them. 
