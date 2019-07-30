package Workers;

public class WorkerHandler {
    public static Thread fetchInventory() {
        GetAllWorker allFetcher = new GetAllWorker();
        Thread thread = new Thread(allFetcher);
        thread.start();
        return thread;
    }
}