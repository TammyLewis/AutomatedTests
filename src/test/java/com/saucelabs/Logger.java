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

	Logger(String path) {
		fileName = path;
	}

	public void add(String message) throws IOException {
		File file = new File("C:\\Tests\\Checkout\\logs\\" + fileName + ".txt");

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		String time = sdf.format(calendar.getTime());

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);

		System.out.println(time + " " + message);
		pw.println(time + " " + message);

		pw.close();
	}
}