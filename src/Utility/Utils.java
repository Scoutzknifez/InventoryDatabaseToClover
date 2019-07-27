package Utility;

import Structures.RequestType;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

;

public class Utils {
    public static Response runRequest(Request request) {
        try {
            return Constants.HTTP_CLIENT.newCall(request).execute();
        } catch (Exception e) {
            return null;
        }
    }

    public static Request buildRequest(RequestType requestType, String apiSection, String bodyJSONString) {
        Request.Builder builder = new Request.Builder();
        builder = builder.url(buildUrl(apiSection));

        if(requestType == RequestType.GET)
            builder = builder.get();
        else if(requestType == RequestType.POST) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/parse"), bodyJSONString);
            builder = builder.post(requestBody);
        }

        builder = builder.header("accept", "application/json")
                .header("content-type", "application/json");

        return builder.build();
    }

    private static String buildUrl(String type) {
        return Constants.WEBSITE_URL + Constants.MERCHANT_ID + "/" + type + Constants.API_TOKEN;
    }
}