package AssociationRules;

import java.util.*; 

public class ItemSet {
    ArrayList<Integer> items;

    public ItemSet(ArrayList<Integer> items) {
        this.items = new ArrayList<>(items);
        Collections.sort(this.items);
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public boolean containsAll(List<Integer> otherItems) {
        return items.containsAll(otherItems);
    }

    public boolean equals(Object o) {
        if (!(o instanceof ItemSet)) return false;
        return items.equals(((ItemSet) o).items);
    }

    public int hashCode() {
        return items.hashCode();
    }

    public String toString() {
        return items.toString();
    }
}
