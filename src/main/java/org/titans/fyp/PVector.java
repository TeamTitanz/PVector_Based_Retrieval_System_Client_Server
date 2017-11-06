package org.titans.fyp;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Buddhi on 9/18/2017.
 */
public class PVector {

    private StanfordLemmatizer slem;
    private String serializedFolderPath = System.getProperty("user.dir") + File.separator + "SerializedFolder";
    private HashMap<String, Double> tfList = new HashMap<String, Double>();
    private HashMap<String, Double> idfList = new HashMap<String, Double>();

    private PVector() {
        slem = StanfordLemmatizer.getInstance();
    }

    private static class InstanceHolder {
        static PVector instance = new PVector();
    }

    public static PVector getInstance() {
        return InstanceHolder.instance;
    }

    private HashMap<String, Double> getVocabularyBase() {
        HashMap<String, Double> vocabularyBase = null;
        try {
            File file = new File(serializedFolderPath + File.separator + "vocabularyBase.ser");
            if (file.exists()) {
//                System.out.println("vocabularyBase serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabularyBase = (HashMap<String, Double>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("VocabularyBase serialized file not found in " + serializedFolderPath
                        + File.separator + "vocabularyBase.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return vocabularyBase;
    }

    private List<String> getPWordList() {
        List<String> p_words_list = null;
        try {
            File file = new File(serializedFolderPath + File.separator + "p_list.ser");
            if (file.exists()) {
//                System.out.println("p_list serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                p_words_list = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
                System.out.println("p_list word serlized file not found in " + serializedFolderPath
                        + File.separator + "p_list.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return p_words_list;
    }

    private int mostCommonWordCount(List<String> list) {
        Map<String, Integer> map = new HashMap<>();

        for (String t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<String, Integer> max = null;

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
//        System.out.println(max.getKey()+", "+ max.getValue());
        return max.getValue();
    }

    private double calculateTf(String term, List<String> words) {
        double count = 0;
        for (String word : words) {
            if (term.equals(word)) {
                count = count + 1;
            }
        }
        double most_occurring_term_value = mostCommonWordCount(words);

        return 0.5 + (0.5 * (count / most_occurring_term_value));
    }

    private void scalingIdf() {
        double idfList_min = 1;
        double idfList_max = 0;
        for (Map.Entry<String, Double> entry : idfList.entrySet()) {
            double value = entry.getValue();
            if (idfList_min > value) {
                idfList_min = value;
            }

            if (idfList_max < value) {
                idfList_max = value;
            }
        }

        for (Map.Entry<String, Double> entry : idfList.entrySet()) {
            double current_value = entry.getValue();
            double scaled_value = 0.5 + 0.5 * ((current_value - idfList_min) / (idfList_max - idfList_min));
            idfList.put(entry.getKey(), scaled_value);
        }
    }

    public double[] normalizeVectors(double[] documentVector) {
        double sum = 0.0;
        for (int i = 0; i < documentVector.length; i++) {
            sum += documentVector[i];
        }
        for (int i = 0; i < documentVector.length; i++) {
            if (sum != 0.0) {
                documentVector[i] = documentVector[i] / sum;
            }
        }
        return documentVector;
    }

    public double[] calculatePVector(String paragraph) {

        idfList = getVocabularyBase();
        List<String> pWordsList = getPWordList();
        double[] queryVector = new double[2000];
        List<String> lemmatizedText = slem.lemmatize(paragraph);

        for (String word : pWordsList) {
            if (lemmatizedText.contains(word)) {
//                System.out.println("***" + word);
                double tfValue = calculateTf(word, lemmatizedText);
                tfList.put(word, tfValue);
            }
        }
        scalingIdf(); // Check ************************************

        int index = 0;
        for (String word : pWordsList) {
            if (lemmatizedText.contains(word)) {
                queryVector[index] = idfList.get(word) * tfList.get(word);
            }
            index++;
        }

        return normalizeVectors(queryVector); // Check ************************************
    }

    public String setPVector(String paragraph) {
        double[] p_vector = calculatePVector(paragraph);
        StringBuilder sb = new StringBuilder();
        for (double val : p_vector) {
            sb.append(val);
            sb.append(" ");
        }
        String sdata = sb.toString();
        return sdata.substring(0, sdata.length() - 1);
    }

    public static void main(String[] args) {

        String par = "I am gay. Me and my partner are married for 3 years now. " +
                "I gave birth to a baby with the help of a sperm donor. " +
                "However, birth registration officials refuse to issue the birth certificate with " +
                "the partner’s name as one of the parents, stating that it is legally prohibited to issue.";

        PVector pv = new PVector();
        System.out.println(pv.setPVector(par));

    }

}
