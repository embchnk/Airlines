package app.db;

import javax.xml.transform.Result;
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
        Model temp;
        ArrayList<Model> array = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet set;
        StringBuilder query = new StringBuilder("select * from " + this.getClass().getSimpleName() + " where ");
        int i = 0;
        /*
         * Build beginning of query string
         */
        for (String key : values.keySet())
            query.append(key).append(" = ? and ");

        // Delete last 'and ' characters
        query.setLength(query.length() - 5);

        /*
         * Initialize prepared statement and fill ArrayList<Model>
         */
        preparedStatement = conn.prepareStatement(query.toString());
        for (String key : values.keySet())
            this.prepareStatement(++i, preparedStatement, key, values.get(key));

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
        PreparedStatement preparedStatement = null;
        ResultSet set;
        StringBuilder query = new StringBuilder("insert into ").append(this.getClass().getSimpleName()).append(" (");
        StringBuilder tempStringBuilder = new StringBuilder();
        int i = 0;

        for (String key : values.keySet()) {
            query.append(key).append(", ");
            tempStringBuilder.append("?, ");
        }
        query.setLength(query.length() - 2);
        tempStringBuilder.setLength(tempStringBuilder.length() - 2);
        query.append(") VALUES (");
        query.append(tempStringBuilder);
        query.append(") RETURNING *");

        preparedStatement = conn.prepareStatement(query.toString());
        for (String key : values.keySet())
            this.prepareStatement(++i, preparedStatement, key, values.get(key));

        try {
            set = preparedStatement.executeQuery();
            temp = this.getClass().getConstructor().newInstance();
            set.next();
            temp.setFields(set);
            return temp;
        } catch (org.postgresql.util.PSQLException e) {
            // Add information about fact that insert operation wasn't successful (GUI)
            System.out.println(e.toString());
            return null;
        }
    }
    /*
     * Update operation executed on objects having values passed as first argument
     * and new values passed as second argument returning all changed tables
     */
    public Model[] update(Hashtable<String, String> values, Hashtable<String, String> new_values)
            throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Model temp;
        ArrayList<Model> array = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet set;
        StringBuilder query = new StringBuilder("update ").append(this.getClass().getSimpleName()).append(" set ");
        int i = 0;

        for (String key : new_values.keySet())
            query.append(key).append(" = ?, ");
        query.setLength(query.length() - 2);

        query.append(" where ");

        for (String key : values.keySet())
            query.append(key).append(" = ? and ");
        query.setLength(query.length() - 4);

        query.append("RETURNING *");

        preparedStatement = conn.prepareStatement(query.toString());
        for (String key : new_values.keySet())
            this.prepareStatement(++i, preparedStatement, key, new_values.get(key));
        for (String key : values.keySet())
            this.prepareStatement(++i, preparedStatement, key, values.get(key));

        set = preparedStatement.executeQuery();

        while (set.next()) {
            temp = this.getClass().getConstructor().newInstance();
            temp.setFields(set);
            array.add(temp);
        }

        return array.toArray(new Model[0]);
    }
    /*
     * Delete tables with values passed as argument, return True if operation was successful
     */
    public boolean delete(Hashtable<String, String> values) throws SQLException {
        StringBuilder query = new StringBuilder("delete from ").append(this.getClass().getSimpleName()).append(" where ");
        PreparedStatement preparedStatement;
        int i = 0;

        for (String key : values.keySet())
            query.append(key).append(" = ?").append(" and ");
        query.setLength(query.length() - 5);

        preparedStatement = conn.prepareStatement(query.toString());

        for (String key : values.keySet())
            this.prepareStatement(++i, preparedStatement, key, values.get(key));

        try {
            preparedStatement.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            return false;
        }

    }
    /*
     * Return all fields values with name of the value as String
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
    /*
     * prepare statement
     */
    private void prepareStatement(int index, PreparedStatement preparedStatement, String column, String value) {
        try {
            Field field = this.getClass().getField(column);
            if (field.get(this) instanceof Integer) {
                preparedStatement.setInt(index, Integer.parseInt(value));
            } else if (field.get(this) instanceof Float) {
                preparedStatement.setFloat(index, Float.parseFloat(value));
            } else if (field.get(this) instanceof Timestamp) {
                preparedStatement.setTimestamp(index, Timestamp.valueOf(value));
            } else {
                preparedStatement.setString(index, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException | SQLException e) {
            System.out.println(e.toString());
        }
    }
}

