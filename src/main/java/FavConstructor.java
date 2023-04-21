import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FavConstructor extends JFrame {
    private Connection connection;
    private String email;

    private JPanel panelLabelAdd;
    private JLabel labelAdd;
    private JPanel panelAdd;
    private JPanel panelLabelDelete;
    private JPanel panelDelete;
    private JComboBox comboBoxAdd;
    private JButton buttonAdd;
    private JLabel labelDelete;
    private JComboBox comboBoxDelete;
    private JButton buttonDelete;
    private JPanel panelFav;
    private JLabel labelFav;
    private JPanel panelWarning;
    private JLabel labelWarning;
    private JScrollPane scrollPaneResults;
    private JPanel panelResults;
    private JPanel panelFavConstructor;
    private JComboBox comboBoxOld;
    private JComboBox comboBoxNew;
    private JButton buttonUpdate;
    private JPanel panelReplace;
    private JPanel panelLabelUpdate;
    private JLabel labelUpdate;

    public FavConstructor(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        this.setContentPane(panelFavConstructor);
        this.setTitle("Your Favorite Constructors");
        this.setSize(new Dimension(800, 800));
        this.setMinimumSize(new Dimension(500, 500));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set up the JComboBoxes.
        setUpSelectorAdd();
        setUpSelectorDelete();

        this.panelResults.setLayout(new BoxLayout(panelResults, BoxLayout.Y_AXIS));

        // Set up initial fav drivers display.
        this.setUpResults(runProcedure(email, "get_fav_constructor(?)"));

        setUpButtons();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Set up the items inside the comboBoxAdd for users to add to their favorite constructors.
     */
    public void setUpSelectorAdd() {
        comboBoxAdd.removeAllItems();
        comboBoxNew.removeAllItems();

        comboBoxAdd.addItem("--");
        comboBoxNew.addItem("--");

        try {
            ResultSet rs = runProcedure(email, "selector_add_fav_constructor(?)");
            while (rs.next()) {
                String name = rs.getString("name");
                comboBoxAdd.addItem(name);
                comboBoxNew.addItem(name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up the items inside the comboBoxDelete for users to delete from their favorite constructors.
     */
    public void setUpSelectorDelete() {
        comboBoxDelete.removeAllItems();
        comboBoxOld.removeAllItems();

        comboBoxDelete.addItem("--");
        comboBoxOld.addItem("--");

        try {
            ResultSet rs = runProcedure(email, "selector_delete_fav_constructor(?)");
            while (rs.next()) {
                String name = rs.getString("name");
                comboBoxDelete.addItem(name);
                comboBoxOld.addItem(name);
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

            // Repaint the panelResults to update it with the newest favorite constructor table.
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
                // Turn the selected constructor name in comboBoxAdd to driverId.
                String constructor = (String) comboBoxAdd.getSelectedItem();
                if (!constructor.equals("--")) {
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmt = connection.prepareStatement("SELECT constructor_id FROM constructors WHERE `name` = ?");
                        stmt.setString(1, constructor);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            // Extract the driverId from result.
                            int constructorId = rs.getInt("constructor_id");
                            runProcedure(email, constructorId, "add_fav_constructor(?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_constructor(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    labelWarning.setText("Error! You must select a constructor to add");
                }
            }
        });

        // Set up Delete button.
        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Turn the selected constructor name in comboBoxDelete to constructorId.
                String constructor = (String) comboBoxDelete.getSelectedItem();
                if (!constructor.equals("--")) {
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmt = connection.prepareStatement("SELECT constructor_id FROM constructors WHERE name = ?");
                        stmt.setString(1, constructor);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            // Extract the driverId from result.
                            int constructorId = rs.getInt("constructor_id");
                            runProcedure(email, constructorId, "delete_fav_constructor(?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_constructor(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    labelWarning.setText("Error! You must select a constructor to delete");
                }
            }
        });

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldConstructor = (String) comboBoxOld.getSelectedItem();
                String newConstructor = (String) comboBoxNew.getSelectedItem();
                if (!oldConstructor.equals("--") && !newConstructor.equals("--")) {
                    try {
                        // Execute statement to get driver_id from first_name and last_name in drivers table.
                        PreparedStatement stmtOld = connection.prepareStatement("SELECT constructor_id FROM constructors WHERE name = ?");
                        stmtOld.setString(1, oldConstructor);
                        ResultSet rsOld = stmtOld.executeQuery();
                        PreparedStatement stmtNew = connection.prepareStatement("SELECT constructor_id FROM constructors WHERE name = ?");
                        stmtNew.setString(1, newConstructor);
                        ResultSet rsNew = stmtNew.executeQuery();
                        if (rsOld.next() && rsNew.next()) {
                            // Extract the driverId from result.
                            int oldConstructorId = rsOld.getInt("constructor_id");
                            int newConstructorId = rsNew.getInt("constructor_id");
                            runProcedure(email, oldConstructorId, newConstructorId, "update_fav_constructor(?, ?, ?)");
                            // Update the selectors.
                            setUpSelectorAdd();
                            setUpSelectorDelete();
                            // Update the result display.
                            setUpResults(runProcedure(email, "get_fav_constructor(?)"));
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }

                } else {
                    labelWarning.setText("Error! You must select one constructor from Favorite Constructors and one constructor currently not in Favorite Constructors");
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
     * Helper function to call the add or delete fav constructor procedure.
     *
     * @param email         user_email.
     * @param constructorId constructor_id.
     * @param procedure     procedure name.
     */
    public void runProcedure(String email, int constructorId, String procedure) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedure));
            stmt.setString(1, email);
            stmt.setInt(2, constructorId);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to call the update_fav_constructor procedure.
     *
     * @param oldConstructorId old driver_id.
     * @param newConstructorId new driver_id.
     * @param procedure        procedure name.
     */
    public void runProcedure(String email, int oldConstructorId, int newConstructorId, String procedure) {
        try {
            CallableStatement stmt = connection.prepareCall(String.format("{CALL %s}", procedure));
            stmt.setString(1, email);
            stmt.setInt(2, oldConstructorId);
            stmt.setInt(3, newConstructorId);
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
        panelFavConstructor = new JPanel();
        panelFavConstructor.setLayout(new GridLayoutManager(10, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelLabelAdd = new JPanel();
        panelLabelAdd.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelLabelAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelAdd = new JLabel();
        labelAdd.setText("Select Constructor To Add:");
        panelLabelAdd.add(labelAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelLabelAdd.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panelFavConstructor.add(spacer2, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelAdd = new JPanel();
        panelAdd.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelAdd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxAdd = new JComboBox();
        panelAdd.add(comboBoxAdd, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonAdd = new JButton();
        buttonAdd.setText("Add To Favorite Constructors");
        panelAdd.add(buttonAdd, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelLabelDelete = new JPanel();
        panelLabelDelete.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelLabelDelete, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelDelete = new JLabel();
        labelDelete.setText("Select Constructor To Delete:");
        panelLabelDelete.add(labelDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panelLabelDelete.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelDelete = new JPanel();
        panelDelete.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelDelete, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxDelete = new JComboBox();
        panelDelete.add(comboBoxDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonDelete = new JButton();
        buttonDelete.setText("Delete From Favorite Drivers");
        panelDelete.add(buttonDelete, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelFav = new JPanel();
        panelFav.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelFav, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelFav = new JLabel();
        labelFav.setText("Your Favorite Constructors:");
        panelFav.add(labelFav, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panelFav.add(spacer4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelWarning = new JPanel();
        panelWarning.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelWarning, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelWarning = new JLabel();
        labelWarning.setForeground(new Color(-4175791));
        labelWarning.setText("");
        panelWarning.add(labelWarning, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panelWarning.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        scrollPaneResults = new JScrollPane();
        panelFavConstructor.add(scrollPaneResults, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelResults = new JPanel();
        panelResults.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaneResults.setViewportView(panelResults);
        panelLabelUpdate = new JPanel();
        panelLabelUpdate.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelLabelUpdate, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelUpdate = new JLabel();
        labelUpdate.setText("Replace Favorite Constructor On The Left with Constructor On The Right:");
        panelLabelUpdate.add(labelUpdate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panelLabelUpdate.add(spacer6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panelReplace = new JPanel();
        panelReplace.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelFavConstructor.add(panelReplace, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxOld = new JComboBox();
        panelReplace.add(comboBoxOld, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxNew = new JComboBox();
        panelReplace.add(comboBoxNew, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUpdate = new JButton();
        buttonUpdate.setText("Update");
        panelReplace.add(buttonUpdate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelFavConstructor;
    }

}
