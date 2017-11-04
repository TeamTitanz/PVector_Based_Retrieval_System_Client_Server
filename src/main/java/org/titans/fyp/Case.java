package org.titans.fyp;

/**
 * Created by Buddhi on 11/4/2017.
 */
public class Case {

    private String id = "";
    private String court = "";
    private String caseName = "";
    private String date = "";
    private String caseID = "";
    private String arguedDate = "";
    private String decidedDate = "";
    private String judge = "";


    public Case(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCourt() {
        return court;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getDate() {
        return date;
    }

    public String getCaseID() {
        return caseID;
    }

    public String getArguedDate() {
        return arguedDate;
    }

    public String getDecidedDate() {
        return decidedDate;
    }

    public String getJudge() {
        return judge;
    }

    public void setCaseData(String line) {
        if (line.contains("United States Supreme Court")) {
            court = "United States Supreme Court";
            if (line.contains("No.")) {
                String[] tem = line.split("Court")[1].split("No.");
                if (tem[0].contains(",")) {
                    String[] nt = tem[0].split(",");
                    caseName = nt[0].trim();
                    if (nt[1].contains("(") && nt[1].contains(")")) {
                        date = (nt[1].split("\\(")[1].split("\\)")[0]).trim();
                    }
                } else {
                    caseName = tem[0].trim();
                }
                if (tem[1].contains("Argued:")) {
                    caseID = ("No." + tem[1].split("Argued:")[0]).trim();
                    if (tem[1].contains("Decided:")) {
                        String[] td = tem[1].split("Argued:")[1].split("Decided:");
                        arguedDate = td[0].trim();
                        decidedDate = td[1].trim();
                    }
                }
            }
        }
    }

}
