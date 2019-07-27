package Structures;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CloverTag {
    private String id;
    private String name;
    private boolean showInReporting = true;

    public CloverTag(String name) {
        setName(name);
    }
}