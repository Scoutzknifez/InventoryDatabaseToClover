import Utility.Constants;
import Utility.Utils;

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
        Utils.getCloverItemListFaster();
        Utils.checkReverse();
        //Utils.printList(Constants.cloverInventoryList.getObjectList());
        System.out.println(Constants.cloverInventoryList.getObjectList().size());
        // Utils.linkItems();
        Utils.saveData();
    }
}