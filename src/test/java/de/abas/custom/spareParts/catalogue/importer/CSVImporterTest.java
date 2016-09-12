package de.abas.custom.spareParts.catalogue.importer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;

import org.junit.Test;

import de.abas.erp.axi.event.EventException;

public class CSVImporterTest {

	CSVImporter instance = new CSVImporter();

	@Test
	public void parseDataInvalidInputTest() {
		try {
			instance.parseData("invalid input");
			fail("Invalid input: EventException expected.");
		} catch (final EventException e) {
			assertThat(e.getMessage(), is(
					"Invalid file format, csv format expected.\nEach line is supposed to have the following format:\nproductNo;description;price[;matchcode]"));
		}
	}

	@Test
	public void parseDataInvalidPriceTest() {
		try {
			instance.parseData("1234;Test spare product;invalid price;");
			fail("Invalid price: EventException expected");
		} catch (final EventException e) {
			assertThat(e.getMessage(), is(
					"Invalid price, could not convert \"invalid price\" to number value.\nPlease note, the decimal separator for price must be \".\"!"));
		}

	}

	@Test
	public void parseDataTest() {
		try {
			final SparePart data1 = instance.parseData("1234;Test spare product;3.12;");
			final SparePart data2 = instance.parseData("1234;Test spare product;3.12;SP/ 1234");

			assertThat(data1.getProductNo(), is("1234"));
			assertThat(data1.getDescription(), is("Test spare product"));
			assertThat(data1.getPrice().compareTo(new BigDecimal("3.12")), is(0));
			assertThat(data1.getMatchCode(), is("ET1234"));

			assertThat(data2.getProductNo(), is("1234"));
			assertThat(data2.getDescription(), is("Test spare product"));
			assertThat(data2.getPrice().compareTo(new BigDecimal("3.12")), is(0));
			assertThat(data2.getMatchCode(), is("SP.1234"));

		} catch (final EventException e) {
			fail("Valid input, no exception should be thrown");
		}
	}

	@Test
	public void readFileFileNotFoundTest() {
		try {
			instance.readFile(new File("notExisting"));
			fail("Not existing file: EventException expected");
		} catch (final EventException e) {
			assertThat(e.getMessage(), is("File \"notExisting\" not found"));
		}
	}

}
