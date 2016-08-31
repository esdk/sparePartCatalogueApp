package de.abas.custom.spareParts.catalogue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import de.abas.custom.spareParts.catalogue.utils.AbstractTest;
import de.abas.custom.spareParts.catalogue.utils.CommandRunner;

public class MainStartButtonTest extends AbstractTest implements CommandRunner {

	@Test
	public void startAfterNoFileTest() {
		infosys.setFile("");
		infosys.invokeStart();
		assertThat(getMessage().contains("FOP: Please enter the import file location"), is(true));
	}

	@Test
	public void startAfterTest() throws Exception {
		infosys.setFile("test.csv");
		infosys.invokeStart();

		assertThat(infosys.table().getRowCount(), is(1));
		assertThat(infosys.table().getRow(1).getProductno(), is("1234"));
		assertThat(infosys.table().getRow(1).getPrice().compareTo(new BigDecimal("3.12")), is(0));
		assertThat(infosys.table().getRow(1).getMatchcode(), is("ET1234"));
	}

}
