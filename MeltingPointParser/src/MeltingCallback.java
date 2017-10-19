package parsing.pubchem.callbacks;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MeltingCallback {

    public boolean MeltingParser(URL url) {

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
            boolean nameFlag=false;
            boolean farenheightFlag=false;

            while ((sCurrentLine = br.readLine()) != null) {

                if((sCurrentLine.matches(".*?\\bName\\b.*?")))
                {
                    farenheightFlag=false;
                    nameFlag=!nameFlag;
                    if (nameFlag==true)
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
                    if(sCurrentLine.matches(".*?labile.*?"))
                        sCurrentLine="2.8";

                    if((sCurrentLine.matches(".*?\\bF\\b.*?"))||(sCurrentLine.matches(".*?\\b°F\\b.*?"))||(sCurrentLine.matches(".*?\\bF°\\b.*?"))||(sCurrentLine.matches(".*?\\bdeg F\\b.*?"))||(sCurrentLine.matches(".*?\\bDEG F\\b.*?")))
                        farenheightFlag=true;
                    else
                        System.out.println("0");
                    if(sCurrentLine.matches(".*?href.*?"))
                        sCurrentLine="-273";
                    sCurrentLine=sCurrentLine.replace("\"","");
                    sCurrentLine=sCurrentLine.replace(",","");
                    sCurrentLine=sCurrentLine.replace("  ","");
                    sCurrentLine=sCurrentLine.replace("/D-form/", "");
                    sCurrentLine=sCurrentLine.replaceAll("[A-Za-z°()\\s]+", "");
                    sCurrentLine=sCurrentLine.replace("=", "");
                    sCurrentLine=sCurrentLine.replace(":", "");
                    sCurrentLine=sCurrentLine.replace("@", "");
                    sCurrentLine=sCurrentLine.replace("&", "");
                    sCurrentLine=sCurrentLine.replace("/", "");


                    if(sCurrentLine.matches(".*?;.*?"))
                    {

                        int last=sCurrentLine.lastIndexOf(";");
                        if(last!=-1) {
                            temp = sCurrentLine.substring(0, last);
                            if (temp.length() == 0)
                                temp = sCurrentLine.substring(last+1, sCurrentLine.length());
                            sCurrentLine = temp;
                        }
                    }
                    if(sCurrentLine.matches(".*?\\d+-+\\d.*?"))
                    {
                        System.out.println("Success");
                        int last=sCurrentLine.lastIndexOf("-");
                        sCurrentLine=sCurrentLine.substring(0,last);
                    }

                    int last=sCurrentLine.lastIndexOf(" ");
                    if(last!=-1)
                        sCurrentLine=sCurrentLine.substring(0, last);
                    if(sCurrentLine.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")) {
                        double dCurrentDouble=Double.parseDouble(sCurrentLine);
                        if(dCurrentDouble>=-273)
                            System.out.println(sCurrentLine);
                        else
                            System.out.println("Null");
                    }
                    else
                        System.out.println("Null");
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