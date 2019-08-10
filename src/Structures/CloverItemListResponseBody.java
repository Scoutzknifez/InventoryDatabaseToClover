package Structures;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
public class CloverItemListResponseBody {
    private ArrayList<LinkedHashMap<String, Object>> elements;
    private String href;
}