package processingEmulator;

import com.sun.rowset.CachedRowSetImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class ExecuteSelectAccount {

    private volatile PreparedStatement selectAccountStatement = null;
    private volatile Connection connection = null;
    public ReentrantLock lock;

    public ExecuteSelectAccount(){
        lock = new ReentrantLock();
    }

    public void init() throws SQLException {
        if(connection != null){
            connection.close();
        }
        connection =  MySQLConnection.getInstance().getConnection();
        String query = "SELECT * FROM "+ Settings.tableName + " WHERE id = ?";
        selectAccountStatement = connection.prepareStatement(query);
    }

    public synchronized ResultSet action(int id) throws SQLException {
        if(selectAccountStatement == null || connection == null || connection.isClosed()){
            init();
        }
        selectAccountStatement.setInt(1, id);
        CachedRowSetImpl res = new CachedRowSetImpl();
        synchronized (selectAccountStatement){//Operation not allowed after ResultSet closed
            ResultSet rs = selectAccountStatement.executeQuery();
            if(rs == null){
                return rs;
            }
            res.populate(rs);
        }
        return res;
    }
}
