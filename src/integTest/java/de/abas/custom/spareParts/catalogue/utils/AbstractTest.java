package de.abas.custom.spareParts.catalogue.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import de.abas.erp.db.DbContext;
import de.abas.erp.db.DbMessage;
import de.abas.erp.db.Deletable;
import de.abas.erp.db.MessageListener;
import de.abas.erp.db.SelectableObject;
import de.abas.erp.db.infosystem.custom.ow1.ReplacementCatalogue;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.ContextHelper;

public class AbstractTest {

	public DbContext ctx;
	public ReplacementCatalogue infosys;

	String hostname;
	String client;
	int port;
	String password;

	String message = "";

	@After
	public void cleanup() {
		if (infosys.active()) {
			infosys.abort();
		}
		ctx.close();
	}

	public <C extends SelectableObject & Deletable> void deleteObjects(Class<C> type, String field, String value) {
		final List<C> objects = ctx.createQuery(SelectionBuilder.create(type).add(Conditions.eq(field, value)).build())
				.execute();
		for (final C object : objects) {
			object.delete();
		}
	}

	public String getMessage() {
		return message;
	}

	public <C extends SelectableObject> C getObject(Class<C> type, String idno) throws Exception {
		final List<C> objects = ctx.createQuery(SelectionBuilder.create(type).add(Conditions.eq("idno", idno)).build())
				.execute();
		if (objects.size() > 1) {
			throw new Exception("idno not unique");
		}
		if (objects.size() < 1) {
			throw new Exception("no object found with the idno");
		}
		return objects.get(0);
	}

	@Before
	public void setup() {
		loadProperties();
		ctx = ContextHelper.createClientContext(hostname, port, client, password, "Test");
		addDefaultMessageListener();
		infosys = ctx.openInfosystem(ReplacementCatalogue.class);
	}

	private void addDefaultMessageListener() {
		ctx.addMessageListener(new MessageListener() {
			// Displays all text, status and error messages
			@Override
			public void receiveMessage(DbMessage dbMessage) {
				ctx.out().println("|" + dbMessage + "|");
				message = message + dbMessage + "\n";
			}

		});
	}

	private void loadProperties() {
		final Properties pr = new Properties();
		final File configFile = new File("gradle.properties");
		try {
			pr.load(new FileReader(configFile));
			hostname = pr.getProperty("EDP_HOST");
			client = pr.getProperty("EDP_CLIENT");
			port = Integer.parseInt(pr.getProperty("EDP_PORT", "6550"));
			password = pr.getProperty("EDP_PASSWORD");
		} catch (final FileNotFoundException e) {
			throw new RuntimeException("Could not find configuration file " + configFile.getAbsolutePath());
		} catch (final IOException e) {
			throw new RuntimeException("Could not load configuration file " + configFile.getAbsolutePath());
		}
	}

}
