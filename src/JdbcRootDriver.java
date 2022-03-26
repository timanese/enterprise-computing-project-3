
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

public class JdbcRootDriver {
  // using a properties file
  private Properties properties = new Properties();
  private FileInputStream filein = null;
  private MysqlDataSource dataSource = null;
  private Connection connection = null;

  // Constructor, might not need it
  public JdbcRootDriver() throws SQLException {
    // read a properties file
    try {
      filein = new FileInputStream("db.properties");
      properties.load(filein);
      dataSource = new MysqlDataSource();
      dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
      dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
      dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));
    } catch (IOException e) {
      // e.printStackTrace();
      System.out.println("Failed to Set dataSource with root properties");
    }
  }

  public void setConnection() throws SQLException {
    System.out.println("Output from SimpleJDBCProperties:   Using a root properties file to hold connection details.");
    java.util.Date date = new java.util.Date();
    System.out.println(date);
    System.out.println();
    // establish a connection to the dataSource - the database
    connection = dataSource.getConnection();
    System.out.println("Database connected");
    DatabaseMetaData dbMetaData = connection.getMetaData();
    System.out.println("JDBC Driver name " + dbMetaData.getDriverName());
    System.out.println("JDBC Driver version " + dbMetaData.getDriverVersion());
    System.out.println("Driver Major version "
        + dbMetaData.getDriverMajorVersion());
    System.out.println("Driver Minor version "
        + dbMetaData.getDriverMinorVersion());
    // Close the connection
    // connection.close();
  }

  public String getPassword() {
    return dataSource.getPassword();

  }

  public String getUsername() {
    return dataSource.getUser();
  }

  public String getURL() {
    return dataSource.getUrl();
  }

  public Connection getConnection() {
    return connection;
  }

}
