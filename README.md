# FluentTab

`FluentTab` enables developers to write test assertions for data in tables
using a simple fluent interface:

```java
ResultSet rs = conn.executeQuery("select * from my_table");

Tab actual = FluentTab.from(rs);

Tab expected = FluentTab.tab()
    .row(1, 2)
    .row(3, 4);

FluentTab.assertThat(actual)
    .matches(expected);
```

## Usage

FluentTab extends [AssertJ](https://github.com/joel-costigliola/assertj-core)
and you'll need both dependencies.

Note, the dependency is not yet made available on Maven Central. I am working
on it. Meanwhile, feel free to build the library from source.

```xml
<dependency>
    <groupId>io.github.nblxa</groupId>
    <artifactId>fluenttab</artifactId>
    <version>0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version></version>
    <scope>test</scope>
</dependency>
```

