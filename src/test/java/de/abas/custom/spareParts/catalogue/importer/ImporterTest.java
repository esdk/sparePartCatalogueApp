package de.abas.custom.spareParts.catalogue.importer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import de.abas.erp.axi.event.EventException;

public class ImporterTest {

	Importer instance = new CSVImporter();

	@Test
	public void readFileFileNotFoundTest() {
		try {
			instance.readFile(new File("does/not/exist"));
			fail("FileNotFoundException leading to EventException expected");
		} catch (final EventException e) {
			assertThat(e.getMessage(), is("File \"exist\" not found"));
		}
	}

	// TODO: further testing

}
