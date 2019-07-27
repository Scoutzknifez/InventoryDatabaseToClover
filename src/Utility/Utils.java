package Utility;

import Structures.RequestType;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Utils {
    public static Response runRequest(Request request) {
        Response response = Utils.callRequest(request);

        if(!Utils.isResponseValid(response))
            throw Utils.makeResponseError(response);

        return response;
    }

    private static Response callRequest(Request request) {
        try {
            return Constants.HTTP_CLIENT.newCall(request).execute();
        } catch (Exception e) {
            return null;
        }
    }

    public static Request buildRequest(RequestType requestType, String apiSection) {
        return buildRequest(requestType, apiSection, "");
    }

    public static Request buildRequest(RequestType requestType, String apiSection, Object jsonable) {
        Request.Builder builder = new Request.Builder();
        builder = builder.url(buildUrl(apiSection));

        if(requestType == RequestType.GET)
            builder = builder.get();
        else if(requestType == RequestType.POST) {
            String jsonString = "";
            try {
                jsonString = Constants.OBJECT_MAPPER.writeValueAsString(jsonable);
            } catch (Exception e) {
                System.out.println("Could not make the string for " + jsonable);
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/parse"), jsonString);
            builder = builder.post(requestBody);
        }

        builder = builder.header("accept", "application/json")
                .header("content-type", "application/json");

        return builder.build();
    }

    private static void printResponseBody(Response response) {
        try {
            System.out.println(response.body().string());
        } catch (Exception e) {
            System.out.println("Could not print the response body.");
        }
    }

    private static boolean isResponseValid(Response response) {
        return (response != null && response.code() == 200);
    }

    private static RuntimeException makeResponseError(Response response) {
        return new RuntimeException("Response came back with error code: " + response.code());
    }

    private static String buildUrl(String type) {
        return Constants.WEBSITE_URL + Constants.MERCHANT_ID + "/" + type + Constants.API_TOKEN;
    }
}