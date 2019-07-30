import Structures.CloverItem;
import Structures.CloverTag;
import Utility.Constants;
import Utility.Utils;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        debug();
    }

    private static void debug() {
        CloverItem item = new CloverItem("test_item_with_label", "1234", "959", 1698);

        System.out.println(item);

        CloverItem responseItem = Utils.postItem(item);

        for(CloverTag tag : Constants.tagList)
            Utils.linkItemToLabel(responseItem, tag);
    }
}