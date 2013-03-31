package processingEmulator;


public class AccountsMapDBUpdater extends Thread{

    public boolean isStoped = false;
    @Override
    public void run(){
        while (!isStoped){
            try {
                Thread.currentThread().sleep(5000);
                AccountsMap.getInstance().updateDB();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop_() {
        isStoped = true;
    }
}
