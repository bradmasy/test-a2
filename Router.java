import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Router extends Thread {

    private Socket socket = null;

    public Router(Socket socket) {
        this.socket = socket;
    }


    private Servlet getServlet(String locationOfRequest ) throws  ClassNotFoundException,
     NoSuchMethodException,InstantiationException,IllegalAccessException ,InvocationTargetException{
        Servlet up = null;
        
        if(locationOfRequest.equals("browser")){
                Class<?> c = Class.forName("UploadServlet");
                up = (UploadServlet) c.getDeclaredConstructor().newInstance(); // cast to upload
                System.out.println("instance has been cast to upload servlet");
                up = new UploadServlet();

            } else if(locationOfRequest.equals("cli")) {
                Class<?> c = Class.forName("ClientServlet");
                System.out.println("instance has been cast to client/cli servlet");
                up = (ClientServlet) c.getDeclaredConstructor().newInstance(); // cast to client 
                up = new ClientServlet();
            }
            
            return up;
    }

    private void runMethod(String method, Response res, Request req, Servlet up){
             switch(method){
                case "GET":
                    System.out.println("-------------------------------2. METHOD:" + method);
                    up.doGet(res, req);
                    break;
                case "POST":
                    System.out.println("-------------------------------3. METHOD:" + method);
                    up.doGet(res, req);
                    up.doPost(res, req);
                    break;
                default:
                    System.out.println("Uh oh");
                    break;
            }
    }

    public void run() {
        System.out.println("Running");

        try {

            //post do this if get do this etc....
            // if its a post it contains multipart data etc, look at user agent: this will tell you where its coming from
            // parse the body of request with post, caption and time, 
            // response is html for browser if its native client create json

            InputStream in = socket.getInputStream(); // get or post as first 
            Request req = new Request(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Response res = new Response(socket, baos);
            String locationOfRequest = req.getUserAgent();
            Servlet up = null; // conrete class is new concrete version of servlet to allow for casting/reflection to subclasses.

            up = getServlet(locationOfRequest);

            String method = req.getReqMethod();

            runMethod(method,res,req,up);
    
            OutputStream out = socket.getOutputStream();
            res.createByteArray();

            // This code gets the form page on localhost:8999
            socket.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

      

    }

}
