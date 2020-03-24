
/**
 * Creates the graphical user interface and handles input to be sent
 * to the ActionRules extraction script.
 * @author Will James
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Font;
import javax.swing.filechooser.*;

public class ActionRulesInterface {

    private static ActionRules extractor;
    private static HashMap<String, ArrayList<String>> availableAttributes;
    private static JLabel dataFileStatus;
    private static JLabel namesFileStatus;
    private static JTextArea availableLabel;
    private static JButton namesOpenButton;
    private static JButton namesLoadButton;
    private static String delimiter;
    private static String selectedData;
    private static String selectedNames;
    private static String decisionAttribute;
    private static String selectedFrom;
    private static String selectedTo;
    private static JComboBox<String> attributeBox;
    private static JComboBox<String> valueFromBox;
    private static JComboBox<String> valueToBox;
    private static JTextField minSupportField;
    private static JTextField minConfidenceField;
    private static JTextArea outputLog;
    private static JList<String> stableSelector;
    private static DefaultListModel<String> stableListModel;
    private static List<String> stableAttributes;
    private static JButton generateButton;
    private static int outputCount;

    public static void main(String[] args) {

        // Instantiating Class to perform Action Rules extraction
        extractor = new ActionRules();
        outputCount = 0;

        // Creating GUI Frame
        JFrame frame = new JFrame("Action Rules Extractor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 700);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                extractor.closeFile();
                System.exit(0);
            }
        });

        // Creating GUI fonts
        Font font = new Font("Courier", Font.PLAIN, 12);
        Font filefont = new Font("Courier", Font.PLAIN, 11);
        Font boldfont = new Font("Courier", Font.BOLD, 14);
        Font smallfont = new Font("Courier", Font.BOLD, 10);

        // Creating grid layout, dividing GUI into a left side and right side
        JPanel layout = new JPanel();
        layout.setLayout(new BoxLayout(layout, BoxLayout.Y_AXIS));
        JPanel grid = new JPanel();
        grid.setMaximumSize(new Dimension(400, 500));
        grid.setLayout(new GridLayout(0, 2));
        JPanel leftpane = new JPanel();
        JPanel rightpane = new JPanel();
        leftpane.setLayout(new BoxLayout(leftpane, BoxLayout.Y_AXIS));
        rightpane.setLayout(new BoxLayout(rightpane, BoxLayout.Y_AXIS));

        // Creating panels to hold dataset file selection components
        JPanel dataFilePanel = new JPanel();
        JPanel dataButtonPanel = new JPanel();
        JPanel dataTitlePanel = new JPanel();
        JPanel delimiterPanel = new JPanel();
        dataFilePanel.setMaximumSize(new Dimension(200, 40));
        dataButtonPanel.setMaximumSize(new Dimension(200, 40));
        dataTitlePanel.setMaximumSize(new Dimension(200, 40));
        delimiterPanel.setMaximumSize(new Dimension(200, 40));
        dataFilePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dataButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dataTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        delimiterPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Creating panels to hold names file selection components
        JPanel namesFilePanel = new JPanel();
        JPanel namesButtonPanel = new JPanel();
        JPanel namesTitlePanel = new JPanel();
        JPanel blankTitlePanel2 = new JPanel();
        namesFilePanel.setMaximumSize(new Dimension(200, 40));
        namesButtonPanel.setMaximumSize(new Dimension(200, 40));
        namesTitlePanel.setMaximumSize(new Dimension(200, 40));
        blankTitlePanel2.setMaximumSize(new Dimension(200, 40));
        namesFilePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namesButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        namesTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Creating panels to hold attribute selection components
        JPanel attributesLabelPanel = new JPanel();
        JPanel availableLabelPanel = new JPanel();
        JPanel blankPanel1 = new JPanel();
        JPanel blankPanel2 = new JPanel();
        JPanel blankPanel3 = new JPanel();
        JPanel blankPanel4 = new JPanel();
        JPanel blankPanel5 = new JPanel();
        JPanel blankPanel6 = new JPanel();
        JPanel decisionAttributePanel = new JPanel();
        JPanel decisionValuePanel = new JPanel();
        JPanel decisionFromPanel = new JPanel();
        JPanel decisionToPanel = new JPanel();
        JPanel stableTitlePanel = new JPanel();
        JPanel stableAttributesPanel = new JPanel();
        attributesLabelPanel.setMaximumSize(new Dimension(200, 40));
        availableLabelPanel.setMaximumSize(new Dimension(200, 40));
        blankPanel1.setMaximumSize(new Dimension(200, 10));
        blankPanel2.setMaximumSize(new Dimension(200, 10));
        blankPanel3.setMaximumSize(new Dimension(200, 10));
        blankPanel4.setMaximumSize(new Dimension(200, 10));
        blankPanel5.setMaximumSize(new Dimension(200, 10));
        blankPanel6.setMaximumSize(new Dimension(200, 10));
        decisionAttributePanel.setMaximumSize(new Dimension(200, 80));
        decisionValuePanel.setMaximumSize(new Dimension(200, 80));
        decisionFromPanel.setMaximumSize(new Dimension(200, 40));
        decisionToPanel.setMaximumSize(new Dimension(200, 40));
        stableTitlePanel.setMaximumSize(new Dimension(200, 20));
        stableAttributesPanel.setMaximumSize(new Dimension(200, 140));
        attributesLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        availableLabelPanel.setLayout(new BorderLayout(10, 10));
        decisionAttributePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        decisionValuePanel.setLayout(new BoxLayout(decisionValuePanel, BoxLayout.Y_AXIS));
        decisionFromPanel.setLayout(new GridLayout(0, 2));
        decisionToPanel.setLayout(new GridLayout(0, 2));
        stableTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        stableAttributesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Creating panels for min confidence/support input
        JPanel minInputPanel = new JPanel();
        JPanel minSupportPanel = new JPanel();
        JPanel minConfidencePanel = new JPanel();
        minInputPanel.setMaximumSize(new Dimension(200, 80));
        minSupportPanel.setMaximumSize(new Dimension(200, 40));
        minConfidencePanel.setMaximumSize(new Dimension(200, 40));
        minInputPanel.setLayout(new BoxLayout(minInputPanel, BoxLayout.Y_AXIS));
        minSupportPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        minConfidencePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Creating panels for generating action rules button and output block
        JPanel actionRulesButtonPanel = new JPanel();
        JPanel logPanel = new JPanel();
        actionRulesButtonPanel.setMaximumSize(new Dimension(350, 40));
        logPanel.setMaximumSize(new Dimension(350, 110));
        actionRulesButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        logPanel.setLayout(new BorderLayout(10, 10));


        // Creating dataset file selection status labels and buttons
        dataFileStatus = new JLabel("No File Selected", SwingConstants.LEFT);
        dataFileStatus.setFont(filefont);
        JButton dataLoadButton = new JButton("Load");
        dataLoadButton.setFont(font);
        JButton dataOpenButton = new JButton("Open");
        dataOpenButton.setFont(font);
        JLabel dataLabel = new JLabel("Select Dataset File");
        dataLabel.setFont(boldfont);

        // Creating names file selection status label and buttons
        namesFileStatus = new JLabel("No File Selected", SwingConstants.LEFT);
        namesFileStatus.setFont(filefont);
        namesLoadButton = new JButton("Load");
        namesLoadButton.setFont(font);
        namesOpenButton = new JButton("Open");
        namesOpenButton.setFont(font);
        JLabel namesLabel= new JLabel("Select Attribute File");
        namesLabel.setFont(boldfont);
        namesOpenButton.setEnabled(false);
        namesLoadButton.setEnabled(false);
        
        // Creating and adding action listener to dataset file buttons
        DataFileChooser dfc = new DataFileChooser();
        dataLoadButton.addActionListener(dfc);
        dataOpenButton.addActionListener(dfc);

        // Creating and adding action listener to names file buttons
        NamesFileChooser nfc = new NamesFileChooser();
        namesLoadButton.addActionListener(nfc);
        namesOpenButton.addActionListener(nfc);

        // Creating label and combobox for delimiter selecter
        JLabel delimiterLabel = new JLabel("Delimiter: ");
        String[] delimiters = {",", "\\t", "|"};
        JComboBox<String> delimiterBox = new JComboBox<String>(delimiters);
        delimiterBox.setSelectedIndex(0);
        delimiterBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> combo = (JComboBox<String>) event.getSource();
                String selected = (String) combo.getSelectedItem();
                if (selected.equals("\\t")) selected = "\t";
                ActionRulesInterface.delimiter = selected;
            }
        });

        // Creating labels for available attribute display
        JLabel attributesLabel = new JLabel("Available Attributes:");
        attributesLabel.setFont(boldfont);
        availableLabel = new JTextArea("");
        availableLabel.setEditable(false);
        availableLabel.setLineWrap(true);
        availableLabel.setFont(font);

        // Creating labels and selectors for decision attribute value selection
        JLabel decisionAttributeLabel = new JLabel("Decision Attribute:     ");
        decisionAttributeLabel.setFont(font);
        JLabel decisionFromLabel = new JLabel("From: ");
        decisionFromLabel.setFont(font);
        JLabel decisionToLabel = new JLabel("To:      ");
        decisionToLabel.setFont(font);
        attributeBox = new JComboBox<String>();
        attributeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> combo = (JComboBox<String>) event.getSource();
                String selected = (String) combo.getSelectedItem(); 
                decisionAttribute = selected;
                ArrayList<String> values = availableAttributes.get(decisionAttribute);
                valueFromBox.removeAllItems();
                valueToBox.removeAllItems();
                valueFromBox.setEnabled(true);
                valueToBox.setEnabled(true);
                if (values != null) {
                    for (String s : values) {
                        valueFromBox.addItem(s);
                        valueToBox.addItem(s);
                    }
                }

                stableListModel.clear();
                Set<String> keys = availableAttributes.keySet();
                for (String attribute : keys) {
                    if (!attribute.equalsIgnoreCase(decisionAttribute)) {
                        stableListModel.addElement(attribute);
                    }
                }
                generateButton.setEnabled(true);
            }
        });
        attributeBox.setEnabled(false);
        valueFromBox = new JComboBox<String>();
        valueFromBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> combo = (JComboBox<String>) event.getSource();
                String selected = (String) combo.getSelectedItem(); 
                selectedFrom = selected;
            }
        });
        valueFromBox.setEnabled(false);
        valueToBox = new JComboBox<String>();
        valueToBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JComboBox<String> combo = (JComboBox<String>) event.getSource();
                String selected = (String) combo.getSelectedItem(); 
                selectedTo = selected;
            }
        });
        valueToBox.setEnabled(false);

        // Creating labels and text fields for min support and confidence input
        JLabel minSupportLabel = new JLabel("Min Support:               ");
        minSupportLabel.setFont(font);
        JLabel minConfidenceLabel = new JLabel("Min Confidence(%):  ");
        minConfidenceLabel.setFont(font);
        JTextField minSupportField = new JTextField(3);
        minSupportField.setFont(font);
        JTextField minConfidenceField = new JTextField(3);
        minConfidenceField.setFont(font);

        // Creating selector for stable attributes
        JLabel stableLabel = new JLabel("Select Stable Attributes");
        stableListModel = new DefaultListModel<String>();
        stableSelector = new JList<String>(stableListModel);
        stableSelector.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        stableSelector.addListSelectionListener(new ListSelectionListener(){
        
            @Override
            public void valueChanged(ListSelectionEvent e) {
                stableAttributes = stableSelector.getSelectedValuesList();
            }
        });

        // Creating generate action rules button
        generateButton = new JButton("Generate Action Rules");
        generateButton.setEnabled(false);
        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFrom.equalsIgnoreCase(selectedTo)) {
                    outputToGUI("Decision Values Cannot Be The Same", false);
                    return;
                }

                int minSupport = 1;
                int minConfidence = 1;
                try {
                    minSupport = Integer.parseInt(minSupportField.getText());
                    minConfidence = Integer.parseInt(minConfidenceField.getText());
                } catch (NumberFormatException error) {
                    outputToGUI("Invalid MinSupport AND/OR MinConfidence Values Input", false);
                }
                
                if (stableAttributes == null) {
                    stableAttributes = new ArrayList<String>();
                }

                extractor.LERS(decisionAttribute, minSupport, minConfidence);
                extractor.extractRules(stableAttributes, selectedFrom, selectedTo);
            }
        });

        // Creating output log text field
        outputLog = new JTextArea();
        outputLog.setFont(smallfont);
        //outputLog.setHorizontalAlignment(JTextField.CENTER);
        outputLog.setEditable(false);
        outputLog.setLineWrap(true);

        // Adding delimiter components to delimiter panel
        delimiterPanel.add(delimiterLabel);
        delimiterPanel.add(delimiterBox);

        // Adding dataset file selector components to related panels
        dataFilePanel.add(dataFileStatus);
        dataButtonPanel.add(dataOpenButton);
        dataButtonPanel.add(dataLoadButton);
        dataTitlePanel.add(dataLabel);

        // Adding names file selecter components to related panels
        namesFilePanel.add(namesFileStatus);
        namesButtonPanel.add(namesOpenButton);
        namesButtonPanel.add(namesLoadButton);
        namesTitlePanel.add(namesLabel);

        // Adding components for attribute selection
        attributesLabelPanel.add(attributesLabel);
        availableLabelPanel.add(availableLabel);
        decisionAttributePanel.add(decisionAttributeLabel);
        decisionAttributePanel.add(attributeBox);
        decisionFromPanel.add(decisionFromLabel);
        decisionFromPanel.add(valueFromBox);
        decisionToPanel.add(decisionToLabel);
        decisionToPanel.add(valueToBox);
        decisionValuePanel.add(decisionFromPanel);
        decisionValuePanel.add(decisionToPanel);

        // Adding components for min values input
        minSupportPanel.add(minSupportLabel);
        minSupportPanel.add(minSupportField);
        minConfidencePanel.add(minConfidenceLabel);
        minConfidencePanel.add(minConfidenceField);
        minInputPanel.add(minSupportPanel);
        minInputPanel.add(minConfidencePanel);

        // Adding button to generate action rules panel
        actionRulesButtonPanel.add(generateButton);

        // Adding output text field to output panel
        logPanel.add(outputLog);

        // Adding components for stable attribute selection
        stableTitlePanel.add(stableLabel);
        stableAttributesPanel.add(stableSelector);

        // Adding panels to the grid left and right sides
        leftpane.add(dataTitlePanel);
        rightpane.add(delimiterPanel);
        leftpane.add(dataFilePanel);
        rightpane.add(dataButtonPanel);
        leftpane.add(namesTitlePanel);
        rightpane.add(blankTitlePanel2);
        leftpane.add(namesFilePanel);
        rightpane.add(namesButtonPanel);
        leftpane.add(blankPanel1);
        rightpane.add(blankPanel2);
        leftpane.add(attributesLabelPanel);
        rightpane.add(availableLabelPanel);
        leftpane.add(blankPanel3);
        rightpane.add(blankPanel4);
        leftpane.add(decisionAttributePanel);
        rightpane.add(decisionValuePanel);
        leftpane.add(blankPanel5);
        rightpane.add(blankPanel6);
        leftpane.add(minInputPanel);
        rightpane.add(stableTitlePanel);
        rightpane.add(stableAttributesPanel);

        // Adding left and right panels to the grid
        grid.add(leftpane);
        grid.add(rightpane);

        layout.add(grid);
        layout.add(actionRulesButtonPanel);
        layout.add(logPanel);

        // Adding grid to main GUI frame and setting frame visible
        frame.add(layout);
        frame.setVisible(true);
    }

    /**
     * Outputs the given string to the JTextArea on the GUI.
     * @param output String to be output to the GUI.
     * @param clear If true, clear the GUI of its current contents before outputting.
     */
    public static void outputToGUI(String output, boolean clear) {
        if (outputLog != null) {
            if (clear) {
                outputLog.setText("");
                outputCount = 0;
            }
            if (outputCount != 0) outputLog.append("\n");
            outputLog.append(output);
            outputCount++;
        }
        if (outputCount >= 6) {
            outputLog.setText("");
            outputCount = 0;
        }
    }

    /**
     * Custom filechooser class for choosing dataset file
     */
    public static class DataFileChooser extends JFrame implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();

            if (action.equals("Load")) { // Load button behavior
                String selectedDataFile = dataFileStatus.getText();
                if (!selectedDataFile.equalsIgnoreCase("No File Selected") && selectedData != null) {
                    //System.out.println(selectedDataFile);
                    if (delimiter == null) delimiter = ",";
                     if (extractor.ReadDataFile(selectedData, delimiter)) {
                        outputToGUI(selectedDataFile + " Loaded", false);
                        namesOpenButton.setEnabled(true);
                        namesLoadButton.setEnabled(true);
                     } else {
                         dataFileStatus.setText("Invalid File Selected");
                         selectedData = null;
                     }
                }
            } else { // Open button behavior
                File workingDirectory = new File(System.getProperty("user.dir"));
                JFileChooser jfc = new JFileChooser(workingDirectory);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Dataset Files", "data");
                jfc.setFileFilter(filter);
                jfc.setDialogTitle("Select Dataset File");

                int response = jfc.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    dataFileStatus.setText(jfc.getSelectedFile().getName());
                    selectedData = jfc.getSelectedFile().getAbsolutePath();
                    namesOpenButton.setEnabled(false);
                    namesLoadButton.setEnabled(false);
                    attributeBox.removeAllItems();
                    attributeBox.setEnabled(false);
                    availableLabel.setText("");
                    valueFromBox.setEnabled(false);
                    valueToBox.setEnabled(false);
                    generateButton.setEnabled(false);
                } else {
                    outputToGUI("The User Cancelled The File Opening", false);
                }
            }
        }

    }

    /**
     * Custom filechooser class for choosing attribute names file
     */
    public static class NamesFileChooser extends JFrame implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();

            if (action.equals("Load")) { // Load button behavior
                String selectedNameFile = namesFileStatus.getText();
                if (!selectedNameFile.equalsIgnoreCase("No File Selected") && selectedNames != null) {
                    //System.out.println(selectedNameFile);
                    if (extractor.ReadNamesFile(selectedNames)) {
                        outputToGUI(selectedNameFile + " Loaded", false);
                        if (extractor.GenerateSets()) {
                            outputToGUI("Data table created", false);
                            availableAttributes = extractor.getAvailableAttributes();
                            Set<String> keys = availableAttributes.keySet();
                            String list = "";
                            for (String attribute : keys) {
                                list += attribute + ", ";
                                attributeBox.addItem(attribute);
                            }
                            list = list.substring(0, list.length() - 2);
                            availableLabel.setText(list);
                            attributeBox.setEnabled(true);
                        } else {
                            namesFileStatus.setText("Incompatible Files Selected");
                            dataFileStatus.setText("Incompatible Files Selected");
                            selectedData = null;
                            selectedNames = null;
                            namesOpenButton.setEnabled(false);
                            namesLoadButton.setEnabled(false);
                            attributeBox.removeAllItems();
                            attributeBox.setEnabled(false);
                            availableLabel.setText("");
                            valueFromBox.setEnabled(false);
                            valueToBox.setEnabled(false);
                            generateButton.setEnabled(false);
                        }
                    } else {
                        namesFileStatus.setText("Invalid File Selected");
                        selectedNameFile = null;
                    }
                }
            } else { // Open button behavior
                File workingDirectory = new File(System.getProperty("user.dir"));
                JFileChooser jfc = new JFileChooser(workingDirectory);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Attribute Names Files", "names");
                jfc.setFileFilter(filter);
                jfc.setDialogTitle("Select Names File");

                int response = jfc.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    namesFileStatus.setText(jfc.getSelectedFile().getName());
                    selectedNames = jfc.getSelectedFile().getAbsolutePath();
                    attributeBox.removeAllItems();
                    attributeBox.setEnabled(false);
                    availableLabel.setText("");
                } else {
                    outputToGUI("The User Cancelled The File Opening", false);
                }
            }
        }
    }
}