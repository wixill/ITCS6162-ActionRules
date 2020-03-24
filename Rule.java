/**
 * Class that represents a certain or possible rule consisting
 * of a left side and right side
 * @author Will James
 */

public class Rule {

    private RuleSet leftSet;
    private RuleSet rightSet;
    private float confidence;

    public Rule(RuleSet leftSet, RuleSet rightSet) {
        this.leftSet = leftSet;
        this.rightSet = rightSet;
        RuleSet intersectSet = new RuleSet(leftSet, rightSet);
        this.confidence = (float)intersectSet.getCount() / (float)leftSet.getCount() * 100;
    }

    /**
     * @return the confidence
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * @return the leftSet
     */
    public RuleSet getLeftSet() {
        return leftSet;
    }

    /**
     * @return the rightSet
     */
    public RuleSet getRightSet() {
        return rightSet;
    }

    @Override
    public String toString() {
        return this.leftSet.getValuesString() + "→" + this.rightSet.getValuesString() + " " + (int)this.confidence + "%";
    }
}