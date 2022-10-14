import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import java.io.*;

public class UploadServlet extends Servlet {
    public void doGet(Response res, Request req) {
        System.out.println("Here in do get");
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = res.getWriter();

        // Check User-Agent
        System.out.println("USER-Agent: " + req.getUserAgent());

        String html = "<!DOCTYPE html>\r\n" +
                "<html>\r\n" +
                "<head>\r\n" +
                "<title>File Upload Form</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "<h1>Upload file</h1>\r\n" +
                "<form id=\"form\" method=\"POST\" action=\"/\" enctype=\"multipart/form-data\">\r\n" + // up to us on where to go, another tcp request comes
                "<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n" +
                "Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n" +
                "<br />\n" +
                "Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n" +
                "<br />\n" +
                "<input id='formBtn' type=\"submit\" name=\"submit\" value=\"Submit\"/>\r\n" +
                "</form>\r\n";
                // + "</body>\r\n</html>\r\n";

                if (req.getUserAgent().equals("browser") && req.getReqMethod().equals("POST")) {
                    // Send back html of our directory.
                    html += getListing();
                }


                String endTags = "</body>\r\n</html>\r\n";
                html += endTags;


        System.out.println(html);
        writer.println(html);

        System.out.println(req);
        res.send(html);

        // "<script>document.getElementById('formBtn').addEventListener('click', function(event){" +
        //         "event.preventDefault()});</script>" +

    }

    public void doPost(Response res, Request req) {
        System.out.println("POST");
        System.out.println("res: " + res);
        System.out.println("req: " + req);
        System.out.println("POST-Request User-Agent: " + req.getUserAgent());


        // if(req.getUserAgent().equals("cli")){
        //     System.out.println("Running CLI Servlet POST Code.");

        //     // Detect CLI, Execute CLI POST Request Implementation
        //     try{
        //         InputStream in = req.getInputStream();
        //         System.out.println("POSTRequest-UserAgent: " + req.getUserAgent() + " detected.");

        //         //System.out.println("Caption from Payload: " + req.getFormData("Caption"));

        //         // Reference Jay's UploadServlet.java code from Assignment1b.
        //         // Objective: Need to define getPart() method in Request Class to help parse multipart/form-data
        //         // Part filePart = req.getPart("File");
        //         // String fileName = filePart.getSubmittedFileName();
        //         // System.out.println("Recieved Filename: "+ fileName);

        //         System.out.println("Recieved Date: "+ req.getFormData("Date"));
        //         System.out.println("Recieved Keyword: "+ req.getFormData("Keyword"));
        //         System.out.println("Recieved Caption: "+ req.getFormData("Caption"));
                

        //         Clock clock = Clock.systemDefaultZone();
        //         long milliSeconds = clock.millis();
        //         String fileName = String.valueOf(milliSeconds);
        //         OutputStream outputStream = null;
        //         ByteArrayOutputStream byteOutputStream = null;
        //         // try {
        //         //     outputStream = new FileOutputStream("./images/" + fileName + ".png");
        //         //     byteOutputStream = new ByteArrayOutputStream();
        //         //     byteOutputStream.write(req.getImageByteCode());
        //         //     byteOutputStream.writeTo(outputStream);
        //         //     outputStream.close();
        //         // } catch (Exception e){
        //         //     System.out.println(e);
        //         // }

        //         try(FileOutputStream fos = new FileOutputStream("./images/" + fileName + ".txt")){
        //             fos.write(req.getImageByteCode());
        //         }

        //         // Path path = Paths.get("./images");
        //         // Files.write(path,req.getImageByteCode());
        //         // File.write(System.getProperty("catalina.base") + "/webapps/Client-Server-A1b/images/" + fileName);

        //     } catch (Exception e){
        //         e.printStackTrace();
        //     }


        // } else if (req.getUserAgent().equals("browser")){
            System.out.println("Running Browser Servlet POST Code.");
            // Detect Browser, Execute Browser POST Request Implementation
            try {
                System.out.println("Recieved Date: " + req.getFormData("Date"));
                System.out.println("Recieved Keyword: " + req.getFormData("Keyword"));
                System.out.println("Recieved Caption: " + req.getFormData("Caption"));
                InputStream in = req.getInputStream();
             
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
                byte[] content = new byte[1];
                int bytesRead = -1;
                while ((bytesRead = in.read(content)) != -1) {
                    baos.write(content, 0, bytesRead);
                }
                System.out.println("2");
                Clock clock = Clock.systemDefaultZone();
                long milliSeconds = clock.millis();
                OutputStream outputStream = new FileOutputStream(new File("./images/" + String.valueOf(milliSeconds) + ".png"));
                baos.writeTo(outputStream);
                outputStream.close();
    
                PrintWriter out = new PrintWriter(res.getOutputStream(), true);
                File dir = new File("./images");
                
                // " key : value "
                HashMap<String,String> responseOutput = new HashMap<String,String>();
                
                String[] chld = dir.list();
                for (int i = 0; i < chld.length; i++) {
                    String currImage = "Image " + i + ": ";
                    String fileName = chld[i];
    
                    // Store image name in hashmap 
                    responseOutput.put(currImage, fileName);
                    
                    
                    out.println(fileName + "\n");
                    // System.out.println(fileName); // writing to the console
                }
                Arrays.sort(chld);  // Sort the array
                System.out.println("Sorted Image Listing: ");
                for (String imageName : chld) {
                    System.out.println(imageName);
                }
                
    
                // JSONObject jsonObject = new JSONObject(responseOutput);
                // String jsonString = jsonObject.toString();
                // System.out.println(jsonObject);
                
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }

    // }


    private String getListing() {
        String dirList =  "<ul>";
         File dir = new File("./images");
         String[] chld = dir.list();
         for(int i = 0; i < chld.length; i++){
            // if ((new File(chld[i])).isDirectory())
            //    dirList += "<li><button type=\"button\">"+chld[i]+"</button></li>";
            // else
               dirList += "<li>"+chld[i]+"</li>";      
         }
         dirList += "</ul>";
         return dirList;
       } 
}