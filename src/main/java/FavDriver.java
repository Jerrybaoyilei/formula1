import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FavDriver extends JFrame {

    private Connection connection;
    private String email;

    private JPanel panelFavDriver;
    private JPanel panelAdd;
    private JScrollPane scrollPaneResults;
    private JPanel panelResults;
    private JComboBox comboBoxAdd;
    private JButton buttonAdd;
    private JComboBox comboBoxDelete;
    private JButton buttonDelete;
    private JPanel panelDelete;
    private JPanel panelLabelAdd;
    private JLabel labelAdd;
    private JPanel panelLabelDelete;
    private JLabel labelDelete;
    private JPanel panelFav;
    private JLabel labelFav;
    private JPanel panelWarning;
    private JLabel labelWarning;
    private JComboBox comboBoxOld;
    private JComboBox comboBoxNew;
    private JButton buttonUpdate;
    private JPanel panelLabelUpdate;
    private JLabel labelUpdate;
    private JPanel panelUpdate;

    public FavDriver(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        this.setContentPane(panelFavDriver);
        this.setTitle("Your Favorite Drivers");
        this.setSize(new Dimension(800, 800));
        this.setMinimumSize(new Dimension(500, 500));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up the JComboBoxes.
        setUpSelectorAdd();
        setUpSelectorDelete();

        this.panelResults.setLayout(new BoxLayout(panelResults, BoxLayout.Y_AXIS));

        // Set up initial fav drivers display.
        this.setUpResults(runProcedure(email, "get_fav_driver(?)"));

        setUpButtons();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Set up the items inside the comboBoxAdd for users to add to their favorite drivers.
     */
    public void setUpSelectorAdd() {
        comboBoxAdd.removeAllItems();
        comboBoxNew.removeAllItems();

        comboBoxAdd.addItem("--");
        comboBoxNew.addItem("--");

        try {
            ResultSet rs = runProcedure(email, "selector_add_fav_driver(?)");
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                comboBoxAdd.addItem(String.format("%s, %s", lastName, firstName));
                comboBoxNew.addItem(String.format("%s, %s", lastName, firstName));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up the items inside the comboBoxDelete for users to delete from their favorite drivers.
     */
    public void setUpSelectorDelete() {
        comboBoxDelete.removeAllItems();
        comboBoxOld.removeAllItems();

        comboBoxDelete.addItem("--");
        comboBoxOld.addItem("--");

        try {
            ResultSet rs = runProcedure(email, "selector_delete_fav_driver(?)");
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                comboBoxDelete.addItem(String.format("%s, %s", lastName, firstName));
                comboBoxOld.addItem(String.format("%s, %s", lastName, firstName));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to set up fav drivers display.
     */
    public void setUpResults(ResultSet resultSet) {

        try {
            // Clear the current items in panelResults.
            this.panelResults.removeAll();

            // Get info on columns.
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numColumns = resultSetMetaData.getColumnCount();
            // Create table model and add column names. Make sure table model cannot be changed by user.
            DefaultTableModel defaultTableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            // Add column names.
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
            JScrollPane scrollPaneTable = new JScrollPane(table);
            // Add table to the panelResults;
            if (table.getRowCount() > 0) {
                this.panelResults.add(scrollPaneTable);
            }

            // Repaint the panelResults to update it with the newest favorite driver table.
            panelResults.revalidate();
            panelResults.repaint();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up the add and delete buttons.
     */
    public void setUpButtons() {
        // Set up Add button.
        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Turn the selected driver name in comboBoxAdd to driverId.
                String driver = (String) comboBoxAdd.getSelectedItem();
                if (!driver.equals("--")) {
                    String[] nameParts = driver.split(",");
                    String lastName = nameParts[0].trim();
                    String firstName = nameParts[1].trim();
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmt = connection.prepareStatement("SELECT driver_id FROM drivers WHERE first_name = ? AND last_name = ?");
                        stmt.setString(1, firstName);
                        stmt.setString(2, lastName);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            // Extract the driverId from result.
                            int driverId = rs.getInt("driver_id");
                            runProcedure(email, driverId, "add_fav_driver(?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_driver(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    labelWarning.setText("Error! You must select a driver to add");
                }
            }
        });

        // Set up Delete button.
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Turn the selected driver name in comboBoxDelete to driverId.
                String driver = (String) comboBoxDelete.getSelectedItem();
                if (!driver.equals("--")) {
                    String[] nameParts = driver.split(",");
                    String lastName = nameParts[0].trim();
                    String firstName = nameParts[1].trim();
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmt = connection.prepareStatement("SELECT driver_id FROM drivers WHERE first_name = ? AND last_name = ?");
                        stmt.setString(1, firstName);
                        stmt.setString(2, lastName);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            // Extract the driverId from result.
                            int driverId = rs.getInt("driver_id");
                            runProcedure(email, driverId, "delete_fav_driver(?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_driver(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    labelWarning.setText("Error! You must select a driver to delete");
                }
            }
        });

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Turn the selected driver name in comboBoxDelete to driverId.
                String oldDriver = (String) comboBoxOld.getSelectedItem();
                String newDriver = (String) comboBoxNew.getSelectedItem();
                if (!oldDriver.equals("--") && !newDriver.equals("--")) {
                    String[] oldNameParts = oldDriver.split(",");
                    String oldLastName = oldNameParts[0].trim();
                    String oldFirstName = oldNameParts[1].trim();
                    String[] newNameParts = newDriver.split(",");
                    String newLastName = newNameParts[0].trim();
                    String newFirstName = newNameParts[1].trim();
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmtOld = connection.prepareStatement("SELECT driver_id FROM drivers WHERE first_name = ? AND last_name = ?");
                        stmtOld.setString(1, oldFirstName);
                        stmtOld.setString(2, oldLastName);
                        ResultSet rsOld = stmtOld.executeQuery();
                        PreparedStatement stmtNew = connection.prepareStatement("SELECT driver_id FROM drivers WHERE first_name = ? AND last_name = ?");
                        stmtNew.setString(1, newFirstName);
                        stmtNew.setString(2, newLastName);
                        ResultSet rsNew = stmtNew.executeQuery();
                        if (rsOld.next() && rsNew.next()) {
                            // Extract the driverId from result.
                            int oldDriverId = rsOld.getInt("driver_id");
                            int newDriverId = rsNew.getInt("driver_id");
                            runProcedure(email, oldDriverId, newDriverId, "update_fav_driver(?, ?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_driver(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    labelWarning.setText("Error! You must select one driver from Favorite Drivers and one driver currently not in Favorite Drivers");
                }
            }
        });
    }

    /**
     * Helper function to return the results from calling procedures to get items to put inside the comboBoxAdd or
     * comboBoxDelete, or select fav driver names for the user.
     *
     * @param email     the user's email.
     * @param procedure the specific procedure name.
     * @return results to the call.
     */
    public ResultSet runProcedure(String email, String procedure) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedure));
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to call the add or delete fav driver procedure.
     *
     * @param email     user email.
     * @param driverId  driver_id.
     * @param procedure procedure name.
     */
    public void runProcedure(String email, int driverId, String procedure) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedure));
            stmt.setString(1, email);
            stmt.setInt(2, driverId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to call the update_fav_driver procedure.
     *
     * @param oldDriverId old driver_id.
     * @param newDriverId new driver_id.
     * @param procedure   procedure name.
     */
    public void runProcedure(String email, int oldDriverId, int newDriverId, String procedure) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedure));
            stmt.setString(1, email);
            stmt.setInt(2, oldDriverId);
            stmt.setInt(3, newDriverId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        panelFavDriver = new JPanel();
        panelFavDriver.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelAdd = new JPanel();
        panelAdd.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxAdd = new JComboBox();
        comboBoxAdd.setToolTipText("");
        panelAdd.add(comboBoxAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonAdd = new JButton();
        buttonAdd.setText("Add To Favorite Drivers");
        panelAdd.add(buttonAdd, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelFavDriver.add(spacer1, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPaneResults = new JScrollPane();
        panelFavDriver.add(scrollPaneResults, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelResults = new JPanel();
        panelResults.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaneResults.setViewportView(panelResults);
        panelDelete = new JPanel();
        panelDelete.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelDelete, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxDelete = new JComboBox();
        panelDelete.add(comboBoxDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonDelete = new JButton();
        buttonDelete.setText("Delete From Favorite Drivers");
        panelDelete.add(buttonDelete, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelLabelAdd = new JPanel();
        panelLabelAdd.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelLabelAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelAdd = new JLabel();
        labelAdd.setText("Select Driver To Add:");
        panelLabelAdd.add(labelAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panelLabelAdd.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelLabelDelete = new JPanel();
        panelLabelDelete.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelLabelDelete, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelDelete = new JLabel();
        labelDelete.setText("Select Driver To Delete:");
        panelLabelDelete.add(labelDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panelLabelDelete.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelFav = new JPanel();
        panelFav.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelFav, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelFav = new JLabel();
        labelFav.setText("Your Favorite Drivers:");
        panelFav.add(labelFav, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panelFav.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelWarning = new JPanel();
        panelWarning.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelWarning, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelWarning = new JLabel();
        labelWarning.setForeground(new Color(-4175791));
        labelWarning.setText("");
        panelWarning.add(labelWarning, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panelWarning.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelLabelUpdate = new JPanel();
        panelLabelUpdate.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelLabelUpdate, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelUpdate = new JLabel();
        labelUpdate.setText("Replace Favorite Driver On The Left with Driver On The Right:");
        panelLabelUpdate.add(labelUpdate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panelLabelUpdate.add(spacer6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelUpdate = new JPanel();
        panelUpdate.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelFavDriver.add(panelUpdate, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxOld = new JComboBox();
        panelUpdate.add(comboBoxOld, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxNew = new JComboBox();
        panelUpdate.add(comboBoxNew, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUpdate = new JButton();
        buttonUpdate.setText("Update");
        panelUpdate.add(buttonUpdate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelFavDriver;
    }

}
