package org.titans.fyp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Buddhi on 9/18/2017.
 */
public class PVector {

    public String serialized_folder = System.getProperty("user.dir") + File.separator + "Serialized_folder";
    private List<String> pWordList;
    private HashSet<String> vocabulary;
    private double[][] t_matrix;
    private StanfordLemmatizer slem;

    private PVector() {
        pWordList = getPWordList();
        vocabulary = getVocabulary();
        t_matrix = getTMatrix();
        slem = StanfordLemmatizer.getInstance();
    }

    private static class InstanceHolder {
        static PVector instance = new PVector();
    }

    public static PVector getInstance() {
        return InstanceHolder.instance;
    }


    public static int getIndex(HashSet<String> set, String value) {
        int result = 0;
        for (String entry : set) {
            if (entry.equals(value)) {
                return result;
            }
            result++;
        }
        return result;
    }

    public List<Double> docVector(String paragraph, List<String> inputWordList, HashSet<String> vocabulary,
                                  double[][] t_matrix) {

        List<Double> docVector = new ArrayList<Double>();
        List<String> lemmatized_text = slem.lemmatize(paragraph);
        String lemmatized_paragraph = String.join(" ", lemmatized_text);

        for (String word : inputWordList) {
            if (lemmatized_paragraph.contains(word)) {
                int index = getIndex(vocabulary, word);
                try {
                    docVector.add(t_matrix[index][0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                docVector.add(0.0);
            }
        }
        return docVector;
    }

    private List<String> getPWordList() {
        List<String> p_words_list = null;
        try {
            File file = new File(serialized_folder + File.separator + "p_list.ser");
            if (file.exists()) {
//                System.out.println("p_list serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                p_words_list = (List<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
//                System.out.println("p_list word serlized file not found in " + serialized_folder + File.separator +
//                        "p_list.ser");
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

    private HashSet<String> getVocabulary() {
        HashSet<String> vocabulary = null;
        try {
            File file = new File(serialized_folder + File.separator + "vocabulary.ser");
            if (file.exists()) {
//                System.out.println("Vocabulary serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                vocabulary = (HashSet<String>) in.readObject();
                in.close();
                fileIn.close();
            } else {
//                System.out.println("Vocabulary serialized file not found in " + serialized_folder + File.separator +
//                        "vocabulary.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return vocabulary;
    }

    private double[][] getTMatrix() {
        double[][] t_matrix = null;
        try {
            File file = new File(serialized_folder + File.separator + "t_matrix.ser");
            if (file.exists()) {
//                System.out.println("t_matrix serialized file found. Reading from it");
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                t_matrix = (double[][]) in.readObject();
                in.close();
                fileIn.close();
            } else {
//                System.out.println("t_matrix serialized file not found in " + serialized_folder + File.separator +
//                        "t_matrix.ser");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return t_matrix;
    }

    public String setPVector(String paragraph) {
        List<Double> p_vector = docVector(paragraph, pWordList, vocabulary, t_matrix);
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

        PVector cal = new PVector();
        List<String> p_words_list = cal.getPWordList();
        HashSet<String> vocabulary = cal.getVocabulary();
        double[][] t_matrix = cal.getTMatrix();

//        List<Double> p_vector = cal.docVector(par, p_words_list, vocabulary, t_matrix);
//        for (double val : p_vector) {
//            System.out.print(val + ", ");
//        }

        cal.setPVector(par);


    }

}
