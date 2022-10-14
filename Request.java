import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Request {

    private InputStream inputStream = null;
    private String reqType = null;
    private String reqUserAgent = "";
    private HashMap<String, String> FormData = new HashMap<String, String>();
    private String imageByteCode;
    

    public Request(InputStream inStream) {
        System.out.println("INPUT STREAM");

        inputStream = inStream;

        // switch(reqUserAgent){
        // // Depending on the User-Agent, we need to send back different response.
        // case "browser":
        // break;
        // case "cli":
        // break;
        // }

        // Create a method to parse incoming request payload
        parsePayload(inputStream);

    }

    public void parsePayload(InputStream inStream) {
        String savedByteCode = "";

        System.out.println("PARSE PAYLOAD");

        Scanner scanner = new Scanner(inStream);

        // Need to know Request type: GET or POST
        // Parse First Line
        reqType = scanner.next();
        System.out.println("The Request Type is: " + reqType);

        // Need to know User-Agent type: Find where the connection is comming from, CLI
        // or Browser.
        // If User agent exists - browser, else - command line

        // Stores the current line being read
        String temp = "";

        // Loops until user agent line in the header
        // boolean userAgentFound = (reqUserAgent.equals("browser") || reqUserAgent.equals("cli"));
        // try {
        //     // while((temp = a.nextLine()) != null && temp.toString() !="\n" &&
        //     //  && !temp.equals("\n") && !temp.equals("\r\n")
        //     // && !temp.equals("\n\n")) {
        //     // temp.toString() != "\r\n") {
        //     while (reqUserAgent.equals("") && (temp = scanner.nextLine()) != null) {
                    
        //         System.out.println("PRINT: " + temp);

        //         if (temp.startsWith("User-Agent")) {
        //             reqUserAgent = "cli";
        //             System.out.println("***User agent found: " + reqUserAgent);
        //         } 
        //         // else {
        //         //     reqUserAgent = "cli";
        //         // }

        //     }

        //     if (reqUserAgent.equals("")) {
        //         reqUserAgent = "browser";
        //     }
        // } catch (Exception e) {
        //     System.out.println(e);
        // }

        String dataKeys[] = {"Date", "Keyword", "Caption", "File"};
        int currentKey = 0;
        int maxKey = 4;

        // Loop until we detect end of multipart/form-data ["--"+boundary+"--"]
        boolean endOfDataReached = false;
        // while(!temp.equals("--boundary--")) {
        while(!endOfDataReached && currentKey < maxKey) {
            // Print each token.
            temp = scanner.nextLine();
            
            // Check and Set User-Agent
            if (temp.startsWith("User-Agent:")){
                reqUserAgent = "browser";
            }
            if (temp.startsWith("User-Agent: cli")){
                reqUserAgent = "cli";
            } 


            if (temp.startsWith("--boundary")) {
                // Start of the current body from the form data
                String currBody = scanner.nextLine();
                String currValue = "";

                // Loop until next boundary is reached
                while (!currBody.trim().equals("")) {
                    currValue = currBody;           // Store previous line's value
                    //System.out.println("CURR VALUE: " + currValue);
                    currBody = scanner.nextLine();  // Get next line
                    //System.out.println("CURR BODY: " + currBody);
                    System.out.println("CURRBODY: " + currBody);
                }

                currValue = scanner.nextLine();

                // Store value in hashmap
                FormData.put(dataKeys[currentKey], currValue);
                System.out.println("PUT IN MAP: " + dataKeys[currentKey] + ", " + currValue);
                currentKey++;
            }

            if (currentKey == 3 && temp.startsWith("--boundary")){

                // At the File Body Part. Currently at "--boundary"
                String iterateByteCode = scanner.nextLine();

                // Parse File Body Part..
                while(!(iterateByteCode.trim().equals(""))) {

                    // Keep iterating until you find empty line, which tells us to start reading at the next line.
                    System.out.println("NOT AT EMPTY LINE" + iterateByteCode);
                    iterateByteCode = scanner.nextLine();
                }
                temp = scanner.nextLine();
            }
         

            while ((!temp.trim().equals("")) && currentKey == 3 && !(temp.equals("--boundary--"))){
                //System.out.println("BYTE CODE????" + temp);
                // Start reading and saving byte code line by line.

                savedByteCode = savedByteCode + temp;

                temp = scanner.nextLine();
            }

            // Parse Payload
            // System.out.println("parsePayload Temp: " + temp);
            
            if (temp.startsWith("Accept-Language:") || temp.contains("--boundary--")){
                System.out.println("THIS SHOULD BE THE LAST ONE");
                imageByteCode = savedByteCode;
                //System.out.println("ImageByteCode: " + imageByteCode);
                // String "Byte Code"
                //System.out.println(savedByteCode);
                endOfDataReached = true;
            }
        }

        // if (reqUserAgent.equals("")) {
        //         reqUserAgent = "cli";
        // }






        // ArrayList<String,String> FormData = new ArrayList<String,String>(); -
        // Consider using hashmap instead..

        // Parse First Line
        // String FirstLine = scanner.next();

        // // Check First Line
        // switch(reqType){
        // case "GET":
        // // We will know to execute GET Request
        // FormData.put("GET", reqType);
        // break;
        // case "POST":
        // // We will know to execute POST request
        // FormData.put("POST",reqType);
        // break;
        // }

        // String currentLine;

        // while (scanner.hasNextLine()) {

        // currentLine = scanner.nextLine();
        // System.out.println(currentLine);
        // // Parse each line after the first..
        // if (currentLine.startsWith("User-Agent")) {
        // reqUserAgent = "browser";
        // } else {
        // reqUserAgent = "cli";
        // }
        // //FormData.add(currentLine); //do this after we see a boundary
        // }

        System.out.println("Made it here");
        // scanner.close();
    }

    public void setReqType(final String reqType) {
        this.reqType = reqType;
    }

    public String getReqMethod() {
        return reqType;
    }

    public String getUserAgent() {
        return reqUserAgent;
    }

    public void setUserAgent(String userAgent) {
        reqUserAgent = userAgent;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFormData(String key){
        return FormData.get(key);
    }

    public String getImageByteCode(){
        return imageByteCode;
    }
}
