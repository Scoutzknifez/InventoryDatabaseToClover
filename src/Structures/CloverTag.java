package Structures;

import Interfaces.Filterable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class CloverTag implements Filterable {
    private String id;
    private String name;
    private boolean showInReporting = true;

    public CloverTag(String name) {
        setName(name);
    }

    public boolean containsFilter(String string) {
        string = string.trim();
        return id.equalsIgnoreCase(string) || id.equalsIgnoreCase(name);
    }

    @Override
    public String toString() {
        return "<| Clover Tag |> \n" +
                "ID: " + getId() + "\n" +
                "Name: " + getName() + "\n" +
                "Show in Reporting: " + isShowInReporting() + "\n" +
                "----------------";
    }
}