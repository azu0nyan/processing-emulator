package processingEmulator;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class ExecuteAddAccount {

    private volatile PreparedStatement addAccountStatement = null;
    private volatile Connection connection;
    public ReentrantLock lock;

    public ExecuteAddAccount(){
        lock = new ReentrantLock();
    }

    public void init() throws SQLException {
        if(connection != null){
            connection.close();
        }
        connection =  MySQLConnection.getInstance().getConnection();
        String query = "INSERT INTO "+ Settings.tableName + " VALUES (?, 0)";
        addAccountStatement = connection.prepareStatement(query);
    }

    public synchronized int action(int id) throws SQLException {
        if(addAccountStatement == null || connection == null || connection.isClosed()){
            init();
        }
        synchronized (addAccountStatement){
            addAccountStatement.setInt(1, id);
            return addAccountStatement.executeUpdate();
        }
    }
}
