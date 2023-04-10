package Frontend;

import javax.xml.ws.Endpoint;

public class runFrontend {
    static IFrontEnd obj = null;
    public static void main(String args[]) {
        obj = new Frontend.FrontEnd();
        registerEndPoint();
    }
    private static void registerEndPoint() {

        Endpoint endpoint = Endpoint.publish("http://localhost:8085/"+ "frontend",obj);
    }
}
