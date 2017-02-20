package de.abas.custom.spareParts.catalogue;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.abas.custom.spareParts.catalogue.utils.AbstractTest;
import de.abas.erp.common.type.AbasDate;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.db.schema.custom.replacement.SparePart;
import de.abas.erp.db.schema.custom.replacement.SparePartEditor;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.schema.vendor.VendorEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;

public class MainImportButtonTest extends AbstractTest {

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yy");
	private Vendor vendor;
	private SparePart sparePart;

	@Override
	public void cleanup() {
		infosys.abort();
		if (sparePart != null) {
			sparePart.delete();
		}
		if (vendor != null) {
			vendor.delete();
		}
		super.cleanup();
	}

	@Test
	public void importCreateTest() {
		infosys.setVendor(vendor);
		infosys.setFile("ow1/test.csv");

		infosys.invokeStart();

		assertThat(infosys.table().getRowCount(), is(1));

		final Iterable<ReplacementCatalogue.Row> rows = infosys.table().getEditableRows();
		for (final ReplacementCatalogue.Row row : rows) {
			row.setImport(true);
		}

		infosys.invokeStartimport();

		final List<SparePart> spareParts = ctx.createQuery(SelectionBuilder.create(SparePart.class)
				.add(Conditions.eq(SparePart.META.ysparevendor, vendor))
				.add(Conditions.eq(SparePart.META.ysparematchcode, infosys.table().getRow(1).getMatchcode())).build())
				.execute();

		assertThat(spareParts.size(), is(1));

		sparePart = spareParts.get(0);

		assertThat(sparePart.getYspareproductno(), is(infosys.table().getRow(1).getProductno()));
		assertThat(sparePart.getYsparedescr(), is(infosys.table().getRow(1).getDescr()));
		assertThat(sparePart.getYspareprice(), is(infosys.table().getRow(1).getPrice()));
		assertThat(sparePart.getYsparetransferred(), is(true));
		assertThat(DATE_FORMATTER.format(sparePart.getYsparedate().toDate()),
				is(DATE_FORMATTER.format(new AbasDate().toDate())));
		assertThat(DATE_FORMATTER.format(sparePart.getYsparechanged().toDate()),
				is(DATE_FORMATTER.format(new AbasDate().toDate())));
		assertThat(sparePart.getYsparesigned(), is(not("")));
	}

	@Test
	public void importUpdateTest() throws Exception {
		final SparePartEditor editor = ctx.newObject(SparePartEditor.class);
		editor.setSwd("TEST");
		editor.setYsparematchcode("ET1234");
		editor.setYsparevendor(vendor);
		editor.setYsparedate(new AbasDate(yesterday()));
		editor.commit();
		sparePart = editor.objectId();

		assertThat(sparePart.getYspareproductno(), is(""));
		assertThat(sparePart.getYsparedescr(), is(""));
		assertThat(sparePart.getYspareprice(), is(closeTo(BigDecimal.ZERO, new BigDecimal(1))));
		assertThat(sparePart.getYsparetransferred(), is(false));
		assertThat(DATE_FORMATTER.format(sparePart.getYsparedate().toDate()), is(DATE_FORMATTER.format(yesterday())));
		assertThat(sparePart.getYsparesigned(), is(""));

		infosys.setVendor(vendor);
		infosys.setFile("ow1/test.csv");

		infosys.invokeStart();

		assertThat(infosys.table().getRowCount(), is(1));

		final Iterable<ReplacementCatalogue.Row> rows = infosys.table().getEditableRows();
		for (final ReplacementCatalogue.Row row : rows) {
			row.setImport(true);
		}

		infosys.invokeStartimport();

		final List<SparePart> spareParts = ctx.createQuery(SelectionBuilder.create(SparePart.class)
				.add(Conditions.eq(SparePart.META.ysparevendor, vendor))
				.add(Conditions.eq(SparePart.META.ysparematchcode, infosys.table().getRow(1).getMatchcode())).build())
				.execute();

		assertThat(spareParts.size(), is(1));

		ctx.flush();

		assertThat(sparePart.getYspareproductno(), is(infosys.table().getRow(1).getProductno()));
		assertThat(sparePart.getYsparedescr(), is(infosys.table().getRow(1).getDescr()));
		assertThat(sparePart.getYspareprice(), is(infosys.table().getRow(1).getPrice()));
		assertThat(sparePart.getYsparetransferred(), is(true));
		assertThat(DATE_FORMATTER.format(sparePart.getYsparedate().toDate()), is(DATE_FORMATTER.format(yesterday())));
		assertThat(DATE_FORMATTER.format(sparePart.getYsparechanged().toDate()),
				is(DATE_FORMATTER.format(new AbasDate().toDate())));
		assertThat(sparePart.getYsparesigned(), is(not("")));

	}

	@Override
	public void setup() {
		super.setup();
		createVendor();

	}

	private void createVendor() {
		infosys.abort();
		final VendorEditor vendor = ctx.newObject(VendorEditor.class);
		vendor.setSwd("TEST");
		vendor.commit();
		this.vendor = vendor.objectId();
		infosys = ctx.openInfosystem(ReplacementCatalogue.class);
	}

	private Date yesterday() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

}
