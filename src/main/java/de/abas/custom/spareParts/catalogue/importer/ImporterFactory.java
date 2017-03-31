package de.abas.custom.spareParts.catalogue.importer;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.db.schema.userenums.UserEnumImportfileformat;

public class ImporterFactory {

	public enum Format {
		CSV("csv");
		private final String name;

		Format(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public static Importer newInstance(UserEnumImportfileformat format) throws EventException {
		if (format == UserEnumImportfileformat.CSV) {
			return new CSVImporter();
		}

		String fileFormats = "";
		for (final Format fileFormat : Format.values()) {
			fileFormats = fileFormat + ", ";
		}
		throw new EventException(
				Util.getMessage("importerfactory.err.file.format", fileFormats.substring(0, fileFormats.length() - 2)),
				1);
	}

}
