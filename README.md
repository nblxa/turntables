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

## Build

### Test with Oracle

Unfortunately, Oracle tests are not containerized.

To run Oracle tests, you'll need an Oracle Cloud Autonomous Database (a
[free one](https://docs.cloud.oracle.com/en-us/iaas/Content/FreeTier/resourceref.htm)
will also work). Use [oracle-test-schemas](https://github.com/nblxa/oracle-test-schemas)
to set up the temporary test schema provisioning.

[Download the Oracle Wallet](https://docs.cloud.oracle.com/en-us/iaas/Content/Database/Tasks/adbconnecting.htm)
for connecting to your Oracle Cloud database. Put the contents of the wallet into the directory
[turntables-test-oracle/wallet.local](turntables-test-oracle/wallet.local) like this

```
turntables-test-oracle/wallet.local
|-- README
|-- cwallet.sso
|-- ewallet.p12
|-- keystore.jks
|-- ojdbc.properties
|-- sqlnet.ora
|-- tnsnames.ora
`-- truststore.jks
```

When building with Maven, provide additional options:
* `-DttOraHost`: hostname of the Oracle Autonomous Database.
* `-DttOraUrl`: the JDBC URL of the Oracle Autonomous Database for connecting using the Wallet.
  Example: `jdbc:oracle:thin:@mytestdb_tp?TNS_ADMIN=C:/turntables/turntables-test-oracle/wallet.local`
* `-DttClientId`: OAuth Client ID for the
  [test schema provisioning REST API](https://github.com/nblxa/oracle-test-schemas).
* `-DttClientSecret`: OAuth Client Secret.
* `-Doracle.jdbc.fanEnabled=false` to suppress a warning from Oracle.

### Oracle Wallet used in Travis

The Oracle Wallet used in the Travis builds has the following lifetime:
```
The SSL certificates provided in this wallet will expire on 2023-03-19 21:43:22.0 UTC.
```
