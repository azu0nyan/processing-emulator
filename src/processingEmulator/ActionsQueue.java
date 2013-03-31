package processingEmulator;


import java.util.concurrent.LinkedBlockingQueue;

public class ActionsQueue {

    private static ActionsQueue ourInstance = new ActionsQueue();
    private LinkedBlockingQueue<Action> actions;
    private ActionsExecutor executor;

    public static ActionsQueue getInstance() {
        return ourInstance;
    }

    private ActionsQueue() {
        actions = new LinkedBlockingQueue<Action>();
        executor = new ActionsExecutor();
        executor.start();
    }

    public void addAction(Action action){
        actions.offer(action);
    }

    public Action take(){
        try {
            return actions.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
