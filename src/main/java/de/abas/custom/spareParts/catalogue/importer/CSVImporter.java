package de.abas.custom.spareParts.catalogue.importer;

import org.apache.log4j.Logger;

import de.abas.erp.axi.event.EventException;

public class CSVImporter implements Importer {

	private static Logger logger = Logger.getLogger(CSVImporter.class);

	@Override
	public SparePart parseData(String line) throws EventException {
		logger.info("Parsing data ...");
		final String[] dataArray = line.split(";");

		try {
			// line in csv file
			// productNo;description;price[;matchcode]
			switch (dataArray.length) {
			case 3:
				return new SparePart(dataArray[0], dataArray[1], dataArray[2]);
			case 4:
				return new SparePart(dataArray[0], dataArray[3], dataArray[1], dataArray[2]);
			default:
				logger.error(String.format("Invalid format of line:\n"
						+ "Line has %d arguments but only 3 or 4 arguments are allowed.\n"
						+ "Each line of csv file is supposed to have the following formatting:\n"
						+ "\tproductNo;description;price[;matchcode]\n" + "\tThe argument separator must be ';'.\n"
						+ "\tPrice must be a floating point number with '.' as decimal separator, e.g. 3.12.\n"
						+ "\tMatchcode is an optional argument.", dataArray.length));
				throw new EventException("Invalid file format, csv format expected.\n"
						+ "Each line is supposed to have the following format:\n"
						+ "productNo;description;price[;matchcode]", 1);
			}
		} catch (final NumberFormatException e) {
			logger.error(String.format("Invalid price:\n"
					+ "Argument 3 '%s' is supposed to be the price but could not be converted to BigDecimal.\n"
					+ "Each line of csv file is supposed to have the following formatting:\n"
					+ "\tproductNo;description;price[;matchcode]\n" + "\tThe argument separator must be ';'.\n"
					+ "\tPrice must be a floating point number with '.' as decimal separator, e.g. 3.12.\n"
					+ "\tMatchcode is an optional argument.", dataArray[2]));
			throw new EventException(String.format("Invalid price, could not convert \"%s\" to number value.\n"
					+ "Please note, the decimal separator for price must be \".\"!", dataArray[2]), 1);
		}
	}

}
