package de.abas.custom.spareParts.catalogue.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.axi.event.EventException;

public interface Importer {

	Logger logger = Logger.getLogger(Importer.class);

	SparePart parseData(String line) throws EventException;

	default List<SparePart> readFile(File file) throws EventException {
		final ArrayList<SparePart> spareParts = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			logger.info(Util.getMessage("importer.log.read.file", file.getName()));
			String line;
			while ((line = r.readLine()) != null) {
				logger.info(Util.getMessage("importer.log.line", line));
				spareParts.add(parseData(line));
			}
		} catch (final FileNotFoundException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new EventException(Util.getMessage("importer.err.no.file", file.getName()), 1);
		} catch (final IOException e) {
			logger.error(e.getMessage(), e.getCause());
			throw new EventException(Util.getMessage("importer.err.reading", file.getName()), 1);
		}
		return spareParts;
	}

}
