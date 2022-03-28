
/*
Name: Tim Yang 
Course: CNT 4714 Spring 2022
Assignment Title: Project 3 - Two-Tier Client_Server Application Development with MYSQL and JDBC
Date: March 27, 2022
Class: Enterprise Computing 
*/

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.cj.jdbc.MysqlDataSource;

public class jdbcClientDriver {
  // using a properties file
  private Properties properties = new Properties();
  private FileInputStream filein = null;
  private MysqlDataSource dataSource = null;
  private Connection connection = null;

  public jdbcClientDriver() throws SQLException {
    // read a properties file
    try {
      filein = new FileInputStream("clientdb.properties");
      properties.load(filein);
      dataSource = new MysqlDataSource();
      dataSource.setURL(properties.getProperty("MYSQL_CLIENT_DB_URL"));
      dataSource.setUser(properties.getProperty("MYSQL_CLIENT_DB_USERNAME"));
      dataSource.setPassword(properties.getProperty("MYSQL_CLIENT_DB_PASSWORD"));
    } catch (IOException e) {
      // e.printStackTrace();
      System.out.println("Failed to set datasource with client properties");
    }

  }

  public void setConnection() throws SQLException {
    System.out
        .println("Output from SimpleJDBCProperties:   Using a client properties file to hold connection details.");
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

  public String getUsername() {
    return dataSource.getUser();
  }

  public String getPassword() {
    return dataSource.getPassword();
  }

  public String getURL() {
    return dataSource.getUrl();
  }

  public Connection getConnection() {
    return connection;
    // might have to put check case here to see if a connection is established
    // already
  }
}
