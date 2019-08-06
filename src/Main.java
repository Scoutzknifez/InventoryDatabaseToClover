import Utility.Constants;
import Utility.Utils;

import java.util.Collections;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        debug();
    }

    private static void debug() {
        Utils.loadData();
        //Utils.makeNewTagsAndPost();
        //Utils.getCloverTags();
        //Utils.postItems();
        Utils.getCloverItemList();
        Collections.reverse(Constants.cloverInventoryList.getObjectList());
        //Utils.printList(Constants.cloverInventoryList.getObjectList());
        System.out.println(Constants.cloverInventoryList.getObjectList().size());
        // Utils.linkItems();
        Utils.saveData();
    }
}