// Tim Yang

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.AbstractTableModel;

public class ResultSetModel extends AbstractTableModel {

  private Statement statement;
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int numberOfRows;
  private Connection connection;

  public ResultSetModel(Connection connection_) throws SQLException {
    this.connection = connection_;
    // create Statement to query database
    this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);
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

  public void setQuery(String query) throws SQLException {
    // In appFrame throw exceptions when this function is called to make sure a
    // connection is established

    // specify query and execute it
    // resultSet = null;
    resultSet = statement.executeQuery(query);

    // if the executeQuery fails
    // if (resultSet == null) {
    // return false;
    // }

    // // Iterate through the result set and print the returned results
    // System.out.println("Results of the Query: . . . . . . . . . . . . . . . . . .
    // . . . . . . . . . . .\n");
    // while (resultSet.next())
    // System.out.println(resultSet.getString("ridername") + " \t" +
    // resultSet.getString("teamname") + " \t" +
    // resultSet.getString("gender"));
    // // the following print statement works exactly the same
    // // System.out.println(resultSet.getString(1) + " \t" +
    // // resultSet.getString(2) + " \t" + resultSet.getString(3));
    // System.out.println();
    // System.out.println();

    // obtain meta data for ResultSet
    metaData = resultSet.getMetaData();

    // determine number of rows in ResultSet
    resultSet.last(); // move to last row
    numberOfRows = resultSet.getRow(); // get row number

    // notify JTable that model has changed
    fireTableStructureChanged();

    // query was sucessful return true
    // return true;
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
    // notify JTable that model has changed

    fireTableStructureChanged();
    // return true;
  }
}