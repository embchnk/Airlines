package sample;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Dictionary;
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
     * Select operation, argument in dictionary of values which returned
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
        int i = 0;
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
     */
    public Model insert(Hashtable<String, String> values) throws SQLException {
        return new Model();
    }
    /*
     * Update operation executed on objects having values passed as first argument
     * and new values passed as second argument returning all changed tables
     */
    public Model[] update(Hashtable<String, String> values, Hashtable<String, String> new_values) throws SQLException {
        return new Model[0];
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

class Plane extends Model {
    public int id;
    public String model;
    public float capacity;
    public int passengers_number;

    public Plane() {}
}

class PlaneUnit extends Model {
    public int id;
    public int plane_pk;
    public int company_pk;

    public PlaneUnit() {}
}

class Airport extends Model {
    public int id;
    public String city;

    public Airport() {}
}

class Flight extends Model {
    public int id;
    public int departureAirportPK;
    public int arrivalAirportPK;
    public Timestamp departure;
    public Timestamp arrival;
    public int reservations;

    public Flight() {}
}

class Company extends Model {
    public int id;
    public String name;

    public Company() {}
}

class RegistrationCode extends Model {
    public int id;
    public String code;
    public int adminPK;

    public RegistrationCode() {}
}

class Admin extends Model {
    public int id;
    public String login;
    public String password;

    public Admin() {}
}
