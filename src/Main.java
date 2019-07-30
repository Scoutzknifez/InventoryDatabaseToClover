import Structures.CloverTag;
import Structures.Item;
import Utility.Constants;
import Utility.Utils;

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

        listOfTags = "";
        for(Object object : Constants.inventoryList.getObjectList()) {
            if(!listOfTags.equalsIgnoreCase(""))
                listOfTags += ", ";

            Item item = (Item) object;
            if(Constants.tagList.contains(item.getBrand()))
                listOfTags += item.getBrand();
        }

        System.out.println("Labels to create: " + listOfTags);
    }
}