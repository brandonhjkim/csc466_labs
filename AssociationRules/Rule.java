package AssociationRules;

import java.util.*;

public class Rule {
    ItemSet left, right;

    public Rule(ItemSet left, ItemSet right) {
        this.left = left;
        this.right = right;
    }

    public ItemSet getLeft() {
        return left;
    }

    public ItemSet getRight() {
        return right;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Rule)) return false;
        Rule r = (Rule) o;
        return left.equals(r.left) && right.equals(r.right);
    }

    public int hashCode() {
        return Objects.hash(left, right);
    }

    public String toString() {
        return left + "->" + right;
    }
}
