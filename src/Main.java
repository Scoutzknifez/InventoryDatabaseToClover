import Utility.Constants;
import Utility.Utils;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        debug();
    }

    private static void debug() {
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
        Utils.saveData();
    }
}