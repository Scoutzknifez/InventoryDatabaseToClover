package Structures;

import Interfaces.Filterable;
import Utility.Constants;

import java.util.ArrayList;
import java.util.List;

public class ItemList
{
    private List<Object> itemList = new ArrayList<>();

    public ItemList() {

    }

    public ItemList(List<Object> inList) {
        itemList = inList;
    }

    public Object get(int index) {
        return itemList.get(index);
    }

    public Object getObject(String filter) {
        return filterList(filter).get(0);
    }

    public List<Object> getObjectList() {
        return itemList;
    }

    public void add(Object item) {
        itemList.add(item);
    }

    public void set(Object item, int index) {
        itemList.set(index, item);
    }

    public void remove(Object item) {
        itemList.remove(item);
    }

    public List<Object> filterList(String filter) {
        List<Object> subset = new ArrayList<>();
        for(Object item : itemList) {
            if(item instanceof Filterable) {
                if(((Filterable) item).containsFilter(filter))
                    subset.add(item);
            }
        }
        return subset;
    }

    public boolean contains(String string) {
        for(Object item : itemList) {
            if(item instanceof Filterable) {
                if(((Filterable) item).containsFilter(string))
                    return true;
            }
        }
        return false;
    }
}