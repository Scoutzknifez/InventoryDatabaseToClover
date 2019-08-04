package Structures;

import Utility.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonPropertyOrder({
        "id",
        "hidden",
        "name",
        "code",
        "sku",
        "price",
        "priceType",
        "defaultTaxRates",
        "isRevenue",
        "modifiedTime"
})

@AllArgsConstructor @Getter @Setter
public class CloverItem implements Serializable {
    private String id = "";
    private boolean hidden = false;
    private String name;
    private String code;
    private String sku;
    private long price;
    private String priceType = "FIXED";
    private boolean defaultTaxRates = true;
    @JsonProperty(value="isRevenue")
    private boolean isRevenue = true;
    private long modifiedTime = 0;

    public CloverItem() {}

    public CloverItem(String name, String sku, String code, long price) {
        setName(name);
        setSku(sku);
        setCode(code);
        setPrice(price);
    }

    public CloverItem(String id, String name, String sku, String code, long price) {
        setId(id);
        setName(name);
        setSku(sku);
        setCode(code);
        setPrice(price);
    }

    public boolean equalsItem(Item item) {
        return (item.getName().equals(getName()) && item.getUpc().equals(getSku()));
    }

    @Override
    public String toString() {
        String returned = "";
        try {
            returned = Constants.OBJECT_MAPPER.writeValueAsString(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return returned;
    }
}