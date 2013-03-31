package processingEmulator;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class ExecuteUpdateMoney {

    private volatile PreparedStatement updateMoneyStatement = null;
    private volatile Connection connection;
    public ReentrantLock lock;

    public ExecuteUpdateMoney(){
        lock = new ReentrantLock();
    }
    private void init() throws SQLException {
        if(connection != null){
            connection.close();
        }
        connection =  MySQLConnection.getInstance().getConnection();
        String query = "UPDATE " + Settings.tableName + " SET value = ? WHERE id = ?";
        updateMoneyStatement = connection.prepareStatement(query);
    }
    public synchronized int action(int id, double money) throws SQLException {
        if(updateMoneyStatement == null || connection == null || connection.isClosed()){
            init();
        }
        updateMoneyStatement.setInt(2, id);
        updateMoneyStatement.setDouble(1, money);
        //System.out.println("UPDATE " + Settings.tableName + " SET value = " + money + " WHERE id = " + id);
        return updateMoneyStatement.executeUpdate();

    }
}

