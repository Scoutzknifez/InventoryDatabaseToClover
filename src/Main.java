import Structures.CloverTag;
import Structures.Item;
import Utility.Constants;
import Utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        Utils.printRequiredTags();
        //postAll();
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
        Utils.saveData();
    }
}