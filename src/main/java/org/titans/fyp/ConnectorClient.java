package org.titans.fyp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Buddhi on 11/3/2017.
 */
public class ConnectorClient {
    private static Thread sent;
    private static Socket socket;
    private BufferedReader stdIn;
    private PrintWriter out;
    private String folderPath = System.getProperty("user.dir");
    private PVector pv;

    public ConnectorClient() {
        pv = PVector.getInstance();
        try {
            socket = new Socket("localhost", 8888);
            stdIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public List<Case> sendData(final String message, int outputCount) {
        List<Case> similarCases = new ArrayList<>();
        sent = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.print(message + "\r\n");
                    out.flush();
                    out.print(String.valueOf(outputCount) + "\r\n");
                    out.flush();
                    String line = stdIn.readLine();
//                    System.out.println(line);
                    if (line == null) {
                        System.out.println("Error");
                    }
                    if (line.contains("data=")) {
                        BufferedReader br;
                        String[] docID = line.substring(5).split(", ");

                        try {
                            for (String fileName : docID) {
                                String file = folderPath + File.separator + "RawCases" + File.separator + fileName + ".txt";
                                br = new BufferedReader(new FileReader(file));
                                Case cs = new Case(fileName);
                                cs.setCaseData(br.readLine().replaceAll("\\P{Print}", ""));
//                        System.out.println(fileName);
                                similarCases.add(cs);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sent.start();
        try {
            sent.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return similarCases;
    }

    private List<Case> findSimilarCases(String sentences, int outputCount) throws Exception {
        long startTime = System.currentTimeMillis();

        String pVector = pv.setPVector(sentences);// + String.valueOf(outputCount);
        List<Case> tem = sendData(pVector, outputCount);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time Taken: " + (totalTime / 1000.0) + "s");
        return tem;
    }


    public static void main(String args[]) {

//        String par = "I am gay. Me and my partner are married for 3 years now. " +
//                "I gave birth to a baby with the help of a sperm donor. " +
//                "However, birth registration officials refuse to issue the birth certificate " +
//                "with the partner’s name as one of the parents, stating that it is legally prohibited to issue.";


//        String par = "I am a divorced wife of a retired veteran. " +
//                "He was ordered to pay me a compensation from his total retirement pay at the divorce. " +
//                "As it was ordered to pay the compensation from his retirement pay, and now he is not receiving it, " +
//                "he now refuses to pay my portion.  What should I do?";

        ConnectorClient cl = new ConnectorClient();
//        cl.sendData(data);

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Please enter your text: ");
            String par = sc.nextLine();
//            System.out.println(par);

            try {
                for (Case cs : cl.findSimilarCases(par, 10)) {
                    System.out.println("ID:" + cs.getId());
                    System.out.println("Court:" + cs.getCourt());
                    System.out.println("Case Name:" + cs.getCaseName());
                    System.out.println("Date:" + cs.getDate());
                    System.out.println("Case ID:" + cs.getCaseID());
                    System.out.println("Argued Date:" + cs.getArguedDate());
                    System.out.println("Decided Date:" + cs.getDecidedDate());
                    System.out.println();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
