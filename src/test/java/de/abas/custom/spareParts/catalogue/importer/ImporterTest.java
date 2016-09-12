package de.abas.custom.spareParts.catalogue.importer;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

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

	@Test
	public void readFileTest() throws Exception {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final URL resource = classLoader.getResource("csv/importerTest.csv");
		final File file = new File(resource.toURI());

		final List<SparePart> spareParts = instance.readFile(file);

		assertThat(spareParts.size(), is(3));

		assertThat(spareParts.get(0).getProductNo(), is("1000"));
		assertThat(spareParts.get(0).getMatchCode(), is("ET1000"));
		assertThat(spareParts.get(0).getDescription(), is("First test spare part"));
		assertThat(spareParts.get(0).getPrice(), is(closeTo(new BigDecimal(3.14), new BigDecimal(0.03))));

		assertThat(spareParts.get(1).getProductNo(), is("2000"));
		assertThat(spareParts.get(1).getMatchCode(), is("ET2000"));
		assertThat(spareParts.get(1).getDescription(), is("Second test spare part"));
		assertThat(spareParts.get(1).getPrice(), is(closeTo(new BigDecimal(1.32), new BigDecimal(0.03))));

		assertThat(spareParts.get(2).getProductNo(), is("3000"));
		assertThat(spareParts.get(2).getMatchCode(), is("THIRD"));
		assertThat(spareParts.get(2).getDescription(), is("Third test spare part"));
		assertThat(spareParts.get(2).getPrice(), is(closeTo(new BigDecimal(4.62), new BigDecimal(0.03))));
	}

}
