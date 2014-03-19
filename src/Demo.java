import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Demo {
	private static String[] header = null;
	private static int totalCompanies = 0;
	private static int lineNumber = 0;
	private static Map<String, Double> companyMaxPriceMap;
	private static Map<String, Company> companyMaxPriceMonthDetailMap;

	// --------------

	public static void main(String[] args) {
		if (0 >= args.length) {
			printErrorAndExit("Invalid arguments count: " + args.length);
		}
		Scanner scanner = readFile(args[0]);
		// String fileName = "C://Users//rohitg//Desktop//Demo.csv";
		// Scanner scanner = readFile(fileName);
		evaluateCompanyMaxPriceMonth(scanner);
		DisplayCompanyMaxPriceMonth();
	}

	private static Scanner readFile(String inFile) {
		// read file line by line in Java using Scanner
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			printErrorAndExit("File " + inFile + " not found.");
		}
		Scanner scanner = new Scanner(fis);
		return scanner;
	}

	private static void evaluateCompanyMaxPriceMonth(Scanner scanner) {
		processHeaderRow(scanner);
		companyMaxPriceMap = new HashMap<String, Double>();
		companyMaxPriceMonthDetailMap = new TreeMap<String, Company>();
		while (scanner.hasNextLine()) {
			String csvLine = scanner.nextLine();
			String[] values = csvLine.split(",");
			if (values.length != totalCompanies) {
				printErrorAndExit("Some data is missing for line number" + lineNumber++);
			}

			// here coloumnNo start from 2 because 0th and 1st column
			// contain year and month detail respectively.
			for (int columnNo = 2; columnNo < totalCompanies; columnNo++) {
				Double currentPrice = getDoubleValue(values, columnNo);
				String processingCompanyName = header[columnNo];
				Double knownMaxPrice = companyMaxPriceMap.get(processingCompanyName);

				if (currentPrice != null && (knownMaxPrice == null || knownMaxPrice < currentPrice)) {
					companyMaxPriceMap.put(processingCompanyName, currentPrice);
					companyMaxPriceMonthDetailMap.put(processingCompanyName, new Company(processingCompanyName,
							values[0], values[1], currentPrice));
				}
			}
			lineNumber++;
		}
		scanner.close();
	}

	private static Double getDoubleValue(String[] values, int columnNo) {
		Double currentPrice = null;
		String priceValue = values[columnNo];
		if (priceValue != null) {
			try {
				currentPrice = Double.parseDouble(priceValue);
			} catch (NumberFormatException ex) {
				printErrorAndExit("Correupted Data exist in a File on line number" + lineNumber++);
			}
		} else {
			printErrorAndExit("Some data is missing for line number" + lineNumber++);
		}
		return currentPrice;
	}

	private static void processHeaderRow(Scanner scanner) {
		if (scanner.hasNextLine()) {
			String firstLine = scanner.nextLine();
			header = firstLine.split(",");
			totalCompanies = header.length;
			if (totalCompanies <= 2) {
				printErrorAndExit("No Company Data found in a file ");
			}
			for (int columnNo = 2; columnNo < totalCompanies; columnNo++) {
				String processingCompanyName = header[columnNo];
				if (processingCompanyName == null) {
					printErrorAndExit("Some Comapny Name are missing ");
				}
			}
			lineNumber++;
		} else {
			printErrorAndExit("No Data found in a file ");
		}
	}

	private static void printErrorAndExit(String errorMsg) {
		System.err.println(errorMsg);
		System.exit(1);
	}

	private static void DisplayCompanyMaxPriceMonth() {
		// Done, now display the winners
		System.out.println("List for each Company year and month in which the share price was highest.");
		System.out.println("------------------------------------------------");
		for (String name : companyMaxPriceMonthDetailMap.keySet()) {
			Company company = companyMaxPriceMonthDetailMap.get(name);
			System.out.println(company.getName() + " highest price " + company.getPrice() + " is  in "
					+ company.getMonth() + " , " + " " + company.getYear());
		}
	}

}
