package processingEmulator;


import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadExecuteRemoveAccount {

    private static MultiThreadExecuteRemoveAccount ourInstance = new MultiThreadExecuteRemoveAccount();
    CopyOnWriteArrayList<ExecuteRemoveAccount> removeAccountExecutors;


    public static MultiThreadExecuteRemoveAccount getInstance() {
        return ourInstance;
    }

    private MultiThreadExecuteRemoveAccount() {
        removeAccountExecutors = new CopyOnWriteArrayList<ExecuteRemoveAccount>();
    }

    public int execute(int id) throws SQLException {
        ExecuteRemoveAccount currentExecutor = null;
        try{
            for(ExecuteRemoveAccount executor : removeAccountExecutors){
                if(executor.lock.tryLock()){
                    currentExecutor = executor;
                    break;
                }
            }
            if(currentExecutor == null){
                currentExecutor = new ExecuteRemoveAccount();
                currentExecutor.lock.lock();
                removeAccountExecutors.add(currentExecutor);
            }
            return currentExecutor.action(id);

        } finally {
            if(currentExecutor != null){
                currentExecutor.lock.unlock();
            }
        }
    }

    public int getExecutorsCount(){
        return removeAccountExecutors.size();
    }
    public void clear(){
        removeAccountExecutors.clear();
    }
}
