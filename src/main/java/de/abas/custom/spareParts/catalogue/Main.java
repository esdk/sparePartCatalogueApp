package de.abas.custom.spareParts.catalogue;

import java.io.File;
import java.util.List;

import de.abas.custom.spareParts.catalogue.creator.Creator;
import de.abas.custom.spareParts.catalogue.importer.Importer;
import de.abas.custom.spareParts.catalogue.importer.ImporterFactory;
import de.abas.custom.spareParts.catalogue.importer.SparePart;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.field.editable.EditableField;
import de.abas.erp.db.field.editable.EditableReferenceField;
import de.abas.erp.db.field.editable.EditableStringField;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.db.schema.vendor.Vendor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = ReplacementCatalogue.class, row = ReplacementCatalogue.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class Main {

	@FieldEventHandler(field = "file", type = FieldEventType.EXIT)
	public void fileExit(ReplacementCatalogue infosys) {
		final String[] file = infosys.getFile().split("\\.");
		infosys.setFormat(file[file.length - 1]);
	}

	@ScreenEventHandler(type = ScreenEventType.ENTER)
	public void screenEnter(ReplacementCatalogue infosys) {
		infosys.table().clear();
		infosys.setFormat("csv");
	}

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(ReplacementCatalogue infosys, ScreenControl screenControl) throws EventException {
		preconditions(infosys, screenControl);
		final Importer importer = ImporterFactory.newInstance(infosys.getFormat());
		final List<SparePart> spareParts = importer.readFile(new File(infosys.getFile()));
		for (final SparePart sparePart : spareParts) {
			TableRow.build(infosys, sparePart);
		}
	}

	@ButtonEventHandler(field = "startimport", type = ButtonEventType.AFTER)
	public void startimportAfter(DbContext ctx, ReplacementCatalogue infosys, ScreenControl screenControl)
			throws EventException {
		checkFieldEmpty(infosys, screenControl, ReplacementCatalogue.META.vendor,
				"Please specifiy vendor of the replacement catalogue");
		if (infosys.table().getRowCount() == 0) {
			throw new EventException("Please specify which table lines to import", 1);
		}
		Creator.newInstance(ctx, infosys).make();
	}

	private void checkFieldEmpty(ReplacementCatalogue infosys, ScreenControl screenControl,
			EditableField<ReplacementCatalogue> field, String message) throws EventException {
		if (field instanceof EditableStringField<?>) {
			if (infosys.getString(field).isEmpty()) {
				screenControl.moveCursor(infosys, field);
				throw new EventException(message, 1);
			}
		} else if (field instanceof EditableReferenceField<?, ?>) {
			if (infosys.getReference(Vendor.class, field.getName()) == null) {
				screenControl.moveCursor(infosys, field);
				throw new EventException(message, 1);
			}
		}
	}

	private void preconditions(ReplacementCatalogue infosys, ScreenControl screenControl) throws EventException {
		infosys.table().clear();
		checkFieldEmpty(infosys, screenControl, ReplacementCatalogue.META.file,
				"Please enter the import file location");
		checkFieldEmpty(infosys, screenControl, ReplacementCatalogue.META.format,
				"Please enter the import file format");
	}

}
