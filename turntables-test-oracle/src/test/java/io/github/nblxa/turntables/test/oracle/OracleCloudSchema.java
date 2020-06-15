package io.github.nblxa.turntables.test.oracle;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.nblxa.turntables.junit.AbstractTestRule;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

public class OracleCloudSchema extends AbstractTestRule {

  static class CreateSchemaPayload {
    public final int timeout;
    public final String password;

    public CreateSchemaPayload(int timeout, String password) {
      this.timeout = timeout;
      this.password = password;
    }
  }

  static class Oauth {
    public final String accessToken;
    public final String tokenType;
    public final int expiresIn;

    @JsonCreator
    public Oauth(@JsonProperty("access_token") String accessToken,
                 @JsonProperty("token_type") String tokenType,
                 @JsonProperty("expires_in") int expiresIn) {
      this.accessToken = accessToken;
      this.tokenType = tokenType;
      this.expiresIn = expiresIn;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class SchemaDetail {
    public final int schemaId;
    public final String schemaName;
    public final LocalDateTime dropTs;

    @JsonCreator
    public SchemaDetail(@JsonProperty("schema_id") int schemaId,
                        @JsonProperty("schema_name") String schemaName,
                        @JsonProperty("drop_ts") String dropTs) {
      this.schemaId = schemaId;
      this.schemaName = schemaName;
      this.dropTs = LocalDateTime.parse(dropTs, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
  }

  private final ClientConfig clientConfig = new ClientConfig();
  private final Client client = ClientBuilder.newClient(clientConfig);
  private final WebTarget target = client.target(getBaseUri());
  private String user;
  private String password;
  private URI schemaUri;
  private Oauth oauth;

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  @Override
  protected void setUp() {
    setUpOauth();
    String randomPassword = RandomPassword.get();
    Response response = target.path("schema/")
        .request()
        .accept(MediaType.APPLICATION_JSON_TYPE)
        .header("Authorization", "Bearer " + oauth.accessToken)
        .post(Entity.json(new CreateSchemaPayload(2, randomPassword)));
    assertThat(response.getStatusInfo())
        .isEqualTo(Response.Status.CREATED);
    schemaUri = response.getLocation();
    SchemaDetail schemaDetail = client.target(schemaUri)
        .request()
        .header("Authorization", "Bearer " + oauth.accessToken)
        .accept(MediaType.APPLICATION_JSON_TYPE)
        .get(SchemaDetail.class);
    user = schemaDetail.schemaName;
    password = randomPassword;
  }

  @Override
  protected void tearDown() {
    if (schemaUri != null) {
      Response response = client.target(schemaUri)
          .request()
          .header("Authorization", "Bearer " + oauth.accessToken)
          .delete();
      assertThat(response.getStatusInfo())
          .isEqualTo(Response.Status.OK);
      schemaUri = null;
    }
  }

  Connection createConnection() {
    Properties info = new Properties();
    info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, user);
    info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, password);
    info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");
    try {
      OracleDataSource ods = new OracleDataSource();
      ods.setURL(getJdbcUrl());
      ods.setConnectionProperties(info);
      return ods.getConnection();
    } catch (SQLException se) {
      throw new UnsupportedOperationException(se);
    }
  }

  static String getJdbcUrl() {
    return Objects.requireNonNull(System.getProperty("ttOraUrl"),
        "Please provide the option -DttOraUrl with the JDBC URL of the Oracle database.");
  }

  private void setUpOauth() {
    String basicAuth = "Basic " + Base64.getEncoder()
        .encodeToString((getClientId() + ":" + getClientSecret()).getBytes());
    Response response = target.path("oauth").path("token")
        .request()
        .header("Authorization", basicAuth)
        .post(Entity.form(new Form().param("grant_type", "client_credentials")));
    assertThat(response.getStatusInfo())
        .isEqualTo(Response.Status.OK);
    oauth = response.readEntity(Oauth.class);
  }

  private static URI getBaseUri() {
    String host = Objects.requireNonNull(System.getProperty("ttOraHost"),
        "Please provide the option -DttOraHost with the Oracle database hostname.");
    return UriBuilder.fromUri("https://" + host + "/ords/test/")
        .build();
  }

  private static String getClientId() {
    return Objects.requireNonNull(System.getProperty("ttClientId"),
        "Please provide the option -DttClientId with the OAuth Client Id.");
  }

  private static String getClientSecret() {
    return Objects.requireNonNull(System.getProperty("ttClientSecret"),
        "Please provide the option -DttClientSecret with the OAuth Client Secret.");
  }
}
