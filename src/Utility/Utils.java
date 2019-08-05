package Utility;

import Structures.*;
import Workers.WorkerHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static void initialize() {
        Thread cloverTagFetcherThread = grabCloverTags();
        Thread sqlFetcherThread = grabInventory();

        try {
            cloverTagFetcherThread.join();
            System.out.println("Tag List Size: " + Constants.tagList.getObjectList().size());
            sqlFetcherThread.join();
            System.out.println("Item List Size: " + Constants.inventoryList.getObjectList().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postItems() {
        for(int i = 0; i < Constants.inventoryList.getObjectList().size(); i++) {
            Object object = Constants.inventoryList.get(i);
            if (object instanceof Item) {
                Item item = (Item) object;
                double price = (item.getPrice() * 100.000005);
                CloverItem cloverItem = new CloverItem(item.getName(), item.getUpc(), item.getProductCode(), ((long) price));
                postItem(cloverItem);
                System.out.println("Posted item " + i);
                System.out.println(cloverItem.getName());
            }
        }
    }

    public static void linkItems() {
        for(int i = 0; i < Constants.cloverInventoryList.getObjectList().size(); i++) {
            CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(i);
            CloverTag cloverTag = null;
            for(Object object : Constants.inventoryList.getObjectList()) {
                if(object instanceof Item) {
                    Item item = (Item) object;
                    if(cloverItem.equalsItem(item)) {
                        cloverTag = getItemsTag(item);
                    }
                }
            }
            if(cloverTag != null) {
                linkItemToLabel(cloverItem, cloverTag);
                System.out.println("Linked item " + i);
            }
        }
    }

    public static CloverTag getItemsTag(Item item) {
        for (Object object : Constants.tagList.getObjectList()) {
            if (object instanceof CloverTag) {
                CloverTag cloverTag = (CloverTag) object;
                if (cloverTag.getName().equals(item.getBrand())) {
                    return cloverTag;
                }
            }
        }
        return new CloverTag("N/A");
    }

    public static void makeNewTagsAndPost() {
        String listOfTags = "";
        for(Object object : Constants.tagList.getObjectList()) {
            if(!listOfTags.equalsIgnoreCase(""))
                listOfTags += ", ";
            CloverTag cloverTag = (CloverTag) object;
            listOfTags += cloverTag.getName();
        }
        System.out.println("Current Labels: " + listOfTags);

        HashMap<String, Integer> brandToItemCount = new HashMap<>();
        listOfTags = "";
        int lineCount = 1;
        for(Object object : Constants.inventoryList.getObjectList()) {
            Item item = (Item) object;
            if(!Constants.tagList.contains(item.getBrand()) && !brandToItemCount.keySet().contains(item.getBrand())) {
                if(!listOfTags.equalsIgnoreCase(""))
                    listOfTags += ", ";

                if(listOfTags.length() > 150 * lineCount) {
                    listOfTags += "\n";
                    lineCount++;
                }

                listOfTags += item.getBrand();
                brandToItemCount.put(item.getBrand(), 1);
            } else {
                int currentValue = brandToItemCount.get(item.getBrand()) + 1;
                brandToItemCount.put(item.getBrand(), currentValue);
            }
        }

        System.out.println("Labels to create:");
        // System.out.println(listOfTags);
        Map<String, Integer> sorted = sortByValue(brandToItemCount, false);

        List<CloverTag> tagList = new ArrayList<>();
        for (String string : sorted.keySet()) {
            System.out.println(string + " - " + sorted.get(string));
            tagList.add(new CloverTag(string));
        }

        for (CloverTag tag : tagList) {
            postTag(tag);
        }
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order)
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    public static Thread grabCloverTags() {
        Thread thread = new Thread(() -> getCloverTags());
        thread.start();
        return thread;
    }

    public static Thread grabInventory() {
        Thread thread = WorkerHandler.fetchInventory();
        return thread;
    }

    public static CloverItem postItem(CloverItem item) {
        Request request = buildRequest(RequestType.POST, "items", item);
        Response response = runRequest(request);
        try {
            String body = response.body().string();
            CloverItem cloverItem = Constants.OBJECT_MAPPER.readValue(body , CloverItem.class);
            return cloverItem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getCloverItemList() {
        Request request = buildRequest(RequestType.GET, "items/");
        Response response = runRequest(request);
        if(response != null) {
            try {
                CloverItemListResponseBody cloverItemListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverItemListResponseBody.class);
                ArrayList<LinkedHashMap<String, String>> unparsedTagList = cloverItemListResponseBody.getElements();
                ArrayList<Object> itemList = new ArrayList<>();
                for(LinkedHashMap<String, String> mapping : unparsedTagList) {
                    String id = mapping.get("id");
                    String name = mapping.get("name");
                    String sku = mapping.get("sku");
                    String code = mapping.get("code");
                    long price = Long.parseLong(mapping.get("price"));
                    itemList.add(new CloverItem(id, name, sku, code, price));
                }
                Constants.cloverInventoryList = new ItemList(itemList);
            } catch (Exception e) {
                System.out.println("Could not parse the clover items into a list.");
                e.printStackTrace();
            }
        }
    }

    public static void linkItemToLabel(CloverItem item, CloverTag tag) {
        String requestString = getItemLabelString(item, tag);
        Request request = buildRequest(RequestType.POST, "tag_items", requestString);

        printResponseBody(runRequest(request));
    }

    public static void getCloverTags() {
        Request request = buildRequest(RequestType.GET, "tags");
        Response response = runRequest(request);
        if(response != null) {
            try {
                CloverTagListResponseBody cloverTagListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverTagListResponseBody.class);
                ArrayList<LinkedHashMap<String, String>> unparsedTagList = cloverTagListResponseBody.getElements();
                ArrayList<Object> tagList = new ArrayList<>();
                for(LinkedHashMap<String, String> mapping : unparsedTagList) {
                    String id = mapping.get("id");
                    String name = mapping.get("name");
                    boolean showInReporting = Boolean.parseBoolean(mapping.get("showInReporting"));
                    tagList.add(new CloverTag(id, name, showInReporting));
                }
                Constants.tagList = new ItemList(tagList);
            } catch(Exception e) {
                System.out.println("Could not parse the clover tag list.");
                e.printStackTrace();
            }
        }
    }

    public static Response runRequest(Request request) {
        Response response = callRequest(request);

        if(isError429(response)) {
            try {
                System.out.println("Calling back in a second. Currently at max connections.");
                Thread.sleep(Constants.MILLIS_IN_SECOND);
                return runRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(!isResponseValid(response)) {
                throw makeResponseError(response);
            }

            return response;
        }
        return null;
    }

    private static Response callRequest(Request request) {
        try {
            return Constants.HTTP_CLIENT.newCall(request).execute();
        } catch (Exception e) {
            return null;
        }
    }

    private static Request buildRequest(RequestType requestType, String apiSection) {
        return buildRequest(requestType, apiSection, "");
    }

    private static Request buildRequest(RequestType requestType, String apiSection, Object jsonable) {
        Request.Builder builder = new Request.Builder();
        String url = buildUrl(apiSection);
        builder = builder.url(url);

        if(requestType == RequestType.GET)
            builder = builder.get();
        else if(requestType == RequestType.POST) {
            String jsonString = "";
            try {
                if(!(jsonable instanceof String))
                    jsonString = Constants.OBJECT_MAPPER.writeValueAsString(jsonable);
                else
                    jsonString = (String) jsonable;
            } catch (Exception e) {
                System.out.println("Could not make the string for " + jsonable);
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), jsonString);
            builder = builder.post(requestBody);
        }

        builder = builder.header("accept", "application/json")
                .header("content-type", "application/json");

        return builder.build();
    }

    public static void postTag(CloverTag tag) {
        Request request = buildRequest(RequestType.POST, "tags", tag);
        runRequest(request);
    }

    public static void printTags() {
        for(Object tag : Constants.tagList.getObjectList())
            System.out.println(tag);
    }

    private static void printResponseBody(Response response) {
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            System.out.println("Could not print the response body.");
        }
    }

    private static boolean isError429(Response response) {
        return response.code() == 429;
    }

    private static boolean isResponseValid(Response response) {
        return (response != null && response.code() == 200);
    }

    private static RuntimeException makeResponseError(Response response) {
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            System.out.println("Can not print out the response body of error.");
        }
        return new RuntimeException("Response came back with error code: " + response.code());
    }

    private static String getItemLabelString(CloverItem item, CloverTag tag) {
        return "{\"elements\":[{ \"item\":{\"id\":\"" + item.getId() + "\"}, \"tag\":{\"id\":\"" + tag.getId() + "\"} }]}";
    }

    private static String buildUrl(String type) {
        return Constants.WEBSITE_URL + Constants.MERCHANT_ID + "/" + type + Constants.API_TOKEN;
    }
}