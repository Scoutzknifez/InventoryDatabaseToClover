import Structures.CloverItem;
import Structures.CloverTag;
import Structures.CloverTagListResponseBody;
import Structures.RequestType;
import Utility.Constants;
import Utility.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.squareup.okhttp.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Main
{
    public static ArrayList<CloverTag> cloverTags = new ArrayList<>();

    public static void main(String[] args) {
        debug();
    }

    private static void debug() {
        getCloverTags();
        // jsonMessing();
        // getCloverItemList();
        // postItem();
        // postTag();

        for(CloverTag tag : Constants.tagList)
            System.out.println(tag);
    }

    private static void getCloverTags() {
        Request request = Utils.buildRequest(RequestType.GET, "tags");
        Response response = Utils.runRequest(request);
        if(response != null) {
            try {
                CloverTagListResponseBody cloverTagListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverTagListResponseBody.class);
                ArrayList<LinkedHashMap<String, String>> unparsedTagList = cloverTagListResponseBody.getElements();
                Constants.tagList.clear();
                for(LinkedHashMap<String, String> mapping : unparsedTagList) {
                    String id = mapping.get("id");
                    String name = mapping.get("name");
                    boolean showInReporting = Boolean.parseBoolean(mapping.get("showInReporting"));
                    Constants.tagList.add(new CloverTag(id, name, showInReporting));
                }
            } catch(Exception e) {
                System.out.println("Could not parse the clover tag list.");
                e.printStackTrace();
            }
        }

    }

    private static void postTag(CloverTag tag) {
        Request request = Utils.buildRequest(RequestType.POST, "tags", tag);
        Utils.runRequest(request);
    }

    private static void postItem(CloverItem item) {
        Request request = Utils.buildRequest(RequestType.POST, "items", item);
        Utils.runRequest(request);
    }

    private static void getCloverItemList() {
        Request request = Utils.buildRequest(RequestType.GET, "items");
        Utils.runRequest(request);
    }

    private static void jsonMessing() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // String itemAsString = "{\"name\":\"test_item\", \"sku\":\"1234\", \"code\":\"test_sku\", \"price\":1698}";
            CloverItem item = new CloverItem("test_item", "1234", "test_sku", 1698);
            // String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
            System.out.println(json);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}