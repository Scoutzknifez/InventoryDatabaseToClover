package Structures;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class CloverItemUpdateBody {
    private String name;
    private String code;
    private long price;
}