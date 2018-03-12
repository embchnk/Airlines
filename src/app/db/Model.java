package app.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;

// Base class for executing operations on database
public class Model {
    public static Connection conn;
    static {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "admin");
            System.out.println("Connection!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Model() {}
    /*
     * Select operation, argument is dictionary of values which returned
     * objects should have
     */
    public Model[] select(Hashtable<String, String> values)
            throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Model[] returnArray;
        Model temp;
        ArrayList<Model> array = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet set;
        StringBuilder query = new StringBuilder("select * from " + this.getClass().getSimpleName() + " where ");
        int i = 0;
        /*
         * Build beginning of query string
         */
        for (String key : values.keySet()) {
            query.append(key);
            query.append(" = ? and ");
        }
        // Delete last 'and ' characters
        query.setLength(query.length() - 4);

        /*
         * Initialize prepared statement and fill ArrayList<Model>
         */
        preparedStatement = conn.prepareStatement(query.toString());
        for (String key : values.keySet()) {
            preparedStatement.setString(++i, values.get(key));
        }
        set = preparedStatement.executeQuery();
        while (set.next()) {
            temp = this.getClass().getConstructor().newInstance();
            temp.setFields(set);
            array.add(temp);
        }

        return array.toArray(new Model[0]);
    }
    /*
     * Insert operation with values we passed, returning created table
     * INSERT INTO table_name (col1, col2, ...) VALUES (val1, val2, ...) RETURNING *;
     */
    public Model insert(Hashtable<String, String> values)
            throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Model temp;
        PreparedStatement preparedStatement;
        ResultSet set;
        StringBuilder query = new StringBuilder("insert into " + this.getClass().getSimpleName() + " (");
        StringBuilder tempStringBuilder = new StringBuilder();
        int i = 0;

        for (String key : values.keySet()) {
            query.append(key);
            query.append(", ");
            tempStringBuilder.append("?, ");
        }
        query.setLength(query.length() - 2);
        tempStringBuilder.setLength(tempStringBuilder.length() - 2);
        query.append(") VALUES (");
        query.append(tempStringBuilder);
        query.append(") RETURNING *");

        preparedStatement = conn.prepareStatement(query.toString());
        for (String key : values.keySet()) {
            preparedStatement.setString(++i, values.get(key));
        }

        try {
            set = preparedStatement.executeQuery();
            temp = this.getClass().getConstructor().newInstance();
            set.next();
            temp.setFields(set);
            System.out.println(query.toString());
            System.out.println(temp);
            return temp;
        } catch (org.postgresql.util.PSQLException e) {
            // Add information about fact that insert operation wasn't successful (GUI)
            return null;
        }
    }
    /*
     * Update operation executed on objects having values passed as first argument
     * and new values passed as second argument returning all changed tables
     */
    public Model[] update(Hashtable<String, String> values, Hashtable<String, String> new_values) throws SQLException {
        return new Model[0];
    }
    /*
     * Delete tables with values passed as argument, return True if operation was successful
     */
    public boolean delete(Hashtable<String, String> values) throws SQLException {
        return true;
    }
    /*
     * Return all fields values with name of the value
     */
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        for (Field f : this.getClass().getDeclaredFields()) {
            sBuilder.append(f.getName());
            sBuilder.append(": ");
            try {
                sBuilder.append(f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sBuilder.append(" ");
        }
        return sBuilder.toString();
    }
    /*
     * Set all fields of object with values passed as ResultSet type object
     */
    private void setFields(ResultSet set) {
        int i = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                if (field.get(this) instanceof Integer) {
                    field.set(this, Integer.parseInt(set.getString(++i)));
                } else if (field.get(this) instanceof Float) {
                    field.set(this, Float.parseFloat(set.getString(++i)));
                } else if (field.get(this) instanceof Timestamp) {
                    field.set(this, Timestamp.valueOf(set.getString(++i)));
                } else {
                    field.set(this, set.getString(++i));
                }
            } catch (IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }

    }
}

