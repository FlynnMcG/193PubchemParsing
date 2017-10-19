import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlashCallback {

    public boolean FlashParser(URL url) {
        InputStream is = null;
        BufferedReader br = null;
        FileReader fr = null;
        List<String> Names = new ArrayList<String>();
        List<Double> MeltingPoints = new ArrayList<Double>();

        try {
            is = url.openStream();

            br = new BufferedReader(new InputStreamReader(is));

            String sCurrentLine;
            String sLastLine="";
            String sTwoLinesBack="";
            String temp="";
            String firstHalf="";
            String secondHalf="";
            //boolean nameFlag=false;
            boolean farenheightFlag=false;
            boolean splitFlag=false;

            while ((sCurrentLine = br.readLine()) != null) {

                if((sCurrentLine.matches(".*?\\bName\\b.*?")))
                {
                    farenheightFlag=false;
                    splitFlag=false;
                    //nameFlag=!nameFlag;
                    if (!sCurrentLine.contains("Flash Point"))
                    {
                        sCurrentLine=sCurrentLine.replace("\"","");
                        sCurrentLine=sCurrentLine.replace(",","");
                        sCurrentLine=sCurrentLine.replace("  ","");
                        System.out.println(sCurrentLine.replace("Name: ", ""));

                    }
                }
                if(sLastLine.matches(".*?\"CID\".*?"))
                {
                    sCurrentLine=sCurrentLine.replace("\"","");
                    sCurrentLine=sCurrentLine.replace(",","");
                    sCurrentLine=sCurrentLine.replace("  ","");
                    System.out.println(sCurrentLine);
                }
                if((sTwoLinesBack.matches(".*?\\bValue\\b.*?")))
                {
                    if((sCurrentLine.matches(".*?\\bHg\\b.*?"))||(sCurrentLine.matches(".*?\\bHG\\b.*?"))||(sCurrentLine.matches(".*?\\bhg\\b.*?")))
                    {
                        int last=sCurrentLine.lastIndexOf('@');
                        if(last!=-1)
                        {
                            firstHalf=sCurrentLine.substring(0,last-1);
                            secondHalf=sCurrentLine.substring(last);
                            splitFlag=true;
                        }
                        else
                        {
                            last=sCurrentLine.lastIndexOf('a');
                            if(last!=-1)
                            {
                                firstHalf=sCurrentLine.substring(0,last-1);
                                secondHalf=sCurrentLine.substring(last);
                                splitFlag=true;
                            }
                            else
                            {
                                last=sCurrentLine.lastIndexOf('A');
                                if(last!=-1)
                                {
                                    firstHalf=sCurrentLine.substring(0,last-1);
                                    secondHalf=sCurrentLine.substring(last);
                                    splitFlag=true;
                                }
                            }
                        }

                    }
                    if((sCurrentLine.matches(".*?\\bF\\b.*?"))||(sCurrentLine.matches(".*?\\b°F\\b.*?"))||(sCurrentLine.matches(".*?\\bF°\\b.*?"))||(sCurrentLine.matches(".*?\\bdeg F\\b.*?"))||(sCurrentLine.matches(".*?\\bDEG F\\b.*?")))
                    {
                        if(!sCurrentLine.matches(".*?\\bC°\\b.*?")&&!sCurrentLine.matches(".*?\\b°C\\b.*?"))
                            System.out.println("1");
                    }

                    else
                        System.out.println("0");
                    if(sCurrentLine.matches(".*?href.*?"))
                        sCurrentLine="-273";
                    if(!splitFlag) {
                        sCurrentLine = sCurrentLine.replace("\"", "");
                        sCurrentLine = sCurrentLine.replace(",", "");
                        sCurrentLine = sCurrentLine.replace("  ", "");
                        sCurrentLine = sCurrentLine.replaceAll("[A-Za-z°()\\s]+", "");
                        sCurrentLine = sCurrentLine.replace("=", "");
                        sCurrentLine = sCurrentLine.replace(":", "");
                        sCurrentLine = sCurrentLine.replace("@", "");
                        sCurrentLine = sCurrentLine.replace("&", "");
                        sCurrentLine = sCurrentLine.replace("/", "");


                        if (sCurrentLine.matches(".*?;.*?")) {

                            int last = sCurrentLine.lastIndexOf(";");
                            if (last != -1) {
                                temp = sCurrentLine.substring(0, last);
                                if (temp.length() == 0)
                                    temp = sCurrentLine.substring(last + 1, sCurrentLine.length());
                                sCurrentLine = temp;
                            }
                        }
                        if (sCurrentLine.matches(".*?\\d+-+\\d.*?")) {

                            int last = sCurrentLine.lastIndexOf("-");
                            sCurrentLine = sCurrentLine.substring(0, last);
                        }

                        int last = sCurrentLine.lastIndexOf(" ");
                        if (last != -1)
                            sCurrentLine = sCurrentLine.substring(0, last);
                        System.out.println(sCurrentLine);
                    }
                    else {
                        firstHalf = firstHalf.replace("\"", "");
                        firstHalf = firstHalf.replace(",", "");
                        firstHalf = firstHalf.replace("  ", "");
                        firstHalf = firstHalf.replaceAll("[A-Za-z°()\\s]+", "");
                        firstHalf = firstHalf.replace("=", "");
                        firstHalf = firstHalf.replace(":", "");
                        firstHalf = firstHalf.replace("@", "");
                        firstHalf = firstHalf.replace("&", "");
                        firstHalf = firstHalf.replace("/", "");

                        secondHalf = secondHalf.replace("\"", "");
                        secondHalf = secondHalf.replace(",", "");
                        secondHalf = secondHalf.replace("  ", "");
                        secondHalf = secondHalf.replaceAll("[A-Za-z°()\\s]+", "");
                        secondHalf = secondHalf.replace("=", "");
                        secondHalf = secondHalf.replace(":", "");
                        secondHalf = secondHalf.replace("@", "");
                        secondHalf = secondHalf.replace("&", "");
                        secondHalf = secondHalf.replace("/", "");



                        if (firstHalf.matches(".*?;.*?")) {

                            int last = firstHalf.lastIndexOf(";");
                            if (last != -1) {
                                temp = firstHalf.substring(0, last);
                                if (temp.length() == 0)
                                    temp = firstHalf.substring(last + 1, firstHalf.length());
                                firstHalf = temp;
                            }
                        }
                        if (secondHalf.matches(".*?;.*?")) {

                            int last = secondHalf.lastIndexOf(";");
                            if (last != -1) {
                                temp = secondHalf.substring(0, last);
                                if (temp.length() == 0)
                                    temp = secondHalf.substring(last + 1, secondHalf.length());
                                secondHalf = temp;
                            }
                        }
                        if (firstHalf.matches(".*?\\d+-+\\d.*?")) {
                            int last = firstHalf.lastIndexOf("-");
                            firstHalf = firstHalf.substring(0, last);
                        }

                        if (secondHalf.matches(".*?\\d+-+\\d.*?")) {
                            int last = secondHalf.lastIndexOf("-");
                            secondHalf = secondHalf.substring(0, last);
                        }

                        int last = firstHalf.lastIndexOf(" ");
                        if (last != -1)
                            firstHalf = firstHalf.substring(0, last);
                        System.out.println(firstHalf);

                        last = secondHalf.lastIndexOf(" ");
                        if (last != -1)
                            secondHalf = secondHalf.substring(0, last);
                        System.out.println(secondHalf);
                    }
                }
                sTwoLinesBack=sLastLine;
                sLastLine=sCurrentLine;

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {


                ex.printStackTrace();

            }

        }
    return true;
    }

}