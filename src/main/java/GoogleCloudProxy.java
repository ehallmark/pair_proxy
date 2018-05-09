import java.net.URL;

import static spark.Spark.*;

public class GoogleCloudProxy {
    public static final String PAIR_URL = "http://patents.reedtech.com/downloads/pairdownload/";
    public static void main(String[] args) {
        port(8080);
        get("/public_pair", (req,res)->{
            String appNum = req.queryParams("app_num");
            if(appNum==null) throw new RuntimeException("Must provide application number...");
            System.out.println("Request for app: "+appNum);
            final String urlStr = PAIR_URL + appNum + ".zip";
            //HttpServletResponse raw = res.raw();
            res.header("Content-Disposition", "attachment; filename="+appNum+".zip");
            res.type("application/force-download");
            return new URL(urlStr).openStream();
        });
        awaitInitialization();
        System.out.println("Started server...");
    }
}
