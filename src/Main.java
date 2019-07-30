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
        int lineCount = 1;
        for(Object object : Constants.inventoryList.getObjectList()) {
            Item item = (Item) object;
            if(!Constants.tagList.contains(item.getBrand()) && !listOfTags.contains(item.getBrand())) {
                if(!listOfTags.equalsIgnoreCase(""))
                    listOfTags += ", ";

                if(listOfTags.length() > 150 * lineCount) {
                    listOfTags += "\n";
                    lineCount++;
                }

                listOfTags += item.getBrand();
            }
        }

        System.out.println("Labels to create: " + listOfTags);
    }
}