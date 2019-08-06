package Structures;

import Interfaces.Filterable;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ItemList
{
    @Setter
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

    public boolean contains(CloverItem cloverItem) {
        for(Object object : itemList) {
            if(object instanceof CloverItem) {
                CloverItem inListItem = (CloverItem) object;
                if(inListItem.getSku().equalsIgnoreCase(cloverItem.getSku()))
                    return true;
            }
        }

        return false;
    }

    public CloverItem getCloverItem(Object object) {
        if(object instanceof String) {
            String sku = (String) object;
            for(Object objectItem : itemList) {
                if(objectItem instanceof CloverItem) {
                    CloverItem inListItem = (CloverItem) objectItem;
                    if(inListItem.getSku().equalsIgnoreCase(sku))
                        return inListItem;
                }
            }
        }

        if(object instanceof CloverItem) {
            CloverItem cloverItem = (CloverItem) object;
            for(Object objectItem : itemList) {
                if(objectItem instanceof CloverItem) {
                    CloverItem inListItem = (CloverItem) objectItem;
                    if(inListItem.getSku().equalsIgnoreCase(cloverItem.getSku()))
                        return inListItem;
                }
            }
        }
        return null;
    }
}