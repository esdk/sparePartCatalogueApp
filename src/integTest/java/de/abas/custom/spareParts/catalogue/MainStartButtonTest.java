package de.abas.custom.spareParts.catalogue;

import de.abas.custom.spareParts.catalogue.utils.CommandRunner;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.esdk.test.util.DoNotFailOnError;
import de.abas.esdk.test.util.EsdkIntegTest;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MainStartButtonTest extends EsdkIntegTest implements CommandRunner {

	ReplacementCatalogue infosys = ctx.openInfosystem(ReplacementCatalogue.class);

	@DoNotFailOnError(message = "ERROR_MESSAGE Cat=ERROR: Please enter the import file location.")
	@Test
	public void startAfterNoFileTest() {
		infosys.setFile("");
		infosys.invokeStart();
	}

	@Test
	public void startAfterTest() {
		infosys.setFile("ow1/test.csv");
		infosys.invokeStart();

		assertThat(infosys.table().getRowCount(), is(1));
		assertThat(infosys.table().getRow(1).getProductno(), is("1234"));
		assertThat(infosys.table().getRow(1).getPrice().compareTo(new BigDecimal("3.12")), is(0));
		assertThat(infosys.table().getRow(1).getMatchcode(), is("ET1234"));
	}

}
