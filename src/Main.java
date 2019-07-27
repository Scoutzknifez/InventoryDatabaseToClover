import Structures.CloverItem;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main
{
    public static void main(String[] args) {
        debug();

    }

    private static void debug() {
        // jsonMessing();
        // getCloverItemList();
        postItem();
    }

    private static void postItem() {
        try {
            // Get the connection and set the request information
            URL url = new URL("https://api.clover.com:443/v3/merchants/JAMSHGJDTVP31/items?access_token=b7aa9b85-79b9-e015-cb2a-b40a2956b929");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");

            // Make input for inventory
            ObjectMapper mapper = new ObjectMapper();
            CloverItem item = new CloverItem("test_item", "1234", "test_sku", 1698);
            String input = mapper.writeValueAsString(item);
            System.out.println(input);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(input.getBytes());
            outputStream.flush();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed with error code: " + connection.getResponseCode());
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("Server responded with: ");
            String response;
            while((response = bufferedReader.readLine()) != null) {
                System.out.println(response);
            }

            connection.disconnect();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCloverItemList() {
        try {
            // Get the connection and set the request information
            URL url = new URL("https://api.clover.com:443/v3/merchants/JAMSHGJDTVP31/items?access_token=b7aa9b85-79b9-e015-cb2a-b40a2956b929");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Weed out the exceptions
            if(connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed the API call! Error code: " + connection.getResponseCode());
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("Server responded with: ");
            String response;
            while((response = bufferedReader.readLine()) != null) {
                System.out.println(response);
            }

            connection.disconnect();

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