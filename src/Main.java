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
        Utils.makeNewTagsAndPost();
        Utils.getCloverTags();
        Utils.postItems();
        Utils.getCloverItemList();
        Collections.reverse(Constants.cloverInventoryList.getObjectList());
        Utils.linkItems();
    }
}