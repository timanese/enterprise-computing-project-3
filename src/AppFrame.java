import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.table.TableModel;

import com.mysql.cj.xdevapi.Table;

public class AppFrame extends JFrame implements ActionListener {
  private static final String IMG_PUSHING_P_PNG = "../img/pushingP.png";
  String propertiesFile[] = { "root.properties", "client.properties" };
  GridBagConstraints c = new GridBagConstraints();

  // Class to execute query and update and format table
  ResultSetModel tableModel;

  // Connection section and all its components
  JPanel connectionPanel = new JPanel();
  JLabel connectionTitle = new JLabel("Connection Details", JLabel.CENTER);
  JLabel propFileLabel = new JLabel("properties File");
  JLabel usernameLabel = new JLabel("Username");
  JLabel passwordLabel = new JLabel("Password");
  JTextField usernameField = new JTextField(16);
  JPasswordField passwordField = new JPasswordField(16);
  JComboBox<String> connectionType = new JComboBox<String>(propertiesFile);
  JButton connectBtn = new JButton("Connect to Database");

  // Command section and all its components
  JPanel commandPanel = new JPanel();
  JLabel commandTitle = new JLabel("Enter a SQL Command", JLabel.CENTER);
  JTextArea commandTextArea = new JTextArea(10, 25);
  JScrollPane commandSp = new JScrollPane(commandTextArea);
  JButton clearCommandBtn = new JButton("Clear Command");
  JButton ExecuteCommandBtn = new JButton("Execute Command");

  // Status section and all its components
  JPanel statusPanel = new JPanel();
  JTextField statusTextField = new JTextField();

  // Result sectin and all its components
  JPanel resultPanel = new JPanel();
  // JTextArea resultTextArea = new JTextArea(23, 56);
  JTable resultTable = new JTable();
  JScrollPane sp = new JScrollPane();
  JButton clearResultBtn = new JButton("Clear Result Window");

  // Create drivers that will handle all the connection for root and client
  JdbcRootDriver rootConnection = new JdbcRootDriver();
  jdbcClientDriver clientConnection = new jdbcClientDriver();

  // Use to store a reference of the connection that is used from the two jdbc
  // classses (root or client)
  Connection overallConnection;

  // keep track if there is a valid connection, initalize with false because no
  // connection made initally
  boolean connectionStatus = false;

  AppFrame() throws SQLException {

    // setting connection section content
    connectionTitle.setOpaque(true);
    connectionTitle.setFont(new Font("Verdana", Font.PLAIN, 15));
    // since i set the text to center, idk if i need this with the boxlayout
    connectionTitle.setAlignmentX(CENTER_ALIGNMENT);
    connectionTitle.setPreferredSize(new Dimension(300, 15));
    connectionType.setMaximumSize(connectionType.getPreferredSize());
    connectionType.setAlignmentX(CENTER_ALIGNMENT);
    usernameLabel.setPreferredSize(new Dimension(90, 15));
    passwordLabel.setPreferredSize(new Dimension(90, 15));
    connectionPanel.setBackground(Color.blue);
    connectionPanel.setBounds(0, 0, 350, 250);
    connectBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.out.println(connectionType.getSelectedItem());

        // check to see if user entered a username or password
        if (usernameField.getText().isEmpty() || passwordField.getPassword().length == 0) {
          JOptionPane.showMessageDialog(null,
              "Enter login credentials! Both username and password", "Missing login",
              JOptionPane.ERROR_MESSAGE);

          // clear the username and password field
          usernameField.setText("");
          passwordField.setText("");
          return;
        }

        // determine if the user selected root or client properties file
        if (connectionType.getSelectedItem().equals(propertiesFile[0])) {

          // check to see if the root username a and password is correct
          if (!usernameField.getText().equals(rootConnection.getUsername())
              || !Arrays.equals(passwordField.getPassword(),
                  rootConnection.getPassword().toCharArray())) {

            // clear out username and passsword field
            usernameField.setText("");
            passwordField.setText("");

            JOptionPane.showMessageDialog(null,
                "INVALID CREDENTIALS FOR ROOT LOGIN", "INCORRECT ROOT LOGIN",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("THE PASSWORD IS WRONG BRUHHHHHHHH!");

          } else {
            try {
              System.out.println("Connecting to the root database");
              rootConnection.setConnection();

              // store the connection for a reference used later
              overallConnection = rootConnection.getConnection();

              // Set text of status bar text field to show user the database the connection is
              // connected too
              statusTextField.setText("Connected to " + rootConnection.getURL());

              // update the status of the connection
              connectionStatus = true;
            } catch (SQLException e1) {
              // TODO Auto-generated catch block
              // e1.printStackTrace();
              System.out.println("Failed to connect to root database");
            }

          }
        } else if (connectionType.getSelectedItem().equals(propertiesFile[1])) {

          if (!usernameField.getText().equals(clientConnection.getUsername())
              || !Arrays.equals(passwordField.getPassword(), clientConnection.getPassword().toCharArray())) {
            // clear out username and passsword field
            usernameField.setText("");
            passwordField.setText("");

            JOptionPane.showMessageDialog(null,
                "INVALID CREDENTIALS FOR CLIENT LOGIN", "INCORRECT CLIENT LOGIN",
                JOptionPane.ERROR_MESSAGE);
            System.out.println("THE PASSWORD IS WRONG BRUHHHHHHHH!");

          } else {
            try {
              System.out.println("Connected to the client database");
              clientConnection.setConnection();
              overallConnection = clientConnection.getConnection();
              // Set text of status bar text field to show user the database the connection is
              // connected too
              statusTextField.setText("Connected to " + clientConnection.getURL());

              // update the status of the connection
              connectionStatus = true;
            } catch (SQLException e1) {
              // TODO Auto-generated catch block
              // e1.printStackTrace();
              System.out.println("Failed to connect to client database");
            }
          }
        }
        try {
          tableModel = new ResultSetModel(overallConnection);
          resultTable.setModel(tableModel);
          sp.setViewportView(resultTable);
        } catch (SQLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    // try {
    // tableModel = new ResultSetModel(overallConnection);
    // resultTable.setModel(tableModel);
    // sp.setViewportView(resultTable);
    // } catch (SQLException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }

    // setting SQL command section content
    commandTitle.setOpaque(true);
    commandTitle.setFont(new Font("Verdana", Font.PLAIN, 15));
    commandTextArea.setLineWrap(true);
    commandPanel.setBackground(Color.green);
    commandPanel.setBounds(350, 0, 350, 250);
    ExecuteCommandBtn.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        System.out.println(commandTextArea.getText());
        if (!connectionStatus) {
          // maybe return an exception
          System.out.println("NO CONNECTION HAS BEEN ESTABLISHED");
          return;
        } else {
          System.out.println("CONNECTION FOUND");
        }
        if (!commandTextArea.getText().isBlank()) {
          String query = commandTextArea.getText();

          // trace user entered query to see if keyword select is present
          // if it is then its a query
          if (query.toLowerCase().contains("select")) {
            try {
              tableModel.setQuery(query);
              System.out.print(query);
              System.out.println("======= PRINTING THE TABEL =======");

            } catch (SQLException e1) {
              // TODO Auto-generated catch block
              // e1.printStackTrace();
              System.out.println("Failed to execute given query");
              JOptionPane.showMessageDialog(null,
                  e1.getMessage(), "Database error",
                  JOptionPane.ERROR_MESSAGE);
            }
          } else {
            try {
              tableModel.setUpdate(query);
            } catch (SQLException e1) {
              // TODO Auto-generated catch block
              // e1.printStackTrace();
              System.out.println("Failed to execute given update");
              JOptionPane.showMessageDialog(null,
                  e1.getMessage(), "Database error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        } else {

          JOptionPane.showMessageDialog(null,
              "ENTER A COMMAND", "EMPTY COMMAND",
              JOptionPane.ERROR_MESSAGE);
        }

      }

    });

    // setting status section content
    statusPanel.setBackground(Color.PINK);
    statusPanel.setBounds(0, 250, 700, 40);
    statusTextField.setPreferredSize(new Dimension(675, 25));
    statusTextField.setEditable(false);
    statusTextField.setText("Not connected now");

    // setting result section content
    resultPanel.setBackground(Color.RED);
    resultPanel.setBounds(0, 290, 350, 250);
    // resultTextArea.setLineWrap(true);
    // resultTextArea.setEditable(true);
    // resultTextArea.setWrapStyleWord(true);
    resultTable.setGridColor(Color.BLACK);

    sp.setPreferredSize(new Dimension(650, 375));

    // set JFrame
    this.setTitle("SQL Client App");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.getContentPane().setBackground(Color.lightGray);
    GridBagLayout gridBagLayout = new GridBagLayout();
    this.setLayout(gridBagLayout);
    this.setResizable(true);
    this.setVisible(true);

    // adding all connection section's components
    connectionPanel.add(connectionTitle);
    connectionPanel.add(propFileLabel);
    connectionPanel.add(connectionType);
    connectionPanel.add(usernameLabel);
    connectionPanel.add(usernameField);
    connectionPanel.add(passwordLabel);
    connectionPanel.add(passwordField);
    connectionPanel.add(connectBtn);

    // adding all command section's components
    commandPanel.add(commandTitle);
    commandPanel.add(commandSp);
    commandPanel.add(clearCommandBtn);
    commandPanel.add(ExecuteCommandBtn);

    // adding all status section's components
    statusPanel.add(statusTextField);

    // adding all result section's components
    resultPanel.add(sp);
    resultPanel.add(clearResultBtn);

    // adding the panel sections to the frame
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    this.add(connectionPanel, c);
    c.weightx = 0.2;
    c.weighty = 0.5;
    c.gridx = 1;
    c.gridy = 0;
    this.add(commandPanel, c);
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0;
    c.weighty = 0;
    c.gridwidth = 2;
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = 1;
    this.add(statusPanel, c);

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    c.gridwidth = 2;
    c.gridheight = 1;
    c.gridx = 0;
    c.gridy = 2;
    this.add(resultPanel, c);

    // set the size of the frame
    this.setSize(700, 700);
    ImageIcon logo = new ImageIcon(IMG_PUSHING_P_PNG);
    this.setIconImage(logo.getImage());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub

  }

}
