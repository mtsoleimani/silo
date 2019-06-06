CREATE KEYSPACE silo WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};



CREATE TABLE silo.silo (
  topic TEXT ,
  ts timeuuid,
  message TEXT,
  created bigint,
  PRIMARY KEY (topic, ts)
);
