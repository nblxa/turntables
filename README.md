# Turntables

`Turntables` enables developers to write test assertions for data in tables
using a simple fluent interface:

```java
ResultSet rs = conn.executeQuery("select id, name from employees");

Tab actual = Turntables.from(rs);

Tab expected = Turntables.tab()
  .row(2, "Bob")
  .row(1, "Alice");

Turntables.assertThat(actual)
  .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
  .matches(expected);
```

`Turntables` integrates with `JUnit` to set up test data in tables:

```java
private TestTable testTab = testDataSource.table("employees")
  .col("id", Typ.INTEGER).col("name", Typ.STRING)
  .row(1, "Alice")
  .row(2, "Bob")
  .cleanupAfterTest(CleanUpAction.DROP);
```

For a complete example with MySQL, see
[ITMySqlTestData.java](turntables-test-mysql/src/test/java/io/github/nblxa/turntables/test/mysql/ITMySqlTestData.java)
(Docker required).

## Maven

Turntables extends [AssertJ](https://github.com/joel-costigliola/assertj-core)
and you'll need both dependencies.

Note, the dependency is not yet made available on Maven Central. I am working
on it. Meanwhile, feel free to build the library from source.

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
