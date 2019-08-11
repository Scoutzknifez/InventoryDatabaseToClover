package Utility;

import Structures.*;
import Workers.WorkerHandler;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    public static void testUrl() {
        String[] args = new String[1];
        args[0] = "item_stocks/QARMPHXSQKJJC";
        // args[1] = makeFilterExactSku("730176357294");
        System.out.println(buildUrl(args));
        runRequest(buildRequest(RequestType.POST, getQuantityString(), args));
    }

    private static Object getQuantityString() {
        return "{\"quantity\":3}";
    }


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
    public static void printRequiredTags() {
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
            if(!Constants.tagList.contains(item.getBrand())) {
                if(!listOfTags.equalsIgnoreCase(""))
                    listOfTags += ", ";

                if(listOfTags.length() > 150 * lineCount) {
                    listOfTags += "\n";
                    lineCount++;
                }

                listOfTags += item.getBrand();
                brandToItemCount.put(item.getBrand(), 1);
            } else {
                if(brandToItemCount.get(item.getBrand()) == null)
                    brandToItemCount.put(item.getBrand(), 1);
                else {
                    int currentValue = brandToItemCount.get(item.getBrand()) + 1;
                    brandToItemCount.put(item.getBrand(), currentValue);
                }
            }
        }

        Map<String, Integer> sorted = sortByValue(brandToItemCount, false);
        for (String string : sorted.keySet()) {
            System.out.println(string + " - " + sorted.get(string));
        }
    }

    public static void syncItems() {
        sortCloverItemList();
        // Posts new items that didnt exist before
        postItems();
        sortCloverItemList();
        // Deletes items that were removed off of master
        deleteItems();
        sortCloverItemList();
        // updates changed items
        updateItems();
        sortCloverItemList();
    }

    private static void updateItems() {
        try {
            int index = 0;
            while(index < Constants.cloverInventoryList.getObjectList().size()) {
                CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(index);
                Item item = (Item) Constants.inventoryList.get(index);

                if(cloverItem.equalsSku(item)) {
                    if(cloverItem.needsUpdate(item)) {
                        // Lets update with new stuff
                        updateItem(cloverItem, item, index);
                    }
                } else {
                    System.out.println("Items are off sync I guess");
                }

                index++;
            }
        } catch (Exception e) {
            System.out.println("Could not update the items in clover.");
            e.printStackTrace();
        }
    }

    private static void updateItem(CloverItem cloverItem, Item item, int index) {
        if(Constants.OFFLINE_MODE)
            return;

        String[] args = new String[1];
        args[0] = "items/" + cloverItem.getId();
        CloverItemUpdateBody updatedCloverItemBody = new CloverItemUpdateBody(item.getName(), item.getProductCode(), makeLong(item.getPrice()));
        Request request = buildRequest(RequestType.POST, updatedCloverItemBody, args);
        Response response = runRequest(request);
        if(response != null) {
            System.out.println("Updating item:");
            System.out.println(cloverItem.getName() + " -> " + updatedCloverItemBody.getName());
            System.out.println(cloverItem.getCode() + " -> " + updatedCloverItemBody.getCode());
            System.out.println(cloverItem.getPrice() + " -> " + updatedCloverItemBody.getPrice());
            ((CloverItem) Constants.cloverInventoryList.getObjectList().get(index)).setName(item.getName());
            ((CloverItem) Constants.cloverInventoryList.getObjectList().get(index)).setCode(item.getProductCode());
            ((CloverItem) Constants.cloverInventoryList.getObjectList().get(index)).setPrice(makeLong(item.getPrice()));
        } else {
            System.out.println("Updating item failed.");
        }
    }

    public static void checkDuplicates() {
        int index = 0;
        while(index < Constants.cloverInventoryList.getObjectList().size() - 1) {
            CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(index);
            CloverItem nextCloverItem = (CloverItem) Constants.cloverInventoryList.get(index+1);
            if(cloverItem.equalsSku(nextCloverItem)) {
                // Lets delete the item (nextCloverItem) because its not unique
                deleteItem(nextCloverItem);
                System.out.println("Deleting item: " + nextCloverItem.getSku() + "> " + nextCloverItem.getName());
                Constants.cloverInventoryList.getObjectList().remove(index+1);
            } else {
                index++;
            }
        }
    }

    private static void deleteItems() {
        try {
            int index = 0;
            boolean isDeleting = true;
            while(isDeleting) {
                CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(index);
                Item item = (Item) Constants.inventoryList.get(index);

                if(!cloverItem.equalsSku(item)) {
                    deleteItem(cloverItem);
                } else {
                    index++;
                }

                if(Constants.cloverInventoryList.getObjectList().size() == index)
                    isDeleting = false;
            }
        } catch (Exception e) {
            System.out.println("Can not delete items from clover.");
            e.printStackTrace();
        }
    }

    private static void deleteItem(CloverItem cloverItem) {
        if(Constants.OFFLINE_MODE)
            return;

        if(cloverItem.getId().equalsIgnoreCase(""))
            throw new RuntimeException("Can not delete an item with not clover ID!");

        String[] args = new String[1];
        args[0] = "items/" + cloverItem.getId();
        Request request = buildRequest(RequestType.DELETE, args);
        Response response = runRequest(request);
        if(response != null) {
            Constants.cloverInventoryList.remove(cloverItem);
        }
    }

    public static void checkReverse() {
        try {
            CloverItem cloverItem = (CloverItem) Constants.cloverInventoryList.get(0);
            Item item = (Item) Constants.inventoryList.get(0);
            if(!cloverItem.equalsItem(item)) {
                Collections.reverse(Constants.cloverInventoryList.getObjectList());
            }
        } catch (Exception e) {
            System.out.println("Failed to check if needing reverse");
        }
    }

    public static void loadData() {
        try {
            ArrayList<LinkedHashMap<String, Object>> map = Constants.OBJECT_MAPPER.readValue(new FileInputStream("Data.json"), ArrayList.class);
            ArrayList<CloverItem> cloverItems = parseList(map);
            for(CloverItem cloverItem : cloverItems)
                Constants.cloverInventoryList.add(cloverItem);
        } catch (Exception e) {
            System.out.println("Could not load data file!");
            e.printStackTrace();
        }
    }

    private static void sortCloverItemList() {
        // Sort before saving file to json
        Constants.cloverInventoryList.getObjectList().sort((o1, o2) -> {
            try {
                CloverItem c1 = (CloverItem) o1;
                CloverItem c2 = (CloverItem) o2;

                int result = String.CASE_INSENSITIVE_ORDER.compare(c1.getSku(), c2.getSku());
                if(result == 0) {
                    result = c1.getSku().compareTo(c2.getSku());
                }
                return result;
            } catch (Exception e) {
                System.out.println("Could not compare clover items!");
                e.printStackTrace();
            }
            return 0;
        });
    }

    public static void saveData() {
        try {
            sortCloverItemList();

            ObjectWriter writer = Constants.OBJECT_MAPPER.writer(new DefaultPrettyPrinter());
            writer.writeValue(new File("Data.json"), Constants.cloverInventoryList.getObjectList());
        } catch (Exception e) {
            System.out.println("Could not make the data file!");
            e.printStackTrace();
        }
    }

    public static void postItems() {
        if(Constants.OFFLINE_MODE)
            return;

        for(int i = 0; i < Constants.inventoryList.getObjectList().size(); i++) {
            Object object = Constants.inventoryList.get(i);
            if (object instanceof Item) {
                Item item = (Item) object;
                if(!Constants.cloverInventoryList.contains(item.getUpc())) {
                    CloverItem cloverItem = new CloverItem(item.getName(), item.getUpc(), item.getProductCode(), makeLong(item.getPrice()));
                    postItem(cloverItem);
                    System.out.println("Posted item " + i);
                    System.out.println(cloverItem.getName());
                }
            }
        }
    }

    public static long makeLong(double d) {
        return ((long) (d * 100.000005));
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
            if(cloverTag != null && getTag(cloverItem) == null) {
                linkItemToLabel(cloverItem, cloverTag);
                System.out.println("Linked item " + cloverItem.getName() + " (" + cloverItem.getId() + ") with " + cloverTag.getName() + " (" + cloverTag.getId() + ")");
            }
        }
    }

    private static String getTag(CloverItem cloverItem) {
        String brand = null;
        if(cloverItem.getTags() != null) {
            try {
                LinkedHashMap<String, Object> mapping = (LinkedHashMap<String, Object>) cloverItem.getTags();
                ArrayList<LinkedHashMap<String, Object>> tagList = (ArrayList<LinkedHashMap<String, Object>>) mapping.get("elements");
                LinkedHashMap<String, Object> currentTag = tagList.get(0);
                brand = (String) currentTag.get("name");
            } catch (Exception e) {
                System.out.println("Item has no brand: " + cloverItem.getName());
            }
        }
        return brand;
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
            if(!Constants.tagList.contains(item.getBrand())) {
                if(!listOfTags.equalsIgnoreCase(""))
                    listOfTags += ", ";

                if(listOfTags.length() > 150 * lineCount) {
                    listOfTags += "\n";
                    lineCount++;
                }

                listOfTags += item.getBrand();
                brandToItemCount.put(item.getBrand(), 1);
            } else {
                if(brandToItemCount.get(item.getBrand()) == null)
                    brandToItemCount.put(item.getBrand(), 1);
                else {
                    int currentValue = brandToItemCount.get(item.getBrand()) + 1;
                    brandToItemCount.put(item.getBrand(), currentValue);
                }
            }
        }

        Map<String, Integer> sorted = sortByValue(brandToItemCount, false);

        List<CloverTag> tagList = new ArrayList<>();
        for (String string : sorted.keySet()) {
            System.out.println(string + " - " + sorted.get(string));
            tagList.add(new CloverTag(string));
        }


        System.out.println("Labels to create:");
        for (CloverTag tag : tagList) {
            if(!Constants.tagList.contains(tag.getName())) {
                System.out.println(tag.getName());
                postTag(tag);
            }
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
        Thread thread = new Thread(Utils::getCloverTags);
        thread.start();
        return thread;
    }

    public static Thread grabInventory() {
        Thread thread = WorkerHandler.fetchInventory();
        return thread;
    }

    public static CloverItem postItem(CloverItem item) {
        if(Constants.OFFLINE_MODE)
            return null;

        Request request = buildRequest(RequestType.POST, item, "items");
        Response response = runRequest(request);
        try {
            String body = response.body().string();
            CloverItem cloverItem = Constants.OBJECT_MAPPER.readValue(body , CloverItem.class);
            Constants.cloverInventoryList.add(cloverItem);
            return cloverItem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Item getItemFromIndex(int index) {
        int maxSize = Constants.inventoryList.getObjectList().size();
        if(index < maxSize)
            return ((Item) Constants.inventoryList.get(index));
        else
            return ((Item) Constants.inventoryList.get(maxSize - 1));
    }

    private static String getUpcOfIndex(int index) {
        return getItemFromIndex(index).getUpc();
    }

    public static void getCloverItemListPerfectly() {
        try {
            for(int i = 0; i < Constants.inventoryList.getObjectList().size(); i++) {
                String[] args = new String[3];
                args[0] = "items/";
                args[1] = "limit=1000";
                args[2] = makeFilterExactSku(getUpcOfIndex(i));
                Request request = buildRequest(RequestType.GET, args);
                Response response = runRequest(request);
                if(response != null) {
                    CloverItemListResponseBody cloverItemListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverItemListResponseBody.class);
                    ArrayList<LinkedHashMap<String, Object>> unparsedItemList = cloverItemListResponseBody.getElements();
                    ArrayList<CloverItem> items = parseList(unparsedItemList);
                    for(CloverItem cloverItem : items) {
                        Constants.cloverInventoryList.add(cloverItem);
                    }
                }
                System.out.println("Currently at item " + i + "/" + Constants.inventoryList.getObjectList().size());
            }
        } catch (Exception e) {
            System.out.println("Could not get the perfect list!");
            e.printStackTrace();
        }
        sortCloverItemList();
    }

    public static void getCloverItemListFaster() {
        try {
            int callsRequired = (Constants.inventoryList.getObjectList().size() / 1000) + 1;

            for(int i = 0; i < callsRequired; i++) {
                String[] args = new String[4];
                args[0] = "items/";
                args[1] = "limit=1000";
                args[2] = "expand=tags,itemStock";
                args[3] = "offset=" + (i * 1000);
                Request request = buildRequest(RequestType.GET, args);
                Response response = runRequest(request);
                if(response != null) {
                    CloverItemListResponseBody cloverItemListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverItemListResponseBody.class);
                    ArrayList<LinkedHashMap<String, Object>> unparsedItemList = cloverItemListResponseBody.getElements();
                    ArrayList<CloverItem> items = parseList(unparsedItemList);
                    for(CloverItem cloverItem : items) {
                        if(cloverItem.getSku() != null && !cloverItem.getSku().equals(""))
                            Constants.cloverInventoryList.add(cloverItem);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not make the clover item list!");
            e.printStackTrace();
        }
        sortCloverItemList();
    }

    private static ArrayList<CloverItem> parseList(ArrayList<LinkedHashMap<String, Object>> list) {
        ArrayList<CloverItem> cloverItems = new ArrayList<>();

        for(int i = list.size() - 1; i >= 0; i--) {
            LinkedHashMap<String, Object> mapping = list.get(i);

            String id = "";
            Object idObject = mapping.get("id");
            if(idObject instanceof String)
                id = (String) idObject;

            String name = "";
            Object nameObject = mapping.get("name");
            if(nameObject instanceof String)
                name = (String) nameObject;

            String sku = "";
            Object skuObject = mapping.get("sku");
            if(skuObject instanceof String)
                sku = (String) skuObject;

            String code = "";
            Object codeObject = mapping.get("code");
            if(codeObject instanceof String)
                code = (String) codeObject;

            Object obj = mapping.get("price");

            long price;
            if(obj instanceof Integer) {
                Integer integer = (Integer) obj;
                price = (long) integer;
            } else {
                String string = (String) obj;
                price = Long.parseLong(string);
            }

            Object tags = mapping.get("tags");
            Object itemStock = mapping.get("itemStock");

            try {
                cloverItems.add(new CloverItem(id, name, sku, code, price, tags, itemStock));
            } catch (Exception e) {
                System.out.println("Could not parse item into clover Item.");
            }
        }
        return cloverItems;
    }

    private static String makeFilterBySku(String sku) {
        return "filter=sku<=" + sku;
    }

    private static String makeFilterExactSku(String sku) {
        return "filter=sku=" + sku;
    }

    public static void linkItemToLabel(CloverItem item, CloverTag tag) {
        Request request = buildRequest(RequestType.POST, getItemLabelString(item, tag), "tag_items");
        Response response = runRequest(request);
        try {
            response.body().close();
        } catch (Exception e) {
            System.out.println("Could not close the response body.");
        }
    }

    public static void getCloverTags() {
        String[] args = new String[2];
        args[0] = "tags";
        args[1] = "limit=1000";
        Request request = buildRequest(RequestType.GET, args);
        Response response = runRequest(request);
        if(response != null) {
            try {
                CloverTagListResponseBody cloverTagListResponseBody = Constants.OBJECT_MAPPER.readValue(response.body().string(), CloverTagListResponseBody.class);
                ArrayList<LinkedHashMap<String, Object>> unparsedTagList = cloverTagListResponseBody.getElements();
                for(LinkedHashMap<String, Object> mapping : unparsedTagList) {

                    String id = "";
                    Object idObject = mapping.get("id");
                    if(idObject instanceof String)
                        id = (String) idObject;

                    String name = "";
                    Object nameObject = mapping.get("name");
                    if(nameObject instanceof String)
                        name = (String) nameObject;

                    boolean showInReporting = true;
                    Object showInReportingObject = mapping.get("showInReporting");
                    if(showInReportingObject instanceof Boolean)
                        showInReporting = (Boolean) showInReportingObject;

                    Constants.tagList.add(new CloverTag(id, name, showInReporting));
                }
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
                try {
                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(Constants.MILLIS_IN_SECOND);
                return runRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if(!isResponseValid(response)) {
                try {
                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    /**
     * Used for getters only
     * @param requestType always GET
     * @param apiSection arguments for type of get
     * @return request
     */
    private static Request buildRequest(RequestType requestType, String... apiSection) {
        return buildRequest(requestType, "", apiSection);
    }

    private static Request buildRequest(RequestType requestType, Object jsonable, String... apiSection) {
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
        else if(requestType == RequestType.DELETE) {
            builder = builder.delete();
        }

        builder = builder.header("accept", "application/json")
                .header("content-type", "application/json");

        return builder.build();
    }

    public static void postTag(CloverTag tag) {
        Request request = buildRequest(RequestType.POST, tag, "tags");
        runRequest(request);
    }

    public static void printList(List<Object> list) {
        for(Object object : list)
            System.out.println(object);
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

    private static Object getItemLabelString(CloverItem item, CloverTag tag) {
        return "{\"elements\":[{ \"item\":{\"id\":\"" + item.getId() + "\"}, \"tag\":{\"id\":\"" + tag.getId() + "\"} }]}";
    }

    private static String buildUrl(String... args) {
        String baseURL = Constants.WEBSITE_URL + Constants.MERCHANT_ID + "/" + args[0] + Constants.API_TOKEN;

        for (int i = 1; i < args.length; i++) {
            baseURL += "&" + args[i];
        }

        return baseURL;
    }
}