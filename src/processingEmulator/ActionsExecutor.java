package processingEmulator;


public class ActionsExecutor extends Thread {

    private boolean isStoped = false;

    public ActionsExecutor(){

    }

    @Override
    public void run(){
        while(!isStoped){
            ActionsQueue.getInstance().take().action();
        }
    }

    public void setStoped(boolean stoped) {
        isStoped = stoped;
    }
}
