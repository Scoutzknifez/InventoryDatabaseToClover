import Structures.CloverTag;
import Structures.Item;
import Utility.Constants;
import Utility.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Main
{
    public static void main(String[] args) {
        Utils.initialize();
        debug();
    }

    private static void debug() {
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

        for (String string : sorted.keySet()) {
            System.out.println(string + " - " + sorted.get(string));
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
}