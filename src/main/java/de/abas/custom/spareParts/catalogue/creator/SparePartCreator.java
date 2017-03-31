package de.abas.custom.spareParts.catalogue.creator;

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
import org.apache.log4j.Logger;

public class SparePartCreator extends Creator {

    private static Logger logger = Logger.getLogger(SparePartCreator.class);

    private SparePart sparePart;

    SparePartCreator(DbContext ctx, ReplacementCatalogue infosys) {
        this.ctx = ctx;
        this.infosys = infosys;
    }

    @Override
    public boolean create(ReplacementCatalogue.Row row) {
        logger.info(Util.getMessage("sparepartcreator.log.start.creation"));
        try {
            final SparePartEditor editor = ctx.newObject(SparePartEditor.class);
            editor.setSwd("SPARE" + infosys.getVendor().getIdno());
            editor.setYsparevendor(infosys.getVendor());
            editor.setYspareproductno(row.getProductno());
            editor.setYsparematchcode(row.getMatchcode());
            editor.setYsparedescr(row.getDescr());
            editor.setYspareprice(row.getPrice());
            editor.setYsparedate(new AbasDate());
            editor.setYsparechanged(new AbasDate());
            editor.setYsparesigned(BufferFactory.newInstance(true).getGlobalTextBuffer().getStringValue("operatorCode"));
            editor.setYsparetransferred(true);
            editor.commit();
            logger.info(Util.getMessage("sparepartcreator.log.created", editor.objectId().getIdno()));
            return true;
        } catch (final DBRuntimeException e) {
            logger.error(Util.getMessage("sparepartcreator.log.err.creation", e.getMessage()));
            return false;
        }
    }

    @Override
    protected boolean exists(Vendor vendor, String matchcode) {
        return (initSparePart(vendor, matchcode) != null);
    }

    @Override
    protected boolean update(ReplacementCatalogue.Row row) {
        logger.info(Util.getMessage("sparepartcreator.log.start.updating"));
        SparePartEditor editor = getSparePart().createEditor();
        try {
            editor.open(EditorAction.UPDATE);
            editor.setYspareproductno(row.getProductno());
            editor.setYsparedescr(row.getDescr());
            editor.setYspareprice(row.getPrice());
            editor.setYsparechanged(new AbasDate());
            editor.setYsparesigned(BufferFactory.newInstance(true).getGlobalTextBuffer().getStringValue("operatorCode"));
            editor.setYsparetransferred(true);
            editor.commit();
            logger.info(Util.getMessage("sparepartcreator.log.updated", getSparePart().getIdno()));
            return true;
        } catch (final CommandException e) {
            logger.error(
                    Util.getMessage("sparepartcreator.log.err.updating", getSparePart().getIdno(), e.getMessage()));
            return false;
        } finally {
            if (editor != null && editor.active()) {
                editor.abort();
            }
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
                SelectionBuilder.create(SparePart.class).add(Conditions.eq(SparePart.META.ysparevendor, vendor))
                        .add(Conditions.eq(SparePart.META.ysparematchcode, matchcode)).build());
    }
}
