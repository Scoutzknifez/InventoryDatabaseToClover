import Structures.CloverItem;
import Structures.CloverTag;
import Structures.RequestType;
import Utility.Constants;
import Utility.Utils;
import com.squareup.okhttp.*;

import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        debug();

    }

    private static void debug() {
        CloverItem item = new CloverItem("test_item_with_label", "1234", "959", 1698);
        ArrayList<String> cloverTags = new ArrayList<>();
        cloverTags.add(Constants.tagList.get(0).getId());
        item.setTags(cloverTags);
        postItem(item);
        // jsonMessing();
        // getCloverItemList();
        // postItem();
        // postTag();
    }

    private static void printItemTag() {
        getCloverItemList();
    }

    private static void postTag(CloverTag tag) {
        Request request = Utils.buildRequest(RequestType.POST, "tags", tag);
        Utils.runRequest(request);
    }

    private static void postItem(CloverItem item) {
        Request request = Utils.buildRequest(RequestType.POST, "items", item);
        Response response = Utils.runRequest(request);
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCloverItemList() {
        Request request = Utils.buildRequest(RequestType.GET, "items/");
        Utils.runRequest(request);
    }
}