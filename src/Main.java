import Structures.CloverItem;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main
{
    public static void main(String[] args) {
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