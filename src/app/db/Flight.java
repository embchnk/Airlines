package app.db;

import app.db.Model;

import java.sql.Timestamp;

public class Flight extends Model {
    public int id;
    public int departureAirportPK;
    public int arrivalAirportPK;
    public Timestamp departure;
    public Timestamp arrival;
    public int reservations;

    public Flight() {}
}
