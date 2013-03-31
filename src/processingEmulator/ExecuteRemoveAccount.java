package processingEmulator;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class ExecuteRemoveAccount {

    private volatile PreparedStatement removeAccountStatement = null;
    private volatile Connection connection = null;
    public ReentrantLock lock;

    public ExecuteRemoveAccount(){
        lock = new ReentrantLock();
    }

    private void init() throws SQLException {
        if(connection != null){
            connection.close();
        }
        connection =  MySQLConnection.getInstance().getConnection();
        String query = "DELETE FROM " + Settings.tableName + " WHERE id = ?";
        removeAccountStatement = connection.prepareStatement(query);
    }

    public synchronized int action(int id) throws SQLException {
        if(removeAccountStatement == null || connection == null || connection.isClosed()){
            init();
        }
        synchronized (removeAccountStatement){
            removeAccountStatement.setInt(1, id);
            return removeAccountStatement.executeUpdate();
        }
    }
}
