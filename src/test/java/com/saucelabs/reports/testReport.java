package com.saucelabs.reports;

import com.saucelabs.saucerest.SauceREST;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class testReport {

    private static String slUser;
    private static String slAuth;


    public static void main (String[] args) throws Exception {



        Properties config = new Properties();
        try {
            InputStream slCfg = new FileInputStream("C:\\Tests\\configs\\SauceLabs.properties");
            config.load(slCfg);

            slUser = config.getProperty("user");
            slAuth = config.getProperty("auth");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        SauceREST client = new SauceREST(slUser, slAuth);

        Integer limit = 10;
        String[] request = {"limit=" + limit};
        JSONArray jobs = new JSONArray(client.getJobsList(request));
        JSONArray info_suitDirectCheckout = new JSONArray();
        JSONArray info_suitDirectOnline = new JSONArray();

        for (int i=0; i<limit; i++) {
            String current = jobs.getJSONObject(i).getString("id");
            JSONArray info = new JSONArray("[" + client.getJobInfo(current) + "]");

            String name = info.getJSONObject(0).getString("name");
            String passed = info.getJSONObject(0).getString("passed");
            Integer start = Integer.parseInt(info.getJSONObject(0).getString("start_time"));
            Integer end = Integer.parseInt(info.getJSONObject(0).getString("end_time"));
            Integer runTime = (end-start);

            JSONObject report = new JSONObject();
            report.put("ID", current);
            report.put("Start Time", start);
            report.put("End Time", end);
            report.put("Run Time", runTime);
            report.put("Passed", passed);

            if (name.equals("Suit Direct Checkout")) {
                info_suitDirectCheckout.put(report);
            } else if (name.contains("Suit Direct Online")) {
                info_suitDirectOnline.put(report);
            }

        }

        Integer info_overallTotal = 0;
        Integer info_suitDirectCheckoutTotal = 0;
        Integer info_suitDirectOnlineTotal = 0;

        for (int i=0; i < info_suitDirectCheckout.length(); i++) {
            info_suitDirectCheckoutTotal = info_suitDirectCheckoutTotal + info_suitDirectCheckout.getJSONObject(i).getInt("Run Time");

            info_overallTotal = info_overallTotal + info_suitDirectCheckout.getJSONObject(i).getInt("Run Time");
        }
        Integer info_suitDirectCheckoutAvg = info_suitDirectCheckoutTotal / info_suitDirectCheckout.length();

        for (int i=0; i < info_suitDirectOnline.length(); i++) {
            info_suitDirectOnlineTotal = info_suitDirectOnlineTotal + info_suitDirectOnline.getJSONObject(i).getInt("Run Time");


            info_overallTotal = info_overallTotal + info_suitDirectOnline.getJSONObject(i).getInt("Run Time");
        }
        Integer info_suitDirectOnlineAvg = info_suitDirectOnlineTotal / info_suitDirectOnline.length();

        Integer info_overallAvg = info_overallTotal / limit;

        System.out.println("Overall -- Average run time in seconds: " + info_overallAvg);
        System.out.println("Suit Direct Checkout -- Average run time in seconds: " + info_suitDirectCheckoutAvg);
        System.out.println("Suit Direct Online -- Average run time in seconds: " + info_suitDirectOnlineAvg);


    }
}
