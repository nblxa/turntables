[Back to README](README.md)

# JDBC

`Turntables` supports reading from and writing to JDBC Connections.
This makes it possible to create test cases that `feed` test data into database
tables, perform a function under test, and then `ingest` the data from database
tables and run assertions on it:

Different database products (MySQL, Oracle, etc.) have different data type
systems and JDBC implementations. Turntables offers extension libraries that
support these databases.

## MySQL

To use Turntables with MySQL, you will need to have the following additional
dependency in your Maven project:

```xml
<dependency>
  <groupId>io.github.nblxa</groupId>
  <artifactId>turntables-mysql</artifactId>
  <version>0.2.0</version>
  <scope>test</scope>
</dependency>
```

## Oracle

To use Turntables with Oracle, you will need to have the following
additional dependency in your Maven project:

```xml
<dependency>
  <groupId>io.github.nblxa</groupId>
  <artifactId>turntables-ojdbc</artifactId>
  <version>0.2.0</version>
  <scope>test</scope>
</dependency>
```

## Other databases

Need support for your database? Feel free to write your own Turntables extension
module (use [turntables-mysql]() or [turntables-ojdbc]() as examples)
or raise an issue in this repository.

Modules use Java SPI to provide implementations of
[IoProtocolProvider](turntables-core/src/main/java/io/github/nblxa/turntables/io/IoProtocolProvider.java).
