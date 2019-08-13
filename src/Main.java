import Utility.Constants;
import Utility.Utils;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        //Utils.testUrl();
        //Utils.printRequiredTags();
        postAll();
    }

    private static void postAll() {
        // Utils.testUrl();

        // Utils.loadData();
        Utils.getCloverTags();
        Utils.makeNewTagsAndPost();
        Utils.getCloverTags();
        // Utils.getCloverItemListPerfectly();
        Utils.getCloverItemListFaster();
        Utils.checkDuplicates();
        Utils.checkReverse();
        Utils.syncItems();
        System.out.println("Size: " + Constants.cloverInventoryList.getObjectList().size());
        Utils.linkItems();

        // Utils.setItemQuantity((CloverItem) Constants.cloverInventoryList.get(2), 5);

        Utils.saveData();
    }
}