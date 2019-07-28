package Structures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor @Getter @Setter
public class CloverItem {
    private String name;
    private String sku;
    private String code;
    private long price;
    private ArrayList<String> tags;
    private String priceType = "FIXED";
    private boolean defaultTaxRates = true;
    private boolean isRevenue = true;
    private boolean hidden = false;
    public CloverItem(String name, String sku, String code, long price) {
        setName(name);
        setSku(sku);
        setCode(code);
        setPrice(price);
    }
}