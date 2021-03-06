package com.nmr.demo.Repository;

import com.nmr.demo.Model.Customer;
import com.nmr.demo.Util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements ICustomerRepository {

    private Connection conn;
    private PreparedStatement preparedStatement;

    public CustomerRepository(){
        this.conn = DatabaseConnectionManager.getDatabaseConnection();
    }

    @Override
    public void createCustomer(Customer customer) {
        try {

            String query1 = "INSERT IGNORE INTO nmr.address_table(address_streetname, address_city, address_zipcode, address_country)" + "VALUES(?,?,?,?);";
            preparedStatement = conn.prepareStatement(query1);
            preparedStatement.setString(1, customer.getAddressStreetname());
            preparedStatement.setString(2, customer.getAddressCity());
            preparedStatement.setInt(3, customer.getAddressZipcode());
            preparedStatement.setString(4, customer.getAddressCountry());

            preparedStatement.executeUpdate();

            String queryEmailTable = "INSERT IGNORE INTO nmr.email_table (email)" +
                    "VALUES (?)";
            preparedStatement = conn.prepareStatement(queryEmailTable);
            preparedStatement.setString(1, customer.getEmail());

            preparedStatement.executeUpdate();

            String quertphonenumberTable = "INSERT IGNORE INTO nmr.phonenumber_table (phonenumber)" +
                    "VALUES (?)";
            preparedStatement = conn.prepareStatement(quertphonenumberTable);
            preparedStatement.setString(1, customer.getPhonenumber());

            preparedStatement.executeUpdate();

            String query = "INSERT INTO nmr.customer_table(customer_address_id, customer_email_id, customer_phonenumber_id, customer_first_name, customer_last_name, customer_dob, customer_driverslicense_number)"
                    + "VALUES((SELECT address_id FROM nmr.address_table WHERE address_streetname = ? AND address_city = ?)," +
                    "(SELECT email_id FROM nmr.email_table WHERE email = ?)," +
                    "(SELECT phonenumber_id FROM nmr.phonenumber_table WHERE phonenumber = ?)," +
                    "?,?,?,?)";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,customer.getAddressStreetname());
            preparedStatement.setString(2,customer.getAddressCity());
            preparedStatement.setString(3,customer.getEmail());
            preparedStatement.setString(4, customer.getPhonenumber());
            preparedStatement.setString(5, customer.getFirstName());
            preparedStatement.setString(6, customer.getLastName());
            preparedStatement.setDate(7,java.sql.Date.valueOf(customer.getDob()));
            preparedStatement.setString(8, customer.getDriverslicense());
            preparedStatement.executeUpdate();




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer readOneCustomer(int customer_id) {
        Customer customerToReturn = new Customer();
        try {
            String getCustomer = "SELECT customer_table.customer_id,customer_table.customer_first_name,customer_table.customer_last_name, " +
                    "address_table.address_streetname, address_table.address_city, address_table.address_zipcode,address_table.address_country, " +
                    "email_table.email, " +
                    "phonenumber_table.phonenumber, "+
                    "customer_dob,customer_driverslicense_number " +
                    "FROM nmr.customer_table "+
                    "INNER JOIN nmr.address_table ON customer_table.customer_address_id = address_table.address_id " +
                    "INNER JOIN nmr.email_table ON customer_table.customer_email_id = email_table.email_id " +
                    "INNER JOIN nmr.phonenumber_table ON customer_table.customer_phonenumber_id = phonenumber_table.phonenumber_id WHERE customer_id=? ";
            PreparedStatement myStatement = conn.prepareStatement(getCustomer);
            myStatement.setInt(1,customer_id);
            ResultSet rs = myStatement.executeQuery();
            while (rs.next()) {
                customerToReturn = new Customer(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDate(10).toString(),
                        rs.getString(11));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerToReturn;
    }

    @Override
    public List<Customer> readAllCustomers() {
        List <Customer> allCustomers = new ArrayList<>();
        try {
            String getAllCustomer = "SELECT customer_table.customer_id, customer_table.customer_first_name, customer_table.customer_last_name, " +
                    "address_table.address_streetname, address_table.address_city, address_table.address_zipcode, address_table.address_country, " +
                    "email_table.email, " +
                    "phonenumber_table.phonenumber, "+
                    "customer_dob, customer_driverslicense_number " +
                    "FROM nmr.customer_table "+
                    "INNER JOIN nmr.address_table ON customer_table.customer_address_id = address_table.address_id " +
                    "INNER JOIN nmr.email_table ON customer_table.customer_email_id = email_table.email_id " +
                    "INNER JOIN nmr.phonenumber_table ON customer_table.customer_phonenumber_id = phonenumber_table.phonenumber_id";
            PreparedStatement myStatement = conn.prepareStatement(getAllCustomer);
            ResultSet rs = myStatement.executeQuery();
            while (rs.next()) {
                allCustomers.add(new Customer(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getDate(10).toString(),
                        rs.getString(11)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allCustomers;
    }

    @Override
    public void updateCustomer(Customer c) {
        try {
            String query = "UPDATE IGNORE nmr.customer_table " +
                    "INNER JOIN nmr.address_table ON customer_table.customer_address_id = address_table.address_id " +
                    "INNER JOIN nmr.email_table ON customer_table.customer_email_id = email_table.email_id " +
                    "INNER JOIN nmr.phonenumber_table ON customer_table.customer_phonenumber_id = phonenumber_table.phonenumber_id " +
                    "SET customer_first_name=?, customer_last_name=?, address_table.address_streetname=?, " +
                    "address_table.address_city=?, address_table.address_zipcode=?," +
                    "address_table.address_country=?,email_table.email=?,phonenumber_table.phonenumber=?,customer_dob=?, " +
                    "customer_driverslicense_number=? WHERE customer_id=?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,c.getFirstName());
            preparedStatement.setString(2, c.getLastName());
            preparedStatement.setString(3,c.getAddressStreetname());
            preparedStatement.setString(4,c.getAddressCity());
            preparedStatement.setInt(5,c.getAddressZipcode());
            preparedStatement.setString(6,c.getAddressCountry());
            preparedStatement.setString(7,c.getEmail());
            preparedStatement.setString(8,c.getPhonenumber());
            preparedStatement.setDate(9,java.sql.Date.valueOf(c.getDob()));
            preparedStatement.setString(10,c.getDriverslicense());
            preparedStatement.setInt(11,c.getCustomer_id());

            preparedStatement.execute();
            preparedStatement.close();
        }catch (SQLException s){
            s.printStackTrace();
        }
    }

    @Override
    public void deleteCustomer(int customer_Id) {
        String sql;
        int address_ID;
        int email_ID;
        int phone_ID;
        try{
            sql = "SELECT customer_address_id, customer_email_id, customer_phonenumber_id  FROM nmr.customer_table WHERE customer_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1,customer_Id);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            address_ID = rs.getInt(1);
            email_ID = rs.getInt(2);
            phone_ID = rs.getInt(3);
            try{
                sql = "DELETE FROM nmr.customer_table WHERE customer_id=?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1,customer_Id);
                preparedStatement.execute();
                preparedStatement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            try{
                sql = "DELETE FROM nmr.address_table WHERE address_id=?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1,address_ID);
                preparedStatement.execute();
                preparedStatement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            try{
                sql = "DELETE FROM nmr.email_table WHERE email_id=?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1,email_ID);
                preparedStatement.execute();
                preparedStatement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            try{
                sql = "DELETE FROM nmr.phonenumber_table WHERE phonenumber_id=?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1,phone_ID);
                preparedStatement.execute();
                preparedStatement.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
