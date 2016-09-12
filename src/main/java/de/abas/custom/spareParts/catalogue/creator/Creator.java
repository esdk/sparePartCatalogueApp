package de.abas.custom.spareParts.catalogue.creator;

import org.apache.log4j.Logger;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.db.schema.vendor.Vendor;

public abstract class Creator {

	private static final String ICON_CREATE = "icon:plus";
	private static final String ICON_UPDATE = "icon:replace";
	private static final String ICON_ERROR = "icon:minus";

	private static Logger logger = Logger.getLogger(Creator.class);

	public static Creator newInstance(DbContext ctx, ReplacementCatalogue infosys) {
		return new SparePartCreator(ctx, infosys);
	}

	DbContext ctx;
	ReplacementCatalogue infosys;

	public void make() {
		logger.info(Util.getMessage("creator.log.start.import"));
		final Iterable<ReplacementCatalogue.Row> rows = infosys.table().getRows();
		for (final ReplacementCatalogue.Row row : rows) {
			if (row.getImport()) {
				logger.info(String.format(Util.getMessage("creator.log.import.line", row.getRowNo())));
				if (exists(infosys.getVendor(), row.getMatchcode())) {
					if (update(row)) {
						row.setTransfericon(ICON_UPDATE);
					} else {
						row.setTransfericon(ICON_ERROR);
					}
				} else {
					if (create(row)) {
						row.setTransfericon(ICON_CREATE);
					} else {
						row.setTransfericon(ICON_ERROR);
					}
				}
			}
		}
		logger.info("creator.log.import.complete");
	}

	protected abstract boolean create(ReplacementCatalogue.Row row);

	protected abstract boolean exists(Vendor vendor, String matchcode);

	protected abstract boolean update(ReplacementCatalogue.Row row);

}
