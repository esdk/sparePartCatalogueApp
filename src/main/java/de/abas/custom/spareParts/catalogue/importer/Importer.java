package de.abas.custom.spareParts.catalogue.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.abas.erp.axi.event.EventException;

public interface Importer {

	static Logger logger = Logger.getLogger(Importer.class);

	SparePart parseData(String line) throws EventException;

	default List<SparePart> readFile(File file) throws EventException {
		final ArrayList<SparePart> spareParts = new ArrayList<SparePart>();
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			logger.info(String.format("Reading from file '%s' ...", file.getName()));
			String line = "";
			while ((line = r.readLine()) != null) {
				logger.info(String.format("line: '%s'", line));
				spareParts.add(parseData(line));
			}
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new EventException(String.format("File \"%s\" not found", file.getName()), 1);
		} catch (final IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new EventException(String.format("Could not read from file \"%s\"", file.getName()), 1);
		}
		return spareParts;
	}

}
