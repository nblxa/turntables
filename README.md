# Turntables

[![CircleCI](https://circleci.com/gh/nblxa/turntables.svg?style=shield)](https://circleci.com/gh/nblxa/turntables)
[![Coverage Status](https://coveralls.io/repos/github/nblxa/turntables/badge.svg?branch=master)](https://coveralls.io/github/nblxa/turntables?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.nblxa:turntables&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.github.nblxa:turntables)

`Turntables` enables developers to write test assertions for data in tables
using a simple fluent interface:

```java
ResultSet rs = conn.executeQuery("select id, name from employees");

Tab actual = Turntables.from(rs);

Turntables.assertThat(actual)
  .rowMode(Settings.RowMode.MATCH_IN_ANY_ORDER)
  .matches()
  .row(2, "Bob")
  .row(1, "Alice")
  .asExpected();
```

`Turntables` integrates with `JUnit` to set up test data in tables:

```java
@TestTable(name = "employees", cleanUpAction = CleanUpAction.DROP)
public Tab testTab = Turntables.tab()
  .col("id", Typ.INTEGER).col("name", Typ.STRING)
  .row(1, "Alice")
  .row(2, "Bob");
```

For a complete example with MySQL, see
[ITMySql.java](turntables-mysql8/src/test/java/io/github/nblxa/turntables/test/mysql/ITMySql.java).

For Oracle, see:
[ITOracle.java](turntables-oracle18-java8/src/test/java/io/github/nblxa/turntables/test/oracle/ITOracle.java).

Turntables's main power and difference from
[AssertJ-DB](https://github.com/assertj/assertj-db) is the ability to match
whole table contents at once using specific rules (e.g. match rows by key or
match columns by name) and to present to the developer only the difference that
matters for the assertion.

## Maven

Turntables extends [AssertJ](https://github.com/joel-costigliola/assertj-core)
and you'll need both dependencies.

```xml
<dependency>
    <groupId>io.github.nblxa</groupId>
    <artifactId>turntables-core</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.17.2</version>
    <scope>test</scope>
</dependency>
```

If you use Turntables with a JDBC database, see [JDBC](JDBC.md) for additional
dependencies.

## Build

### Test with Oracle

The Oracle test is containerized using the image `quillbuilduser/oracle-18-xe`.
* Docker Hub: https://hub.docker.com/r/quillbuilduser/oracle-18-xe
* Source: https://github.com/deusaquilus/docker-oracle-xe

Image size is **13GB** at the time of writing this, so it's advisable to
download it beforehand.
