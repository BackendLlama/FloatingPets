package net.llamasoftware.spigot.floatingpets.manager.sql;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MySQLManager {

    private RowSetFactory rowSetFactory;
    private final HikariDataSource dataSource;
    private final Logger logger;

    public MySQLManager(String server, int port, String databaseName, String username, String password, int poolSize, Logger logger){
        this.logger = logger;
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://" + server + ":" + port + "/" + databaseName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(poolSize);

        try {
            rowSetFactory = RowSetProvider.newFactory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String query, String... values){
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(connection == null)
            return;

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            preparedStatement.execute();

            connection.close();
            preparedStatement.close();

        } catch (SQLException ex){
            logger.warning("An error occurred while executing following sql query (EXECUTE): " + query);
            ex.printStackTrace();
        }
    }

    public CachedRowSet query(String query, String... values){

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(connection == null)
            return null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        CachedRowSet rowSet = null;

        try {
            statement = connection.prepareStatement(query);

            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }

            resultSet = statement.executeQuery();

            rowSet = rowSetFactory.createCachedRowSet();
            rowSet.populate(resultSet);

        } catch (SQLException ex){
            logger.warning("An error occurred while executing following sql query (QUERY): " + query);
            ex.printStackTrace();
        } finally {
            try {
                if(resultSet != null)
                    resultSet.close();
                if(statement != null)
                    statement.close();
            } catch (SQLException ex){
                logger.warning("An error occurred while closing SQL connection");
            }
        }

        return rowSet;
    }

}