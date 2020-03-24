
/**
 * Handles ActionRules extraction
 * @author Will James
 */

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ActionRules {

    PrintStream output;
    ArrayList<String[]> dataset;
    ArrayList<String> attributeNames;
    ArrayList<HashMap<String, RuleSet>> attributes;
    ArrayList<Rule> certainRules;
    String decisionAttribute;
    int minSupport;
    int minConfidence;
    boolean hasOutput;

    /**
     * Class constructor, instantiates global variables.
     */
    public ActionRules() {
        dataset = new ArrayList<String[]>();
        attributeNames = new ArrayList<String>();
        attributes = new ArrayList<HashMap<String, RuleSet>>();
        certainRules = new ArrayList<Rule>();

        hasOutput = false;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'-'HH-mm-ss");
        LocalDateTime currenttime = LocalDateTime.now();
        try {
            String filename = format.format(currenttime);
            output = new PrintStream(new File("output\\" + filename + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ActionRulesInterface.outputToGUI("Error Creating Output File", false);
            ActionRulesInterface.outputToGUI("Output will be printed to the console instead", false);
        } catch (DateTimeException e2) {
            e2.printStackTrace();
        }
    }

    /**
     * Closes the output file on GUI close.
     */
    public void closeFile() {
        if (output != null) {
            System.out.println("Output file closed");
            output.close();
        }
    }

    /**
     * Generates Action Rules using the previously created Certain Rules.
     * @param stableAttributes - List of attributes that are not to be changed.
     * @param decisionFrom - Decision value to change.
     * @param decisionTo - Decision value to change to.
     */
    public void extractRules(List<String> stableAttributes, String decisionFrom, String decisionTo) {
        if (output != null) {
            output.println("\nStable Attributes: " + stableAttributes.toString());
            output.println("Reclassifying from " + decisionFrom + " to " + decisionTo);
        } else {
            System.out.println("\nStable Attributes: " + stableAttributes.toString());
            System.out.println("Reclassifying from " + decisionFrom + " to " + decisionTo);
        }

        if (certainRules.isEmpty()) {
            ActionRulesInterface.outputToGUI("\nNo Action Rules Found", true);
            if (output != null) {
                output.println("\nNo Action Rules Found");
            } else {
                System.out.println("\nNo Action Rules Found");
            }
            return;
        }

        ArrayList<Rule> fromList = new ArrayList<Rule>();
        ArrayList<Rule> toList = new ArrayList<Rule>();
        for (Rule rule : certainRules) {
            RuleSet decisionSet = rule.getRightSet();
            if (decisionSet.getValues().get(0).equalsIgnoreCase(decisionFrom)) {
                fromList.add(rule);
            } else {
                toList.add(rule);
            }
        }
        
        ArrayList<String> actionRules = new ArrayList<String>();
        for (Rule fromRule : fromList) {
            for (Rule toRule : toList) {
                ArrayList<String> fromAttributes = fromRule.getLeftSet().getAttributeNames();
                ArrayList<String> toAttributes = toRule.getLeftSet().getAttributeNames();
                ArrayList<String> fromValues = fromRule.getLeftSet().getValues();
                ArrayList<String> toValues = toRule.getLeftSet().getValues();
                ArrayList<String> actionStrings = new ArrayList<String>();
                ArrayList<String> stableStrings = new ArrayList<String>();
                ArrayList<String[]> lefthandFromComponents = new ArrayList<String[]>();
                ArrayList<String[]> lefthandToComponents = new ArrayList<String[]>();
                for (int i = 0; i < fromAttributes.size(); i++) {
                    if (i >= fromValues.size()) continue;
                    for (int j = 0; j < toAttributes.size(); j++) {
                        if (j >= toValues.size()) continue;
                        String fromAttr = fromAttributes.get(i);
                        String toAttr = toAttributes.get(j);
                        String fromValue = fromValues.get(i);
                        String toValue = toValues.get(j);
                        if (stableAttributes.contains(toAttr)) {
                            String s = "(" + toAttr + "=" + toValue + ")";
                            if (!stableStrings.contains(s)) {
                                stableStrings.add(s);
                                String[] toPair = {toAttr, toValue};
                                lefthandToComponents.add(toPair);
                            }
                        } else if (fromAttr.equalsIgnoreCase(toAttr) && !fromValue.equalsIgnoreCase(toValue)) {
                            String s = "(" + fromAttr + ", " + fromValue + "→" + toValue + ")";
                            if (!actionStrings.contains(s)) {
                                actionStrings.add(s);
                                String[] fromPair = {fromAttr, fromValue};
                                lefthandFromComponents.add(fromPair);
                                String[] toPair = {toAttr, toValue};
                                lefthandToComponents.add(toPair);
                            }
                        }
                    }
                }
                if (!actionStrings.isEmpty()) {
                    RuleSet z1 = getRuleSet(decisionAttribute, decisionFrom);
                    RuleSet z2 = getRuleSet(decisionAttribute, decisionTo);
                    RuleSet y1 = getRuleSet(lefthandFromComponents.get(0)[0], lefthandFromComponents.get(0)[1]);
                    RuleSet y2 = getRuleSet(lefthandToComponents.get(0)[0], lefthandToComponents.get(0)[1]);

                    for (int i = 1; i < lefthandFromComponents.size(); i++) {
                        RuleSet nextFromSet = getRuleSet(lefthandFromComponents.get(i)[0], lefthandFromComponents.get(i)[1]);
                        if (nextFromSet == null) continue;
                        y1 = new RuleSet(y1, nextFromSet);
                    }
                    for (int j = 1; j < lefthandToComponents.size(); j++) {
                        RuleSet nextToSet = getRuleSet(lefthandToComponents.get(j)[0], lefthandToComponents.get(j)[1]);
                        if (nextToSet == null) continue;
                        y2 = new RuleSet(y2, nextToSet);
                    }
                    if (z1 == null || z2 == null || y1 == null || y2 == null) continue;
                    RuleSet y1withz1 = new RuleSet(y1, z1);
                    RuleSet y2withz2 = new RuleSet(y2, z2);

                    int support = y1withz1.getCount();
                    double confidence = (y1withz1.getCount() / y1.getCount()) * (y2withz2.getCount() / y2.getCount()) * 100;
                    if (support < minSupport || confidence < minConfidence) continue;


                    StringBuilder newActionRule = new StringBuilder();
                    for (String action : actionStrings) {
                        newActionRule.append(action);
                        newActionRule.append(" ∧ ");
                    }
                    for (String stable : stableStrings) {
                        newActionRule.append(stable);
                        newActionRule.append(" ∧ ");
                    }
                    int length = newActionRule.length();
                    newActionRule.delete(length - 2, length);
                    newActionRule.append("→ ");
                    String classify = "(" + decisionAttribute + ", " + decisionFrom + "→" + decisionTo + ")";
                    newActionRule.append(classify);
                    String stats = "  (Supp: " + support + ", Conf: " + (int)confidence + "%)";
                    newActionRule.append(stats);
                    String actionString = newActionRule.toString();
                    if (!actionRules.contains(actionString)) actionRules.add(newActionRule.toString());
                }
            }
        }

        if (!actionRules.isEmpty()) {
            //ActionRulesInterface.outputToGUI("\nACTION RULES:", true);
            ActionRulesInterface.outputToGUI("\nAction Rules Generated!", true);
            if (output != null) {
                output.println("\nACTION RULES:");
                ActionRulesInterface.outputToGUI("See new output file for results", false);
            } else {
                System.out.println("\nACTION RULES:");
                ActionRulesInterface.outputToGUI("See console for results", false);
            }
            for (String actionRule : actionRules) {
                //ActionRulesInterface.outputToGUI(actionRule, false);
                if (output != null) {
                    output.println(actionRule);
                } else {
                    System.out.println(actionRule);
                }
            }
        } else {
            ActionRulesInterface.outputToGUI("\nNo Action Rules Found", true);
            if (output != null) {
                output.println("\nNo Action Rules Found");
            } else {
                System.out.println("\nNo Action Rules Found");
            }
        }

        if (output != null) {
            output.println();
            if (hasOutput) {
                ActionRulesInterface.outputToGUI("\nOutput file updated!", false);
            } else {
                ActionRulesInterface.outputToGUI("\nOutput file created!", false);
                hasOutput = true;
            }
        } else {
            System.out.println();
        }
    }

    /**
     * Uses the input dataset to perform the LERS algorithm for determining certain
     * and possible rules.
     * @param decisionAttribute - Attribute to generate rules around.
     * @param minSupport - Minimum threshhold for occurences of rules.
     * @param minConfidence - Minimum percentage for occurences of rules.
     */
    public void LERS(String decisionAttribute, int minSupport, int minConfidence) {
        this.decisionAttribute = decisionAttribute;
        this.minConfidence = minConfidence;
        this.minSupport = minSupport;

        if (output != null) {
            output.println("Decision Attribute: " + decisionAttribute);
            output.println("Minimum Support: " + minSupport);
            output.println("Minimum Confidence: " + minConfidence);
        } else {
            System.out.println("Decision Attribute: " + decisionAttribute);
            System.out.println("Minimum Support: " + minSupport);
            System.out.println("Minimum Confidence: " + minConfidence);
        }

        ArrayList<RuleSet> marked = new ArrayList<RuleSet>();
        ArrayList<RuleSet> decision = new ArrayList<RuleSet>();
        ArrayList<RuleSet> toCheck = new ArrayList<RuleSet>();
        certainRules.clear();

        for (int x = 0; x < attributes.size(); x++) {
            for (Map.Entry mapElement : attributes.get(x).entrySet()) {
                RuleSet set = (RuleSet) mapElement.getValue();
                if (set.getCount() < minSupport) continue;
                if (set.getAttributeNames().get(0).equalsIgnoreCase(decisionAttribute)) {
                    decision.add(set);
                } else {
                    toCheck.add(set);
                }
            }
        }

        System.out.println();
        int loopCount = 0;
        while (!toCheck.isEmpty()) {
            ArrayList<RuleSet> unmarked = new ArrayList<RuleSet>();
            loopCount++;
            if (output != null) {
                output.println("\nLOOP " + loopCount);
            } else {
                System.out.println("\nLOOP " + loopCount);
            }
            ArrayList<Rule> possibleRules = new ArrayList<Rule>();
            for (int i = 0; i < toCheck.size(); i++) {
                boolean mark = false;
                RuleSet toCheckSet = toCheck.get(i);
                if (toCheckSet.getCount() < minSupport) continue;
                for (int j = 0; j < decision.size(); j++) {
                    RuleSet decisionSet = decision.get(j);
                    if (decisionSet.isSubset(toCheckSet)) {
                        mark = true;
                        Rule newRule = new Rule(toCheckSet, decisionSet);
                        certainRules.add(newRule);
                    }
                }
                if (mark) {
                    marked.add(toCheckSet);
                    mark = false;
                } else {
                    for (RuleSet decisionSet : decision) {
                        Rule newRule = new Rule(toCheckSet, decisionSet);
                        if (newRule.getConfidence() >= minConfidence) {
                            possibleRules.add(newRule);
                        }
                        unmarked.add(toCheckSet);
                    }
                }
            }
            toCheck.clear();
            if (output != null) {
                if (certainRules.isEmpty()) {
                    output.println("NO CERTAIN RULES FOUND");
                } else {
                    output.println("\nCERTAIN RULES:");
                    for (Rule r : certainRules) {
                        output.println(r.toString());
                    }
                }
                
                if (possibleRules.isEmpty()) {
                    output.println("NO POSSIBLE RULES FOUND");
                } else {
                    output.println("\nPOSSIBLE RULES:");
                    for (Rule r : possibleRules) {
                        output.println(r.toString());
                    }
                }
            } else {
                if (certainRules.isEmpty()) {
                    System.out.println("NO CERTAIN RULES FOUND");
                } else {
                    System.out.println("\nCERTAIN RULES:");
                    for (Rule r : certainRules) {
                        System.out.println(r.toString());
                    }
                }
                
                if (possibleRules.isEmpty()) {
                    System.out.println("NO POSSIBLE RULES FOUND");
                } else {
                    System.out.println("\nPOSSIBLE RULES:");
                    for (Rule r : possibleRules) {
                        System.out.println(r.toString());
                    }
                }
            }
            

            
            for (int i = 0; i < unmarked.size() - 1; i++) {
                RuleSet set1 = unmarked.get(i);
                for (int j = i + 1; j < unmarked.size(); j++) {
                    ArrayList<RuleSet> set2Components = unmarked.get(j).getSubsets();
                    //if (loopCount == 2) System.out.println("SUBSETS OF " + unmarked.get(j).toString());
                    for (RuleSet subset : set2Components) {
                        //if (loopCount == 2) System.out.println(subset.toString());
                        if (!marked.contains(subset) && !set1.sharesAttributes(subset)) {
                            RuleSet combination = new RuleSet(set1, subset);
                            if (!toCheck.contains(combination)) {
                                ArrayList<RuleSet> comboSubsets = combination.getSubsets();
                                boolean skip = false;
                                for (RuleSet comboSubset : comboSubsets) {
                                    if (marked.contains(comboSubset)) skip = true;
                                }
                                if (!skip) toCheck.add(combination);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Takes the input dataset and creates sets according
     * each value's row position and count.
     */
    public boolean GenerateSets() {
        int attributeCount = attributeNames.size();
        attributes.clear();
        for (int i = 0; i < dataset.size(); i++) {
            String[] row = dataset.get(i);
            if (row.length != attributeCount) return false;
            for (int j = 0; j < attributeCount; j++) {
                if (i == 0) {
                    attributes.add(new HashMap<String, RuleSet>());
                }
                HashMap<String, RuleSet> dict = attributes.get(j);
                if (dict.containsKey(row[j])) {
                    RuleSet current = dict.get(row[j]);
                    current.incrementCount();
                    current.addElement("x" + (i + 1));
                } else {
                    RuleSet newSet = new RuleSet(attributeNames.get(j), row[j], "x" + (i + 1));
                    newSet.incrementCount();
                    dict.put(row[j], newSet);
                }
            }
        }
        
        return true;
    }

    /**
     * Returns a hashmap of each attribute as a key to the related attribute values.
     * @return
     */
    public HashMap<String, ArrayList<String>> getAvailableAttributes() {
        HashMap<String, ArrayList<String>> availAttributes = new HashMap<String, ArrayList<String>>();
        for (int i = 0; i < attributes.size(); i++) {
            for (Map.Entry mapElement : attributes.get(i).entrySet()) {
                RuleSet set = (RuleSet) mapElement.getValue();
                String key = set.getAttributeNames().get(0);
                if (availAttributes.containsKey(key)) {
                    availAttributes.get(key).add(set.getValues().get(0));
                } else {
                    ArrayList<String> values = new ArrayList<String>();
                    values.add(set.getValues().get(0));
                    availAttributes.put(key, values);
                }
            }
        }
        return availAttributes;
    }

    /**
     * Attempts to read from the input dataset file and parses each line of the file
     * into individual data values using the input delimiter. Stores the parsed dataset
     * into the dataset ArrayList.
     * @param filename - Name of the file to attempt to read from.
     * @param delimiter - Regex string to parse each line of the dataset with.
     * @return - True if the file was able to be successfully read, False otherwise.
     */
    public boolean ReadDataFile(String filename, String delimiter) {
        File file = new File(filename);
        dataset.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                boolean discard = false;
                String[] row = line.strip().split(delimiter);
                for (int i = 0; i < row.length; i++) {
                    if (row[i].equals("?") || row[i].isEmpty()) {
                        discard = true;
                    }
                }
                line = reader.readLine();
                if (discard) {
                    discard = false;
                    continue;
                }
                dataset.add(row);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Attempts to read from the input names file, storing each line as a
     * new attribute name in the attributeNames arraylist.
     * @param filename - Name of the file to attempt to read from.
     * @return - True if the file was able to be successfully read, False otherwise.
     */
    public boolean ReadNamesFile(String filename) {
        File file = new File(filename);
        attributeNames.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                attributeNames.add(line.strip());
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        // PRINTS
        //System.out.println(attributeNames.toString());
        return true;
    }

    /**
     * Searches for the initial RuleSet object related to the given
     * attribute name and value.
     * @param attributeName - Attribute name pertaining to the RuleSet in search of
     * @param value - Value pertaining to the RuleSet in search of
     * @return - The Ruleset object related to the given attribute name and value
     */
    public RuleSet getRuleSet(String attributeName, String value) {
        RuleSet set = null;
        for (int i = 0; i < attributeNames.size(); i++) {
            if (attributeNames.get(i).equalsIgnoreCase(attributeName)) {
                set = attributes.get(i).get(value);
            }
        }
        return set;
    }

    /**
     * Prints the currently stored dataset.
     */
    private void printDataset() {
        for (String[] row: dataset) {
            for (String s: row) {
                System.out.print(s + " ");
            }
            System.out.print("\n");
        }
    }
}

