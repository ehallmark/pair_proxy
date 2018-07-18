import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

        get("/epo", (req, res)-> {
            String asset = req.queryParams("asset");
            String auth_token = req.queryParams("auth_token");
            if(asset == null) return null;
            URL url = new URL("http://ops.epo.org/3.2/rest-services/family/publication/docdb/"+asset);
            System.out.println(url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization","Bearer "+auth_token);
            InputStream content = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(content));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        });

        awaitInitialization();
        System.out.println("Started server...");
    }
}
