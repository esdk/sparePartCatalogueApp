package de.abas.custom.spareParts.catalogue.importer;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.db.schema.userenums.UserEnumImportfileformat;

public class ImporterFactory {

	public static Importer newInstance(UserEnumImportfileformat format) throws EventException {
		if (format.equals(UserEnumImportfileformat.CSV)) {
			return new CSVImporter();
		}

		String fileFormats = "";
		for (UserEnumImportfileformat fileFormat : UserEnumImportfileformat.values()) {
			fileFormats = fileFormat + ", ";
		}
		throw new EventException(
				Util.getMessage("importerfactory.err.file.format", fileFormats.substring(0, fileFormats.length() - 2)),
				1);
	}

}
