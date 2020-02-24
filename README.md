# Turntables

[![Build Status](https://travis-ci.com/nblxa/turntables.svg?branch=master)](https://travis-ci.com/nblxa/turntables)
[![Coverage Status](https://coveralls.io/repos/github/nblxa/turntables/badge.svg?branch=master)](https://coveralls.io/github/nblxa/turntables?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=turntables&metric=alert_status)](https://sonarcloud.io/dashboard?id=turntables)

`Turntables` enables developers to write test assertions for data in tables
using a simple fluent interface:

```java
ResultSet rs = conn.executeQuery("select id, name from employees");

Tab actual = Turntables.from(rs);

Turntables.assertThat(actual)
  .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
  .matches()
  .row(2, "Bob")
  .row(1, "Alice")
  .asExpected();
```

`Turntables` integrates with `JUnit` to set up test data in tables:

```java
private TestTable testTab = testDataSource.table("employees")
  .col("id", Typ.INTEGER).col("name", Typ.STRING)
  .row(1, "Alice")
  .row(2, "Bob")
  .cleanUpAfterTest(CleanUpAction.DROP);
```

For a complete example with MySQL, see
[ITMySqlTestData.java](turntables-test-mysql/src/test/java/io/github/nblxa/turntables/test/mysql/ITMySqlTestData.java)
(Docker required).

## Maven

Turntables extends [AssertJ](https://github.com/joel-costigliola/assertj-core)
and you'll need both dependencies.

```xml
<dependency>
    <groupId>io.github.nblxa</groupId>
    <artifactId>turntables</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version></version>
    <scope>test</scope>
</dependency>
```
