package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaveUtil {

	private static final SimpleDateFormat	DATE_FORMAT	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final String				FILE_NAME	= "save.txt";
	private static final String				DELIMITER	= "//";

	public static void save(List<Donation> donations) {
		try {
			PrintWriter out = new PrintWriter(FILE_NAME);
			for (Donation donation : donations) {

				String name = donation.getName();
				Double amount = donation.getAmount();
				String time = DATE_FORMAT.format(new Date(donation.getTime()));
				out.println(name + DELIMITER + amount + DELIMITER + time);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<Donation> load() {
		List<Donation> list = new ArrayList<Donation>();
		try {
			FileReader fileReader = new FileReader(FILE_NAME);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String[] fields = line.split(DELIMITER);
				String name = fields[0];
				Double amount = Double.parseDouble(fields[1]);
				Long time = DATE_FORMAT.parse(fields[2]).getTime();
				list.add(new Donation(name, amount, time));
			}
			bufferedReader.close();

		} catch (FileNotFoundException e) {
			System.err.println("No save file exists so one was created.");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}

}
