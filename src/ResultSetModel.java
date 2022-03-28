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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import com.mysql.cj.jdbc.MysqlDataSource;

public class ResultSetModel extends AbstractTableModel {

  private Statement statement;
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int numberOfRows;
  private Connection connection;

  // connection for operation log database
  private Properties propertiesOL = new Properties();
  private FileInputStream fileinOL = null;
  private MysqlDataSource dataSourceOL = null;
  private Connection connectionOL = null;
  private Statement statementOL;

  public ResultSetModel(Connection connection_, String query) throws SQLException {
    this.connection = connection_;
    // create Statement to query database
    this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);

    setDefaultQuery(query);
    // read a properties file
    try {
      fileinOL = new FileInputStream("db.properties");
      propertiesOL.load(fileinOL);
      dataSourceOL = new MysqlDataSource();
      dataSourceOL.setURL(
          "jdbc:mysql://localhost:3306/operationslog?useTimezone=true&serverTimezone=UTC");
      dataSourceOL.setUser(propertiesOL.getProperty("MYSQL_DB_USERNAME"));
      dataSourceOL.setPassword(propertiesOL.getProperty("MYSQL_DB_PASSWORD"));
    } catch (IOException e) {
      // e.printStackTrace();
      System.out.println("Failed to Set dataSource with root operation log data properties");
    }
  }

  // get class that represents column type
  public Class getColumnClass(int column) throws IllegalStateException {

    // determine Java class of column
    try {
      String className = metaData.getColumnClassName(column + 1);

      // return Class object that represents className
      return Class.forName(className);
    } // end try
    catch (Exception exception) {
      exception.printStackTrace();
    } // end catch

    return Object.class; // if problems occur above, assume type Object
  } // end method getColumnClass

  // get name of a particular column in ResultSet
  public String getColumnName(int column) throws IllegalStateException {

    // determine column name
    try {
      return metaData.getColumnName(column + 1);
    } // end try
    catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } // end catch

    return ""; // if problems, return empty string for column name
  } // end method getColumnName

  // return number of rows in ResultSet
  @Override
  public int getRowCount() {
    return numberOfRows;
  }

  // get number of columns in ResultSet
  @Override
  public int getColumnCount() {
    // determine number of columns
    try {
      return metaData.getColumnCount();
    } // end try
    catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } // end catch

    return 0; // if problems occur above, return 0 for number of columns
  }

  // obtain value in particular row and column
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    // obtain a value at specified ResultSet row and column
    try {
      resultSet.next(); /* fixes a bug in MySQL/Java with date format */
      resultSet.absolute(rowIndex + 1);
      return resultSet.getObject(columnIndex + 1);
    } // end try
    catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } // end catch

    return ""; // if problems, return empty string object
  }

  // To set the first query so it isnt empty
  // when the table model is empty the gui will not show the table ever
  public void setDefaultQuery(String query) throws SQLException {
    // In appFrame throw exceptions when this function is called to make sure a
    // connection is established

    // specify query and execute it
    // resultSet = null;
    resultSet = statement.executeQuery(query);

    // obtain meta data for ResultSet
    metaData = resultSet.getMetaData();

    // determine number of rows in ResultSet
    resultSet.last(); // move to last row
    numberOfRows = resultSet.getRow(); // get row number

    // notify JTable that model has changed
    fireTableStructureChanged();

  }

  public void setQuery(String query) throws SQLException {
    // In appFrame throw exceptions when this function is called to make sure a
    // connection is established

    // specify query and execute it
    // resultSet = null;
    resultSet = statement.executeQuery(query);

    connectionOL = dataSourceOL.getConnection();
    statementOL = connectionOL.createStatement();
    int result = statementOL.executeUpdate("update operationscount set num_queries = num_queries + 1");
    connectionOL.close();
    // obtain meta data for ResultSet
    metaData = resultSet.getMetaData();

    // determine number of rows in ResultSet
    resultSet.last(); // move to last row
    numberOfRows = resultSet.getRow(); // get row number

    // notify JTable that model has changed
    fireTableStructureChanged();

  }

  public void setUpdate(String query) throws SQLException {
    int res;
    // specify query and execute it
    res = statement.executeUpdate(query);
    /*
     * // obtain meta data for ResultSet
     * metaData = resultSet.getMetaData();
     * // determine number of rows in ResultSet
     * resultSet.last(); // move to last row
     * numberOfRows = resultSet.getRow(); // get row number
     */

    connectionOL = dataSourceOL.getConnection();
    statementOL = connectionOL.createStatement();
    int result = statementOL.executeUpdate("update operationscount set num_updates = num_updates + 1");
    connectionOL.close();

    // notify JTable that model has changed
    fireTableStructureChanged();
    // return true;
  }
}