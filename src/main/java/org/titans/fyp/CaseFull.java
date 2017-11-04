package org.titans.fyp;

import java.io.*;

/**
 * Created by Buddhi on 11/4/2017.
 */
public class CaseFull extends Case {
    private String fulltext = "";

    public CaseFull(String id) {
        super(id);
        setCaseData();
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setCaseData() {
        String path = System.getProperty("user.dir") + File.separator + "RawCases" + File.separator + getId() + ".txt";
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            setCaseData(line.replaceAll("\\P{Print}", ""));

            while ((line = br.readLine()) != null) {
                if (!line.contains("Footnote")) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
            }
            br.close();
            fulltext = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
