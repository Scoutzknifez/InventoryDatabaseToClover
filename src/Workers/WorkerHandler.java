package Workers;

import Structures.ItemList;
import Utility.Constants;
import Utility.Result;

public class WorkerHandler {
    public static Result fetchInventory() {
        GetAllWorker allFetcher = new GetAllWorker();
        Thread thread = new Thread(allFetcher);
        thread.start();
        try {
            thread.join();
            Constants.inventoryList = new ItemList(allFetcher.getItems());
            return Result.SUCCESS;
        } catch(Exception e) {
            System.out.println("Can not get inventory list from database.");
        }
        return Result.FAILURE;
    }
}