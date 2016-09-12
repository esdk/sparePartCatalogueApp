package de.abas.custom.spareParts.catalogue.importer;

import org.apache.log4j.Logger;

import de.abas.custom.spareParts.catalogue.util.Util;
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
				logger.error(Util.getMessage("csvimporter.log.err.file.format", dataArray.length));
				throw new EventException(Util.getMessage("csvimporter.err.file.format"), 1);
			}
		} catch (final NumberFormatException e) {
			logger.error(Util.getMessage("csvimporter.log.err.invalid.price", dataArray[2]));
			throw new EventException(Util.getMessage("csvimporter.err.invalid.price", dataArray[2]));
		}
	}

}
