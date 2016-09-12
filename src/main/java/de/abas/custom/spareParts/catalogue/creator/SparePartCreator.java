package de.abas.custom.spareParts.catalogue.creator;

import org.apache.log4j.Logger;

import de.abas.custom.spareParts.catalogue.util.Util;
import de.abas.erp.common.type.AbasDate;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.exception.CommandException;
import de.abas.erp.db.exception.DBRuntimeException;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.db.schema.custom.replacement.SparePart;
import de.abas.erp.db.schema.custom.replacement.SparePartEditor;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.QueryUtil;
import de.abas.jfop.base.buffer.BufferFactory;

public class SparePartCreator extends Creator {

	private static Logger logger = Logger.getLogger(SparePartCreator.class);

	SparePart sparePart;

	protected SparePartCreator(DbContext ctx, ReplacementCatalogue infosys) {
		this.ctx = ctx;
		this.infosys = infosys;
	}

	@Override
	public boolean create(ReplacementCatalogue.Row row) {
		logger.info(Util.getMessage("sparepartcreator.log.start.creation"));
		try {
			final SparePartEditor editor = ctx.newObject(SparePartEditor.class);
			editor.setSwd("SPARE" + infosys.getVendor().getIdno());
			editor.setYvendor(infosys.getVendor());
			editor.setYproductno(row.getProductno());
			editor.setYmatchcode(row.getMatchcode());
			editor.setYdescr(row.getDescr());
			editor.setYprice(row.getPrice());
			editor.setYdate(new AbasDate());
			editor.setYchanged(new AbasDate());
			editor.setYsigned(BufferFactory.newInstance(true).getGlobalTextBuffer().getStringValue("operatorCode"));
			editor.setYtransferred(true);
			editor.commit();
			logger.info(Util.getMessage("sparepartcreator.log.created", editor.objectId().getIdno()));
			return true;
		} catch (final DBRuntimeException e) {
			logger.error(Util.getMessage("sparepartcreator.log.err.creation", e.getMessage()));
			return false;
		}
	}

	private SparePart getSparePart() {
		return sparePart;
	}

	private SparePart initSparePart(Vendor vendor, String matchcode) {
		setSparePart(vendor, matchcode);
		return sparePart;
	}

	private void setSparePart(Vendor vendor, String matchcode) {
		sparePart = QueryUtil.getFirst(ctx,
				SelectionBuilder.create(SparePart.class).add(Conditions.eq(SparePart.META.yvendor, vendor))
						.add(Conditions.eq(SparePart.META.ymatchcode, matchcode)).build());
	}

	@Override
	protected boolean exists(Vendor vendor, String matchcode) {
		return (initSparePart(vendor, matchcode) == null ? false : true);
	}

	@Override
	protected boolean update(ReplacementCatalogue.Row row) {
		logger.info(Util.getMessage("sparepartcreator.log.start.updating"));
		SparePartEditor editor = null;
		try {
			editor = getSparePart().createEditor().open(EditorAction.UPDATE);
			editor.setYproductno(row.getProductno());
			editor.setYdescr(row.getDescr());
			editor.setYprice(row.getPrice());
			editor.setYchanged(new AbasDate());
			editor.setYsigned(BufferFactory.newInstance(true).getGlobalTextBuffer().getStringValue("operatorCode"));
			editor.setYtransferred(true);
			editor.commit();
			logger.info(Util.getMessage("sparepartcreator.log.updated", getSparePart().getIdno()));
			return true;
		} catch (final CommandException e) {
			logger.error(
					Util.getMessage("sparepartcreator.log.err.updating", getSparePart().getIdno(), e.getMessage()));
			return false;
		}
	}
}
