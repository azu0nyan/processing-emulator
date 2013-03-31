package processingEmulator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class MySQLConnection {
    private static MySQLConnection ourInstance = new MySQLConnection();
    //Connection connection;
    DataSource datasource;

    public static MySQLConnection getInstance() {
        return ourInstance;
    }

    private MySQLConnection() {
        try {
            if(datasource == null ){//connection == null || !connection.isValid(500)
                upConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void upConnection() throws SQLException {
            PoolProperties p = new PoolProperties();
            p.setUrl("jdbc:mysql://" + Settings.DBserverName + "/" + Settings.DBname);
            p.setDriverClassName(Settings.DBdriverName);
            p.setUsername(Settings.DBUsername);
            p.setPassword(Settings.DBPassword);
            p.setJmxEnabled(true); //  нет времени объяснять, копирую из примера.
            p.setTestWhileIdle(false);
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setTestOnReturn(false);
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMaxActive(100);
            p.setInitialSize(10);
            p.setMaxWait(10000);
            p.setRemoveAbandonedTimeout(200);
            p.setMinEvictableIdleTimeMillis(30000);
            p.setMinIdle(10);
            p.setLogAbandoned(true);
            p.setRemoveAbandoned(true);
            /*p.setJdbcInterceptors(
                    "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                            "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");     */
            datasource = new DataSource();
            datasource.setPoolProperties(p);
            //datasource.setRetainStatementAfterResultSetClose(true);
            //connection = datasource.getConnection();


    }
    public Connection getConnection(){
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ResultSet executeQuery(String query){
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return  rs;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public boolean execute(String query){
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.execute(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int executeUpdate(String query) {
        try {
            Statement stmt = getConnection().createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    /* private MySQLConnection() {
        try {
            // Название драйвера
            String driverName = Settings.DBdriverName;

            Class.forName(driverName);

            // Create a connection to the database
            String serverName = Settings.DBserverName;
            String DBName = Settings.DBname;
            String url = "jdbc:mysql://" + serverName + "/" + DBName;
            String username = Settings.DBUsername;
            String password = Settings.DBPassword;

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("is connect to DB" + connection);

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Could not find the database driver
        } catch (SQLException e) {
            e.printStackTrace();
            // Could not connect to the database
        }
    }*/
}
