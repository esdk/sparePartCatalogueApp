package de.abas.custom.spareParts.catalogue.importer;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.db.schema.userenums.UserEnumSpareImportFileFormat;

public class ImporterFactory {

	public static Importer newInstance(UserEnumSpareImportFileFormat format) throws EventException {
		if (format.equals(UserEnumSpareImportFileFormat.SPARE_CSV)) {
			return new CSVImporter();
		}

		String fileFormats = "";
		for (UserEnumSpareImportFileFormat fileFormat : UserEnumSpareImportFileFormat.values()) {
			fileFormats = fileFormat + ", ";
		}
		throw new EventException(
				Util.getMessage("importerfactory.err.file.format", fileFormats.substring(0, fileFormats.length() - 2)),
				1);
	}

}
