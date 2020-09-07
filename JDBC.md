[Back to README](README.md)

# JDBC

`Turntables` supports reading from and writing to JDBC Connections. This makes it possible to create test cases that `feed` test data into database tables, perform a function under test, and then `ingest` the data from database tables and run assertions on it:

Different database products (MySQL, Oracle, etc.) have different data type systems and JDBC implementations. Turntables offers extension libraries that support these databases.

## MySQL

To use Turntables with MySQL, you will need to have the following additional dependencies in your Maven project:

```xml
<dependency>
    <groupId>io.github.nblxa</groupId>
    <artifactId>turntables-mysql8</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.21</version>
    <scope>test</scope>
</dependency>
```

## Oracle

To use Turntables with Oracle and Java 8, you will need to have the following additional dependencies in your Maven project:

```xml
<dependency>
    <groupId>io.github.nblxa</groupId>
    <artifactId>turntables-oracle18-java8</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>19.7.0.0</version>
    <scope>test</scope>
</dependency>
```