/**
 * Class that contains information pertaining to a set in the LERS algorithm including
 * list of attributes included, list of values included, list of elements covered, and list of
 * subsets.
 * @author Will James
 */

import java.util.ArrayList;

public class RuleSet {

    private ArrayList<String> attributeNames;
    private ArrayList<String> values;
    private ArrayList<String> elements;
    private ArrayList<RuleSet> subsets;
    private int count;

    public RuleSet(String attributeName, String value, String element) {
        attributeNames = new ArrayList<String>();
        values = new ArrayList<String>();
        elements = new ArrayList<String>();
        subsets = new ArrayList<RuleSet>();
        count = 0;
        attributeNames.add(attributeName);
        values.add(value);
        elements.add(element);
        subsets.add(this);
    }

    public RuleSet(ArrayList<String> attributeNames, ArrayList<String> values, ArrayList<String> elements) {
        subsets = new ArrayList<RuleSet>();
        this.attributeNames = attributeNames;
        this.values = values;
        this.elements = elements;
        count = 0;
        subsets.add(this);
    }

    public RuleSet(ArrayList<String> attributeNames, ArrayList<String> values) {
        subsets = new ArrayList<RuleSet>();
        this.attributeNames = attributeNames;
        this.values = values;
        elements = new ArrayList<String>();
        count = 0;
        subsets.add(this);
    }

    public RuleSet(RuleSet a, RuleSet b) {
        attributeNames = new ArrayList<String>();
        values = new ArrayList<String>();
        elements = new ArrayList<String>();
        subsets = new ArrayList<RuleSet>();
        subsets.add(this);
        subsets.addAll(a.getSubsets());
        subsets.addAll(b.getSubsets());
        if (subsets.size() <= (Math.pow(2, values.size()) - 1)) {
            for (int i = 0; i < subsets.size() - 1; i++) {
                for (int j = i + 1; j < subsets.size(); j++) {
                    RuleSet newSet = new RuleSet(subsets.get(i), subsets.get(j));
                    if (!subsets.contains(newSet)) subsets.add(newSet);
                }
            }
        }
        this.attributeNames.addAll(a.getAttributeNames());
        for (String s : b.getAttributeNames()) {
            if (!this.attributeNames.contains(s)) {
                this.attributeNames.add(s);
            }
        }
        this.values.addAll(a.getValues());
        for (String s : b.getValues()) {
            if (!this.values.contains(s)) {
                this.values.add(s);
            }
        }
        for (String s : a.getElements()) {
            if (b.getElements().contains(s)) {
                this.elements.add(s);
            }
        }
        count = this.elements.size();
    }

    /**
     * @return the subsets
     */
    public ArrayList<RuleSet> getSubsets() {
        return subsets;
    }

    /**
     * @return the attributeNames
     */
    public ArrayList<String> getAttributeNames() {
        return attributeNames;
    }

    /**
     * @param attributeNames the attributeNames to set
     */
    public void setAttributeNames(ArrayList<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public String getValuesString() {
        StringBuilder vString = new StringBuilder();
        for (int i = 0; i < this.values.size(); i++) {
            vString.append(this.values.get(i));
            if (i != this.values.size() - 1) vString.append("∧");
        }
        return vString.toString();
    }

    /**
     * @return the values
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    /**
     * @return the elements
     */
    public ArrayList<String> getElements() {
        return elements;
    }

    /**
     * @param elements the elements to set
     */
    public void setElements(ArrayList<String> elements) {
        this.elements = elements;
    }

    public void addElement(String element) {
        this.elements.add(element);
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount() {
        this.count++;
    }

    public boolean isSubset(RuleSet other) {
        for (String element : other.getElements()) {
            if (!this.elements.contains(element)){
                return false;
            }
        }
        return true;
    }

    public boolean sharesAttributes(RuleSet other) {
        for (String attribute : other.getAttributeNames()) {
            if (this.attributeNames.contains(attribute)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        RuleSet set = (RuleSet) obj;
        for (String s : set.values) {
            if (!this.values.contains(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getValuesString();
    }
}