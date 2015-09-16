package com.saucelabs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Logger {

	private String fileName;
	private String sessionId;

	public Logger(String path, String session) {
		fileName = path;
		sessionId = session;
	}

	public void add(String message) throws IOException {
		File file = new File("C:\\Tests\\logs\\" + fileName + " - " + sessionId + ".txt");

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		String time = sdf.format(calendar.getTime());

        if (!file.exists()) {
            Boolean fc = file.createNewFile();
            if (!fc) {
                System.out.println("File could not be created");
                System.exit(1);
            } else {
                System.out.println("File was created");
            }
        }

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        System.out.println(time + " " + message);
        pw.println(time + " " + message);

        pw.close();
	}
}