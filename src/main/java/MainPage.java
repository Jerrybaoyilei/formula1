import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class MainPage extends JFrame {

    private Connection connection;
    private String email;
    private String URL_LINK = "https://docs.google.com/document/d/1jM8JdQNveXrcXTaDVXYF9DJnFVK5ngVfO4Fj_-5WrxU/edit?usp=sharing";

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
    private JPanel panelWarning;
    private JLabel labelWarning;
    private JComboBox comboBoxConstructor;
    private JLabel labelConstructor;
    private JButton buttonFavConstructor;
    private JButton buttonFavDriver;
    private JButton buttonInfo;

    public MainPage(Connection connection, String email) {
        this.connection = connection;
        this.email = email;

        // Credit: https://stackoverflow.com/questions/55844177/intellij-swing-gui-not-compiling-because-of-gradle
        // Added intellij's gradle dependency 'com.intellij:forms_rt:7.0.3';
        // Changed Intellij setting "Editor > GUI Designer > Generate GUI Into" into "Java source code";
        this.setContentPane(panelMain);
        this.setTitle("Formula 1 Checker");
        this.setSize(new Dimension(1500, 1000));
        this.setMinimumSize(new Dimension(1000, 800));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up the 3 JComboBox.
        setUpSelectors();

        // Set the panelResults to BoxLayout so that tables will be shown vertically, one after another.
        this.panelResults.setLayout(new BoxLayout(panelResults, BoxLayout.Y_AXIS));

        // Set up ActionListeners for four check buttons.
        this.setUpButtons();

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

        // Set up the items for selecting constructor.
        try {
            comboBoxConstructor.addItem("--");
            Statement stmtConstructor = connection.createStatement();
            ResultSet resultSetConstructor = stmtConstructor.executeQuery("SELECT DISTINCT `name` FROM constructors ORDER BY `name`");
            while (resultSetConstructor.next()) {
                String name = resultSetConstructor.getString("name");
                comboBoxConstructor.addItem(name);
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
     * Set up the button functions for checking results.
     */
    public void setUpButtons() {

        // Set up "Qualifying" button.
        buttonQualifying.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Read the selected items into variables.
                String season = String.valueOf(comboBoxSeason.getSelectedItem());
                String race = (String) comboBoxRace.getSelectedItem();
                String driver = (String) comboBoxDriver.getSelectedItem();

                // Remove current stuff in the panelResults;
                panelResults.removeAll();

                if (season.equals("--")) {
                    if (race.equals("--")) {
                        // Case 1: user didn't select any of the season, race, or driver.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure("check_qualifying");
                            setUpResults("All Qualifying Results", resultSet);
                            ResultSet rs = runProcedure("check_sprint");
                            setUpResults("All Sprint Results", rs);
                        }
                        // Case 2: user provided only driver. Show this driver's qualifying results in all seasons
                        //          and all races. Order by year DESC & race round.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(firstName, lastName, "check_qualifying_driver(?, ?)");
                            setUpResults(String.format("Qualifying Results for %s", firstName + " " + lastName), resultSet);
                            ResultSet rs = runProcedure(firstName, lastName, "check_sprint_driver(?, ?)");
                            setUpResults(String.format("Sprint Results for %s", firstName + " " + lastName), rs);
                        }
                    } else {
                        // Case 3: user provided only race. Show all qualifying results for this race over years. Order
                        //          by year DESC & driver position.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure(race, "check_qualifying_race(?)");
                            setUpResults(String.format("Qualifying Results for %s", race), resultSet);
                            ResultSet rs = runProcedure(race, "check_sprint_race(?)");
                            setUpResults(String.format("Sprint Results for %s", race), rs);
                        }
                        // Case 4: user provided race and driver. Show all qualifying results for this driver in this
                        // race over years. Order by years DESC.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(race, firstName, lastName, "check_qualifying_race_driver(?, ?, ?)");
                            setUpResults(String.format("Qualifying Results for %s in %s", firstName + " " + lastName, race), resultSet);
                            ResultSet rs = runProcedure(race, firstName, lastName, "check_sprint_race_driver(?, ?, ?)");
                            setUpResults(String.format("Sprint Results for %s in %s", firstName + " " + lastName, race), rs);
                        }
                    }
                } else {
                    if (race.equals("--")) {
                        // Case 5: user provided season. Show all qualifying results in all races in this season. Order
                        //          by race round then qualifying position.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure(season, "check_qualifying_season(?)");
                            setUpResults(String.format("Qualifying Results in %s", season), resultSet);
                            ResultSet rs = runProcedure(season, "check_sprint_season(?)");
                            setUpResults(String.format("Sprint Results in %s", season), rs);
                        }
                        // Case 6: user provided season and driver. Show all qualifying results in all races in this
                        //          season for this driver. Order by race rounds.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, firstName, lastName, "check_qualifying_season_driver(?, ?, ?)");
                            setUpResults(String.format("Qualifying Results for %s in %s", firstName + " " + lastName, season), resultSet);
                            ResultSet rs = runProcedure(season, firstName, lastName, "check_sprint_season_driver(?, ?, ?)");
                            setUpResults(String.format("Sprint Results for %s in %s", firstName + " " + lastName, season), rs);
                        }
                    } else {
                        // Case 7: user provided season and race. Show qualifying results for all drivers. Order by
                        //          position.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure(season, race, "check_qualifying_season_race(?, ?)");
                            setUpResults(String.format("Qualifying Results in %s %s", season, race), resultSet);
                            ResultSet rs = runProcedure(season, race, "check_sprint_season_race(?, ?)");
                            setUpResults(String.format("Sprint Results in %s %s", season, race), rs);
                        }
                        // Case 8: user provided season, race, and driver.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, race, firstName, lastName, "check_qualifying_season_race_driver(?, ?, ?, ?)");
                            setUpResults(String.format("Qualifying Result for %s in %s %s", firstName + " " + lastName, season, race), resultSet);
                            ResultSet rs = runProcedure(season, race, firstName, lastName, "check_sprint_season_race_driver(?, ?, ?, ?)");
                            setUpResults(String.format("Sprint Result for %s in %s %s", firstName + " " + lastName, season, race), rs);
                        }
                    }
                }

                // Repaint the panelResults to update it with the corresponding result tables.
                panelResults.revalidate();
                panelResults.repaint();
            }
        });

        // Set up "Race" button.
        buttonRace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Read the selected items into variables.
                String season = String.valueOf(comboBoxSeason.getSelectedItem());
                String race = (String) comboBoxRace.getSelectedItem();
                String constructor = (String) comboBoxConstructor.getSelectedItem();
                String driver = (String) comboBoxDriver.getSelectedItem();

                // Remove current stuff in the panelResults;
                panelResults.removeAll();

                if (season.equals("--")) {
                    if (race.equals("--")) {
                        if (constructor.equals("--")) {
                            // Case 1: user provided no input. Show both full tables for drivers and constructors.
                            if (driver.equals("--")) {
                                ResultSet rsDriver = runProcedure("check_race_d");
                                setUpResults("All Race Results for Drivers", rsDriver);
                                ResultSet rsConstructor = runProcedure("check_race_c");
                                setUpResults("All Race Results for Constructors", rsConstructor);
                            }
                            // Case 2: user provided driver. Show driver races results for this driver. Order by year
                            //          DESC and race round.
                            else {
                                String[] nameParts = driver.split(",");
                                String lastName = nameParts[0].trim();
                                String firstName = nameParts[1].trim();
                                ResultSet rsDriver = runProcedure(firstName, lastName, "check_race_d_driver(?, ?)");
                                setUpResults(String.format("Race Results for %s %s", firstName, lastName), rsDriver);
                            }
                        } else {
                            // Case 3: user provided constructor. Show constructor race results for this constructor.
                            //          Order by year DESC and race round.
                            if (driver.equals("--")) {
                                ResultSet rsConstructor = runProcedure(constructor, "check_race_c_constructor(?)");
                                setUpResults(String.format("Race Results for %s", constructor), rsConstructor);
                                ResultSet rsDriver = runProcedure(constructor, "check_race_c_constructor_breakdown(?)");
                                setUpResults(String.format("Race Results for Drivers Driving for %s", constructor), rsDriver);
                            }
                            // Case 4: user provided both constructor and driver. Show no result because there is a conflict.
                            else {
                                labelWarning.setText("Error! Cannot select both constructor and driver when checking Race results");
                            }
                        }
                    } else {
                        if (constructor.equals("--")) {
                            // Case 5: user provided race. Show race results for all constructors and drivers in this
                            //          specific Grand Prix. Order by year DESC then position.
                            if (driver.equals("--")) {
                                ResultSet rsDriver = runProcedure(race, "check_race_d_race(?)");
                                setUpResults(String.format("Driver Race Results in %s", race), rsDriver);
                                ResultSet rsConstructor = runProcedure(race, "check_race_c_race(?)");
                                setUpResults(String.format("Constructor Race Results in %s", race), rsConstructor);
                            }
                            // Case 6: user provided both race and driver. Show race results for this driver in this
                            //          Grand Prix over the years. Order by year DESC.
                            else {
                                String[] nameParts = driver.split(",");
                                String lastName = nameParts[0].trim();
                                String firstName = nameParts[1].trim();
                                ResultSet rsDriver = runProcedure(race, firstName, lastName, "check_race_d_race_driver(?, ?, ?)");
                                setUpResults(String.format("Race Results for %s %s in %s", firstName, lastName, race), rsDriver);
                            }
                        } else {
                            // Case 7: user provided both race and constructor. Show race results for this constructor
                            //          in this Grand Prix over the years. Order by year DESC.
                            if (driver.equals("--")) {
                                ResultSet rsConstructor = runProcedure(race, constructor, "check_race_c_race_constructor(?, ?)");
                                setUpResults(String.format("Race Results for %s in %s", constructor, race), rsConstructor);
                            }
                            // Case 8: user provided race, constructor, and driver. Conflict.
                            else {
                                labelWarning.setText("Error! Cannot select both constructor and driver when checking Race results");
                            }
                        }
                    }
                } else {
                    if (race.equals("--")) {
                        if (constructor.equals("--")) {
                            // Case 9: user provided season. Show all race results of all races in this season for both
                            //          constructors and drivers. Also show end of season constructor and driver
                            //          standings.
                            if (driver.equals("--")) {
                                ResultSet rsDriver = runProcedure(season, "check_race_d_season(?)");
                                setUpResults(String.format("Driver Race Results in %s", season), rsDriver);
                                ResultSet rsDriverStanding = runProcedure(season, "check_driver_standing_season(?)");
                                setUpResults(String.format("Driver Standings by the end of %s", season), rsDriverStanding);
                                ResultSet rsConstructor = runProcedure(season, "check_race_c_season(?)");
                                setUpResults(String.format("Constructor Race Results in %s", season), rsConstructor);
                                ResultSet rsConstructorStanding = runProcedure(season, "check_constructor_standing_season(?)");
                                setUpResults(String.format("Constructor Standings by the end of %s", season), rsConstructorStanding);
                            }
                            // Case 10: user provided season and driver. Show all race results in all races in this
                            //          season for this driver. Order by race round.
                            else {
                                String[] nameParts = driver.split(",");
                                String lastName = nameParts[0].trim();
                                String firstName = nameParts[1].trim();
                                ResultSet rsDriver = runProcedure(season, firstName, lastName, "check_race_d_season_driver(?, ?, ?)");
                                setUpResults(String.format("Driver Results for %s %s in %s", firstName, lastName, season), rsDriver);
                            }
                        } else {
                            // Case 11: user provided season and constructor. Show all race results for this constructor
                            //          in this season. Include results for the drivers driving for this constructor.
                            //          Order by race rounds.
                            if (driver.equals("--")) {
                                ResultSet rsConstructor = runProcedure(season, constructor, "check_race_c_season_constructor(?, ?)");
                                setUpResults(String.format("Race Results for %s in %s", constructor, season), rsConstructor);
                                ResultSet rsDriver = runProcedure(season, constructor, "check_race_c_season_constructor_breakdown(?, ?)");
                                setUpResults(String.format("Race Results for Drivers Driving for %s in %s", constructor, season), rsDriver);
                            }
                            // Case 12: user provided season, constructor, and driver. Conflict.
                            else {
                                labelWarning.setText("Error! Cannot select both constructor and driver when checking Race results");
                            }
                        }
                    } else {
                        if (constructor.equals("--")) {
                            // Case 13: user provided season and race. Show race results for all drivers and
                            //          constructors in this race. Order by race final orders.
                            if (driver.equals("--")) {
                                ResultSet rsDriver = runProcedure(season, race, "check_race_d_season_race(?, ?)");
                                setUpResults(String.format("Driver Race Results in %s %s", season, race), rsDriver);
                                ResultSet rsConstructor = runProcedure(season, race, "check_race_c_season_race(?, ?)");
                                setUpResults(String.format("Constructor Race Results in %s %s", season, race), rsConstructor);
                            }
                            // Case 14: user provided season, race, and driver. Show race result for this driver in this
                            //          specific Grand Prix in this year.
                            else {
                                String[] nameParts = driver.split(",");
                                String lastName = nameParts[0].trim();
                                String firstName = nameParts[1].trim();
                                ResultSet rsDriver = runProcedure(season, race, firstName, lastName, "check_race_d_season_race_driver(?, ?, ?, ?)");
                                setUpResults(String.format("Race Result for %s %s in %s %s", firstName, lastName, season, race), rsDriver);
                            }
                        } else {
                            // Case 15: user provided season, race, and constructor. Show race results for this
                            //          constructor in this Grand PRix in this year. Also show the race results for
                            //          drivers driving for this constructor.
                            if (driver.equals("--")) {
                                ResultSet rsConstructor = runProcedure(season, race, constructor, "check_race_c_season_race_constructor(?, ?, ?)");
                                setUpResults(String.format("Race Result for %s in %s %s", constructor, season, race), rsConstructor);
                                ResultSet rsDriver = runProcedure(season, race, constructor, "check_race_c_season_race_constructor_breakdown(?, ?, ?)");
                                setUpResults(String.format("Race Result for Drivers Driving for %s in %s %s", constructor, season, race), rsDriver);
                            }
                            // Case 16: user provided season, race, constructor, and driver. Conflict.
                            else {
                                labelWarning.setText("Error! Cannot select both constructor and driver when checking Race results");
                            }
                        }
                    }
                }

                // Repaint the panelResults to update it with the corresponding result tables.
                panelResults.revalidate();
                panelResults.repaint();
            }
        });

        // Set up "Lap Time" button.
        buttonLapTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Read the selected items into variables.
                String season = String.valueOf(comboBoxSeason.getSelectedItem());
                String race = (String) comboBoxRace.getSelectedItem();
                String driver = (String) comboBoxDriver.getSelectedItem();

                // Remove current stuff in the panelResults;
                panelResults.removeAll();

                if (season.equals("--")) {
                    if (race.equals("--")) {
                        // Case 1: Not enough information provided. Show warning.
                        labelWarning.setText("Select at least two from Season, Race, or Driver to obtain Lap Time results");
                    } else {
                        // Case 2: user provided race and driver. Show all lap times for this Grand Prix and driver over
                        //          years. Order by years DESC then laps.
                        if (!driver.equals("--")) {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(race, firstName, lastName, "check_laps_race_driver(?, ?, ?)");
                            setUpResults(String.format("Lap Times for %s %s in %s", firstName, lastName, race), resultSet);
                        }
                    }
                } else {
                    if (race.equals("--")) {
                        // Case 3: Not enough information provided. Show warning.
                        if (driver.equals("--")) {
                            labelWarning.setText("Select at least two from Season, Race, or Driver to obtain Lap Time results");
                        }
                        // Case 4: user provided season and driver. Show all lap times in all races in this season for
                        //          this driver. Order by race rounds then laps.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, firstName, lastName, "check_laps_season_driver(?, ?, ?)");
                            setUpResults(String.format("Lap Times for %s %s in %s", firstName, lastName, season), resultSet);
                        }
                    } else {
                        // Case 5: user provided season and race. Show lap times for all drivers in this season in this
                        //          race. Order by lap then by order in the lap.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure(season, race, "check_laps_season_race(?, ?)");
                            setUpResults(String.format("Lap Times in %s %s", season, race), resultSet);
                        }
                        // Case 6: user provided season, race and driver. Show lap times for this driver in this season
                        //          in this race. Order by lap.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, race, firstName, lastName, "check_laps_season_race_driver(?, ?, ?, ?)");
                            setUpResults(String.format("Lap Times for %s %s in %s %s", firstName, lastName, season, race), resultSet);
                        }
                    }
                }

                // Repaint the panelResults to update it with the corresponding result tables.
                panelResults.revalidate();
                panelResults.repaint();
            }
        });

        // Set up "Pit Stop" button.
        buttonPitStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Read the selected items into variables.
                String season = String.valueOf(comboBoxSeason.getSelectedItem());
                String race = (String) comboBoxRace.getSelectedItem();
                String driver = (String) comboBoxDriver.getSelectedItem();

                // Remove current stuff in the panelResults;
                panelResults.removeAll();

                if (season.equals("--")) {
                    if (race.equals("--")) {
                        // Case 1: Not enough information provided. Show warning.
                        labelWarning.setText("Select at least two from Season, Race, or Driver to obtain Pit Stop results");
                    } else {
                        // Case 2: user provided race and driver. Show all lap times for this Grand Prix and driver over
                        //          years. Order by years DESC then laps.
                        if (!driver.equals("--")) {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(race, firstName, lastName, "check_pit_stop_race_driver(?, ?, ?)");
                            setUpResults(String.format("Pit Stops for %s %s in %s", firstName, lastName, race), resultSet);
                        }
                    }
                } else {
                    if (race.equals("--")) {
                        // Case 3: Not enough information provided. Show warning.
                        if (driver.equals("--")) {
                            labelWarning.setText("Select at least two from Season, Race, or Driver to obtain Pit Stop results");
                        }
                        // Case 4: user provided season and driver. Show all lap times in all races in this season for
                        //          this driver. Order by race rounds then laps.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, firstName, lastName, "check_pit_stop_season_driver(?, ?, ?)");
                            setUpResults(String.format("Pit Stops for %s %s in %s", firstName, lastName, season), resultSet);
                        }
                    } else {
                        // Case 5: user provided season and race. Show lap times for all drivers in this season in this
                        //          race. Order by lap then by order in the lap.
                        if (driver.equals("--")) {
                            ResultSet resultSet = runProcedure(season, race, "check_pit_stop_season_race(?, ?)");
                            setUpResults(String.format("Pit Stops in %s %s", season, race), resultSet);
                        }
                        // Case 6: user provided season, race and driver. Show lap times for this driver in this season
                        //          in this race. Order by lap.
                        else {
                            String[] nameParts = driver.split(",");
                            String lastName = nameParts[0].trim();
                            String firstName = nameParts[1].trim();
                            ResultSet resultSet = runProcedure(season, race, firstName, lastName, "check_pit_stop_season_race_driver(?, ?, ?, ?)");
                            setUpResults(String.format("Pit Stops for %s %s in %s %s", firstName, lastName, season, race), resultSet);
                        }
                    }
                }

                // Repaint the panelResults to update it with the corresponding result tables.
                panelResults.revalidate();
                panelResults.repaint();
            }
        });

        // Set up "Favorite Driver" button.
        buttonFavDriver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FavDriver(connection, email);
            }
        });

        // Set up "Favorite Constructors" button.
        buttonFavConstructor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FavConstructor(connection, email);
            }
        });

        // Set up "Not Familiar" button.
        buttonInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(URL_LINK));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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

    /**
     * Helper function to set up the results displayed.
     *
     * @param tableTitle title of the displayed table.
     * @param resultSet  the results of executing MySQL procedures.
     */
    public void setUpResults(String tableTitle, ResultSet resultSet) {
        try {
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
            scrollPaneTable.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));

            // Add the table title and the tableScrollPane to the panelResults JPanel if table has data.
            if (table.getRowCount() > 0) {
                labelWarning.setText("Related data successfully obtained");
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
            }
            else {
                labelWarning.setText("No data for this combination");
            }
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
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelSelectors = new JPanel();
        panelSelectors.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelSelectors, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxSeason = new JComboBox();
        panelSelectors.add(comboBoxSeason, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxRace = new JComboBox();
        panelSelectors.add(comboBoxRace, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxDriver = new JComboBox();
        panelSelectors.add(comboBoxDriver, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelSeason = new JLabel();
        labelSeason.setText("Choose season:");
        panelSelectors.add(labelSeason, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelRace = new JLabel();
        labelRace.setText("Choose Race:");
        panelSelectors.add(labelRace, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelDriver = new JLabel();
        labelDriver.setText("Choose Driver:");
        panelSelectors.add(labelDriver, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxConstructor = new JComboBox();
        panelSelectors.add(comboBoxConstructor, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelConstructor = new JLabel();
        labelConstructor.setText("Choose Constructor:");
        panelSelectors.add(labelConstructor, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panelFavButtons.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelFavButtons, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonFavConstructor = new JButton();
        buttonFavConstructor.setText("Favorite Constructors");
        panelFavButtons.add(buttonFavConstructor, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonFavDriver = new JButton();
        buttonFavDriver.setText("Favorite Drivers");
        panelFavButtons.add(buttonFavDriver, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonInfo = new JButton();
        buttonInfo.setText("Not familiar with Formula 1? Check this out!");
        panelFavButtons.add(buttonInfo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCheck = new JLabel();
        labelCheck.setText("Click a button to check out results");
        panelMain.add(labelCheck, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelResults = new JLabel();
        labelResults.setText("Results:");
        panelMain.add(labelResults, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollPaneResults = new JScrollPane();
        panelMain.add(scrollPaneResults, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panelResults = new JPanel();
        panelResults.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaneResults.setViewportView(panelResults);
        panelWarning = new JPanel();
        panelWarning.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panelWarning, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelWarning = new JLabel();
        labelWarning.setForeground(new Color(-4175791));
        labelWarning.setText("\"Constructor\" selection is ignored for Qualifying, Lap Time ,or Pit Stop checks. Don't select both \"Constructor\" and \"Driver\" for Race check. ");
        panelWarning.add(labelWarning, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
