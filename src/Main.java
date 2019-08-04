import Structures.CloverItem;
import Structures.CloverTag;
import Structures.Item;
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
        postItems();
        Utils.getCloverItemList();
        Collections.reverse(Constants.cloverInventoryList.getObjectList());
        linkItems();
    }

    private static void postItems() {
        for(int i = 0; i < 10; i++) {
            Object object = Constants.inventoryList.get(i);
            if (object instanceof Item) {
                Item item = (Item) object;
                CloverItem cloverItem = new CloverItem(item.getName(), item.getUpc(), item.getProductCode(), ((long) (item.getPrice() * 100)));
                Utils.postItem(cloverItem);
            }
        }
    }

    private static void linkItems() {
        for(int i = 0; i < 10; i++) {
            CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(i);
            CloverTag cloverTag = null;
            for(Object object : Constants.inventoryList.getObjectList()) {
                if(object instanceof Item) {
                    Item item = (Item) object;
                    if(cloverItem.equalsItem(item)) {
                        cloverTag = Utils.getItemsTag(item);
                    }
                }
            }
            if(cloverTag != null)
                Utils.linkItemToLabel(cloverItem, cloverTag);
        }
    }
}