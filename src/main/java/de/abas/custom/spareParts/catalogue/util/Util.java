package de.abas.custom.spareParts.catalogue.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.abas.eks.jfop.remote.EKS;

public class Util {

	private final static String MESSAGE_BASE = "de.abas.custom.spareParts.catalogue.messages";

	private static Locale locale = Locale.ENGLISH;

	private static Locale getLocale() {
		try {
			return EKS.getFOPSessionContext().getOperatingLangLocale();
		} catch (final NullPointerException e) {
			return locale;
		}
	}

	public static String getMessage(String key) {
		final ResourceBundle rb = ResourceBundle.getBundle(MESSAGE_BASE, getLocale());
		return rb.getString(key);
	}

	public static String getMessage(String key, Object... params) {
		final ResourceBundle rb = ResourceBundle.getBundle(MESSAGE_BASE, getLocale());
		return MessageFormat.format(rb.getString(key), params);
	}

}
