import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

// import org.json.simple.JSONObject;

public class ClientServlet extends Servlet {

    // Client Servlet Class
    // Defining for Socket, the name of the Host
    public String serverName = "localhost";

    // Socket Port Number
    public int portNumber = 8999;

    // File Path for Image
    public String imagePath;
    public String MultiDate = "";
    public String MultiKeyword = "";
    public String MultiCaption = "";

    /*
     * Objectives:
     * Create Socket Connection to Server.java
     * Recieve Input information about:
     * Name of Server, in this case, which is "Server". (Server.java)
     * Information [Data]
     * Information [Keyword]
     * Information [Caption]
     * Information [Image]
     * 
     * Only need to implement POST Request, as we are only required to specify a
     * POST Request to "Upload" an image to our /images directory.
     */

    public void POSTRequest() {
        // HttpURLConnection conn = null;
        BufferedReader reader = null;
        String charset = "UTF-8";

        // Assign File w/ imagePath
        Path file = Paths.get(imagePath);

        // POST Request: Upload an image from File System as "Multipart Data" along with
        // other form data (Date, Keyword, Caption, Filename)
        try {

            // Create Socket Connection to "server" at "portNumber".
            Socket serverSocket = new Socket(serverName, portNumber);

            // Get OutputStream for Socket
            OutputStream outputStream = serverSocket.getOutputStream();

            // Create PrintWriter for output capabilities to Server
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset));

            // Define and open connection to URL.
            // URL url = new URL("http://localhost:8081/Client-Server-A1b/upload");
            // conn = (HttpURLConnection) url.openConnection();
            String boundary = "boundary";
            String payload = "";
            String newLine = "\r\n";

            // Set output capability to true
            writer.append("POST / HTTP/1.1").append(newLine);
            writer.append("Content-Type: multipart/form-data; boundary=" + boundary).append(newLine);
            writer.append("User-Agent: cli").append(newLine).append(newLine).flush();

            // Sending text "Data"
            writer.append("--" + boundary).append(newLine);
            writer.append("Content-Disposition: form-data; name=\"Date\"").append(newLine);
            writer.append("Content-Type: text/plain; charset=" + charset).append(newLine);
            writer.append(newLine).append(MultiDate).append(newLine).flush();

            // Sending text "Keyword"
            writer.append("--" + boundary).append(newLine);
            writer.append("Content-Disposition: form-data; name=\"Keyword\"").append(newLine);
            writer.append("Content-Type: text/plain; charset=" + charset).append(newLine);
            writer.append(newLine).append(MultiKeyword).append(newLine).flush();

            // Sending text "Caption"
            writer.append("--" + boundary).append(newLine);
            writer.append("Content-Disposition: form-data; name=\"Caption\"").append(newLine);
            writer.append("Content-Type: text/plain; charset=" + charset).append(newLine);
            writer.append(newLine).append(MultiCaption).append(newLine).flush();

            // Sending Image File
            writer.append("--" + boundary).append(newLine);
            writer.append("Content-Disposition: form-data; name=\"File\"; filename=\"" + file.getFileName() + "\"")
                    .append(newLine);
            writer.append("Content-Type: image/png").append(newLine);
            writer.append("Content-Transfer-Encoding: binary").append(newLine);
            writer.append(newLine).flush();
            // Files.copy(file, outputStream);
            writer.append(encodeImage(imagePath));
            outputStream.flush();
            writer.append(newLine).flush();
            // End of Multipart.
            writer.append("--" + boundary + "--").append(newLine).flush();

            BufferedReader respnse = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            serverSocket.close();

        } catch (Exception e) {
            System.out.println("POST Request Error: " + e);
            e.printStackTrace();
        }
    }

    public String encodeImage(String imagePath) throws Exception{
        FileInputStream imageStream = new FileInputStream(imagePath);
        byte[] data = imageStream.readAllBytes();
        String imageString = Base64.getEncoder().encodeToString(data);
        return imageString;
    }

    public static BufferedImage decodeToImage(String imageString) {
 
        BufferedImage image = null;
        byte[] imageByte;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            imageByte = decoder.decode(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public void printImageList(){
        File dir = new File("./images");
        HashMap<String,String> responseOutput = new HashMap<String,String>();
        
        String[] chld = dir.list();
        Arrays.sort(chld);
        System.out.println("Sorted List");
        for (String x : chld){
            System.out.println(x);
        }
    }

    public void getUserInput() {

        MultiDate = java.time.LocalDate.now().toString();

        // UserInput Scanner
        Scanner scanner = new Scanner(System.in);

        // User Input for Image
        System.out.print("Please enter the file path for the image\n> ");
        String userInput = scanner.nextLine();
        System.out.println("\n");
        while (userInput.equals("")) {
            try {
                logErrorInFile("Invalid Input for file path.");
                throw new InvalidInputException("Invalid Input...");
            } catch (InvalidInputException e) {
                e.printStackTrace();
            }
            
            System.out.print("Please enter the file path for the image\n> ");
            userInput = scanner.nextLine();
        }
        imagePath = userInput;
        System.out.println("Image Path: " + imagePath + "\n");

        // User Input for Keyword
        System.out.print("Please enter a Keyword for the image\n> ");
        userInput = scanner.nextLine();
        System.out.println("\n");
        while (userInput.equals("")) {
            try {
                logErrorInFile("Invalid Input for keyword.");
                throw new InvalidInputException("Invalid Input...");
            } catch (InvalidInputException e) {
                e.printStackTrace();
            }
            System.out.print("Please enter a Keyword for the image\n> ");
            userInput = scanner.nextLine();
        }
        MultiKeyword = userInput;
        System.out.println("Keyword: " + MultiKeyword + "\n");

        // User Input for Keyword
        System.out.print("Please enter a Caption for the image\n> ");
        userInput = scanner.nextLine();
        System.out.println("\n");
        while (userInput.equals("")) {
            try {
                logErrorInFile("Invalid Input for caption.");
                throw new InvalidInputException("Invalid Input...");
            } catch (InvalidInputException e) {
                e.printStackTrace();
            }
            System.out.print("Please enter a Caption for the image\n> ");
            userInput = scanner.nextLine();
        }
        MultiCaption = userInput;
        System.out.println("Caption: " + MultiCaption + "\n");

    }

    public void doGet(Response res, Request req) {
        System.out.println("ClientServlet.java doGet");
    };

    public void doPost(Response res, Request req) {
        System.out.println("ClientServlet.java doPost");
        System.out.println("Running CLI Servlet POST Code.");

        // Detect CLI, Execute CLI POST Request Implementation
        try {
            System.out.println("POSTRequest-UserAgent: " + req.getUserAgent() + " detected.");


            System.out.println("Recieved Date: " + req.getFormData("Date"));
            System.out.println("Recieved Keyword: " + req.getFormData("Keyword"));
            System.out.println("Recieved Caption: " + req.getFormData("Caption"));

            Clock clock = Clock.systemDefaultZone();
            long milliSeconds = clock.millis();
            String fileName = String.valueOf(milliSeconds);

            BufferedImage image = decodeToImage(req.getImageByteCode());
            File outputFile = new File("./images/" + fileName + ".png");
            ImageIO.write(image, "png", outputFile);

        

        } catch (Exception e) {
            e.printStackTrace();
        }
    };


    public void logErrorInFile(String errorMessage) {
        try {
            FileWriter myWriter = new FileWriter("error-log.txt", true);
            myWriter.append("Error logged: " + errorMessage + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** main method */
    public static void main(String args[]) {

        ClientServlet client = new ClientServlet();
        client.getUserInput(); // get request --> making connection to server
        client.POSTRequest();
        client.printImageList();
    }

}
