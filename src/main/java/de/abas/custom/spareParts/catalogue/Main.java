package de.abas.custom.spareParts.catalogue;

import java.io.File;
import java.util.List;

import de.abas.custom.spareParts.catalogue.importer.CSVImporter;
import de.abas.custom.spareParts.catalogue.importer.Importer;
import de.abas.custom.spareParts.catalogue.importer.SparePart;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = ReplacementCatalogue.class, row = ReplacementCatalogue.Row.class)
@RunFopWith(EventHandlerRunner.class)
public class Main {

	@ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
	public void startAfter(DbContext ctx, ReplacementCatalogue infosys, ScreenControl screenControl)
			throws EventException {
		preconditions(infosys, screenControl);
		final Importer importer = new CSVImporter();
		final List<SparePart> spareParts = importer.readFile(new File(infosys.getFile()));
		for (final SparePart sparePart : spareParts) {
			TableRow.build(infosys, sparePart);
		}

	}

	private void preconditions(ReplacementCatalogue infosys, ScreenControl screenControl) throws EventException {
		infosys.table().clear();
		if (infosys.getFile().isEmpty()) {
			throw new EventException("Please enter the import file location", 1);
		}
	}

}
