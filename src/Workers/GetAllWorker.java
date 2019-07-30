package Workers;


import Structures.Item;
import Structures.ItemList;
import Utility.Constants;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetAllWorker extends WorkerParent implements Runnable{
    private List<Object> items;

    public GetAllWorker() {
        super();
    }
    @Override
    public void run() {
        if(statement == null)
            return;

        items = getAll();
        Constants.inventoryList = new ItemList(items);
        if(items == null) {
            System.out.println("Item list that was fetched is null.");
        }

        closeConnection();
    }
    private List<Object> getAll() {
        String sqlArg = "SELECT * FROM physical_inventory";
        try {
            return putResultIntoList(statement.executeQuery(sqlArg));
        } catch(Exception e) {
            System.out.println("Failed to fetch item list from database.");
            return null;
        }
    }
    private List<Object> putResultIntoList(ResultSet set) {
        List<Object> fetchedList = new ArrayList<>();
        try {
            while (set.next()) {
                String upc = set.getString("UPC");
                String itemName = set.getString("Name");
                double price = set.getDouble("Price");
                int quantity = set.getInt("Quantity");
                String brand = set.getString("Brand");
                String productCode = set.getString("product_code");
                Item thisItem = new Item(upc, productCode, itemName, brand, price, quantity, "", "", "", true);
                fetchedList.add(thisItem);
            }
            return fetchedList;
        } catch(Exception e) {
            System.out.println("Could not put result set into item list.");
            return null;
        }
    }
    public List<Object> getItems() {
        return items;
    }
}