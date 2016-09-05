package de.abas.custom.spareParts.catalogue;

import de.abas.custom.spareParts.catalogue.importer.SparePart;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;

public class TableRow {

	public static void build(ReplacementCatalogue infosys, SparePart sparePart) {
		final TableRow tableRow = new TableRow(infosys, sparePart);
		tableRow.init();
		tableRow.fill();
	}

	ReplacementCatalogue infosys;
	ReplacementCatalogue.Row row;
	SparePart sparePart;

	public TableRow(ReplacementCatalogue infosys, SparePart sparePart) {
		this.infosys = infosys;
		this.sparePart = sparePart;
	}

	private void fill() {
		row.setProductno(sparePart.getProductNo());
		row.setDescr(sparePart.getDescription());
		row.setMatchcode(sparePart.getMatchCode());
		row.setPrice(sparePart.getPrice());
	}

	private void init() {
		row = infosys.table().appendRow();
	}

}
