package de.abas.custom.spareParts.catalogue.importer;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.axi.event.EventException;

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

	public static Importer newInstance(String format) throws EventException {
		if (format.equals(Format.CSV.toString())) {
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
