import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainPage extends JFrame {
    private Connection connection;
    private String email;
    private JPanel panelMain;
    private JPanel panelSelectors;
    private JPanel panelCheckButtons;
    private JPanel panelFavButtons;
    private JComboBox comboBoxSeason;
    private JComboBox comboBoxRace;
    private JComboBox comboBoxDriver;
    private JLabel labelSeason;
    private JLabel labelRace;
    private JLabel labelDriver;
    private JButton buttonQualifying;
    private JButton buttonPitStop;
    private JButton buttonRace;
    private JButton buttonLapTime;
    private JLabel labelCheck;
    private JLabel labelResults;
    private JScrollPane scrollPaneResults;
    private JPanel panelResults;

    public MainPage(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        // Credit: https://stackoverflow.com/questions/55844177/intellij-swing-gui-not-compiling-because-of-gradle
        // Added intellij's gradle dependency 'com.intellij:forms_rt:7.0.3';
        // Changed Intellij setting "Editor > GUI Designer > Generate GUI Into" into "Java source code";
        this.setContentPane(panelMain);
        this.setTitle("Formula 1 Checker");
        this.setSize(new Dimension(1000, 1000));
        this.setMinimumSize(new Dimension(800, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the 3 JComboBox.
        setUpSelectors();

        // Set the panelResults to BoxLayout so that tables will be shown vertically, one after another.
        this.panelResults.setLayout(new BoxLayout(panelResults, BoxLayout.Y_AXIS));

        // Set up ActionListeners for four check buttons.
        this.setUpCheckButtons();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Set up the selection options for three JComboBoxes.
     */
    public void setUpSelectors() {
        // Set up the items for selecting season (i.e. year).
        try {
            comboBoxSeason.addItem("--");
            Statement stmtSeason = connection.createStatement();
            ResultSet resultSetSeason = stmtSeason.executeQuery("SELECT DISTINCT `year` FROM races ORDER BY `year` DESC");
            while (resultSetSeason.next()) {
                int season = resultSetSeason.getInt("year");
                comboBoxSeason.addItem(season);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Set up the items for selecting race (i.e. xxx Grand Prix).
        try {
            comboBoxRace.addItem("--");
            Statement stmtRace = connection.createStatement();
            ResultSet resultSetRace = stmtRace.executeQuery("SELECT DISTINCT race_name FROM races ORDER BY race_name");
            while (resultSetRace.next()) {
                String race = resultSetRace.getString("race_name");
                comboBoxRace.addItem(race);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Set up the items for selecting driver.
        try {
            comboBoxDriver.addItem("--");
            Statement stmtDriver = connection.createStatement();
            ResultSet resultSetDriver = stmtDriver.executeQuery("SELECT DISTINCT first_name, last_name FROM drivers ORDER BY last_name, first_name");
            while (resultSetDriver.next()) {
                String name = resultSetDriver.getString("last_name") + ", " + resultSetDriver.getString("first_name");
                comboBoxDriver.addItem(name);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Helper function to run a certain procedure in MySQL Formula 1 database to check specific results. Procedure has
     * 4 input parameters.
     *
     * @param procedureName the name of the procedure to be run.
     * @return result set from running the procedure.
     */
    public ResultSet runProcedure(String input1, String input2, String input3, String input4, String procedureName) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedureName));
            stmt.setString(1, input1);
            stmt.setString(2, input2);
            stmt.setString(3, input3);
            stmt.setString(4, input4);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    /**
     * Helper function to run a certain procedure in MySQL Formula 1 database to check specific results. Procedure has
     * 4 input parameters.
     *
     * @param procedureName the name of the procedure to be run.
     * @return result set from running the procedure.
     */
    public ResultSet runProcedure(String input1, String input2, String input3, String procedureName) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedureName));
            stmt.setString(1, input1);
            stmt.setString(2, input2);
            stmt.setString(3, input3);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    /**
     * Helper function to run a certain procedure in MySQL Formula 1 database to check specific results. Procedure has
     * 4 input parameters.
     *
     * @param procedureName the name of the procedure to be run.
     * @return result set from running the procedure.
     */
    public ResultSet runProcedure(String input1, String input2, String procedureName) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedureName));
            stmt.setString(1, input1);
            stmt.setString(2, input2);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    /**
     * Helper function to run a certain procedure in MySQL Formula 1 database to check specific results. Procedure has
     * 4 input parameters.
     *
     * @param procedureName the name of the procedure to be run.
     * @return result set from running the procedure.
     */
    public ResultSet runProcedure(String input1, String procedureName) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedureName));
            stmt.setString(1, input1);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    /**
     * Helper function to run a certain procedure in MySQL Formula 1 database to check specific results. Procedure has
     * 4 input parameters.
     *
     * @param procedureName the name of the procedure to be run.
     * @return result set from running the procedure.
     */
    public ResultSet runProcedure(String procedureName) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedureName));
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ;

    public void setUpResults(String tableTitle, ResultSet resultSet) {
        try {
            // Get info on columns.
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numColumns = resultSetMetaData.getColumnCount();
            // Create table model and add column names.
            DefaultTableModel defaultTableModel = new DefaultTableModel();
            for (int i = 0; i < numColumns; i++) {
                String columnName = resultSetMetaData.getColumnName(i + 1);
                defaultTableModel.addColumn(columnName);
            }
            // Add data from resultSet into this table model.
            while (resultSet.next()) {
                Object[] rowData = new Object[numColumns];
                for (int i = 0; i < numColumns; i++) {
                    rowData[i] = resultSet.getObject(i + 1);
                }
                defaultTableModel.addRow(rowData);
            }

            // Create JTable with the table model.
            JTable table = new JTable(defaultTableModel);

            // Create a JScrollPane and add the table to it.
            JScrollPane scrollPaneTable = new JScrollPane(table);
            scrollPaneTable.setBorder(BorderFactory.createMatteBorder(0, 0, 20, 0, Color.WHITE));

            // Add the table title and the tableScrollPane to the panelResults JPanel.
            JTextPane textPaneTableTitle = new JTextPane();
            textPaneTableTitle.setText(tableTitle);
            // Format the table title pane to font size 18.
            SimpleAttributeSet attributeSet = new SimpleAttributeSet();
            StyleConstants.setFontSize(attributeSet, 18);
            textPaneTableTitle.getStyledDocument().setCharacterAttributes(0, textPaneTableTitle.getDocument().getLength(), attributeSet, false);
            // Add title.
            textPaneTableTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            textPaneTableTitle.setMinimumSize(new Dimension(Integer.MIN_VALUE, 40));
            textPaneTableTitle.setPreferredSize(new Dimension(Integer.MIN_VALUE, 40));
            textPaneTableTitle.setEditable(false);
            this.panelResults.add(textPaneTableTitle);
            // Add table scroll pane
            this.panelResults.add(scrollPaneTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void setUpCheckButtons() {
        // Read the selected items into variables.
        String season = (String) comboBoxSeason.getSelectedItem();
        String race = (String) comboBoxRace.getSelectedItem();
        String driver = (String) comboBoxDriver.getSelectedItem();

        // Set up "Qualifying" button.
        buttonQualifying.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Remove current stuff in the panelResults;
                panelResults.removeAll();
                if (season.equals("--") && race.equals("--") && driver.equals("--")) {
                    System.out.println("Inside the if");
                    ResultSet resultSet = runProcedure("check_qualifying()");
                    setUpResults("All Qualifying Results", resultSet);
                }

                panelResults.revalidate();
                panelResults.repaint();


            }
        });
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelSelectors = new JPanel();
        panelSelectors.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelSelectors, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxSeason = new JComboBox();
        panelSelectors.add(comboBoxSeason, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxRace = new JComboBox();
        panelSelectors.add(comboBoxRace, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxDriver = new JComboBox();
        panelSelectors.add(comboBoxDriver, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelSeason = new JLabel();
        labelSeason.setText("Choose season:");
        panelSelectors.add(labelSeason, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelRace = new JLabel();
        labelRace.setText("Choose Race:");
        panelSelectors.add(labelRace, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelDriver = new JLabel();
        labelDriver.setText("Choose Driver:");
        panelSelectors.add(labelDriver, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelMain.add(spacer1, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelCheckButtons = new JPanel();
        panelCheckButtons.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelCheckButtons, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonQualifying = new JButton();
        buttonQualifying.setText("Qualifying");
        panelCheckButtons.add(buttonQualifying, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonPitStop = new JButton();
        buttonPitStop.setText("Pit Stop");
        panelCheckButtons.add(buttonPitStop, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonRace = new JButton();
        buttonRace.setText("Race");
        panelCheckButtons.add(buttonRace, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonLapTime = new JButton();
        buttonLapTime.setText("Lap Time");
        panelCheckButtons.add(buttonLapTime, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelFavButtons = new JPanel();
        panelFavButtons.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelFavButtons, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelCheck = new JLabel();
        labelCheck.setText("Click a button to check out results");
        panelMain.add(labelCheck, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelResults = new JLabel();
        labelResults.setText("Results:");
        panelMain.add(labelResults, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollPaneResults = new JScrollPane();
        panelMain.add(scrollPaneResults, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelResults = new JPanel();
        panelResults.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaneResults.setViewportView(panelResults);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
