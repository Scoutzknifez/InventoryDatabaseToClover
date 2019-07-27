import Structures.CloverItem;
import Structures.CloverTag;
import Structures.RequestType;
import Utility.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;

import java.util.ArrayList;

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
    }

    private static void getCloverTags() {
        Request request = Utils.buildRequest(RequestType.GET, "tags", "");
        Response response = Utils.runRequest(request);

        if(response.code() != 200) {
            throw new RuntimeException("Fetch for tags failed with error code: " + response.code());
        }

        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void postTag() {
        try {
            // Make tag JSON object
            ObjectMapper mapper = new ObjectMapper();
            CloverTag tag = new CloverTag("Test_tag");
            String tagJSON = mapper.writeValueAsString(tag);

            // Make connection to clover and send the tag
            String url = ("https://api.clover.com:443/v3/merchants/JAMSHGJDTVP31/tags?access_token=b7aa9b85-79b9-e015-cb2a-b40a2956b929");
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), tagJSON);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                throw new RuntimeException("Tag post failed with error code: " + response.code());
            }

            System.out.println(response.body().string());

        } catch(Exception e) {

        }
    }

    private static void postItem() {
        try {
            // Make input for inventory
            ObjectMapper mapper = new ObjectMapper();
            CloverItem item = new CloverItem("test_item", "1234", "test_sku", 1698);
            String input = mapper.writeValueAsString(item);
            System.out.println(input);

            // Get the connection and set the request information
            String url = ("https://api.clover.com:443/v3/merchants/JAMSHGJDTVP31/items?access_token=b7aa9b85-79b9-e015-cb2a-b40a2956b929");
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), input);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                throw new RuntimeException("Fetch failed with error code: " + response.code());
            }

            System.out.println(response.body().string());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCloverItemList() {
        try {
            // Get the connection and set the request information
            String url = ("https://api.clover.com:443/v3/merchants/JAMSHGJDTVP31/items?access_token=b7aa9b85-79b9-e015-cb2a-b40a2956b929");

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                throw new RuntimeException("Fetch failed with error code: " + response.code());
            }

            System.out.println(response.body().string());

        } catch(Exception e) {
            e.printStackTrace();
        }
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