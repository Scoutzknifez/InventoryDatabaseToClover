package Structures;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
public class CloverTagListResponseBody {
    private ArrayList<LinkedHashMap<String, String>> elements;
    private String href;
}