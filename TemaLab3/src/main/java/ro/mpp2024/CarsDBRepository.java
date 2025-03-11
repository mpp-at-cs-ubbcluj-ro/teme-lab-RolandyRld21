package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry("Finding cars by manufacturer: {}", manufacturerN);
        List<Car> cars = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM table_name WHERE manufacturer = ?")) {
            stmt.setString(1, manufacturerN);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String manufacturer = rs.getString("manufacturer");
                String model = rs.getString("model");
                int year = rs.getInt("year");
                Car car = new Car(manufacturer, model, year);
                car.setId(id);
                cars.add(car);
            }
        } catch (SQLException e) {
            logger.error("Database error: ", e);
        }
        return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        logger.traceEntry("Finding cars between years {} and {}", min, max);
        List<Car> cars = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM table_name WHERE year BETWEEN ? AND ?")) {
            stmt.setInt(1, min);
            stmt.setInt(2, max);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String manufacturer = rs.getString("manufacturer");
                String model = rs.getString("model");
                int year = rs.getInt("year");
                Car car = new Car(manufacturer, model, year);
                car.setId(id);
                cars.add(car);
            }
        } catch (SQLException e) {
            logger.error("Database error: ", e);
        }
        return cars;
    }

    @Override
    public void add(Car elem) {
        logger.traceEntry("Saving car: {}", elem);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO table_name (year, model, manufacturer) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preStmt.setInt(1, elem.getYear());
            preStmt.setString(2, elem.getModel());
            preStmt.setString(3, elem.getManufacturer());
            int result = preStmt.executeUpdate();
            logger.trace("Saved instance, affected rows: {}", result);
        } catch (SQLException e) {
            logger.error("Database error: ", e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer id, Car elem) {
        logger.traceEntry("Updating car with id: {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement stmt = con.prepareStatement("UPDATE table_name SET year = ?, model = ?, manufacturer = ? WHERE id = ?")) {
            stmt.setInt(1, elem.getYear());
            stmt.setString(2, elem.getModel());
            stmt.setString(3, elem.getManufacturer());
            stmt.setInt(4, id);
            int result = stmt.executeUpdate();
            logger.trace("Updated instance, affected rows: {}", result);
        } catch (SQLException e) {
            logger.error("Database error: ", e);
        }
        logger.traceExit();
    }

    @Override
    public Iterable<Car> findAll() {
        logger.traceEntry("Finding all cars");
        List<Car> cars = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM table_name")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String manufacturer = result.getString("manufacturer");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit(cars);
        return cars;
    }
}
