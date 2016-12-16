package de.abas.custom.spareParts.catalogue.importer;

import java.math.BigDecimal;

public class SparePart {

	private String productNo;
	private String matchCode;
	private String description;

	private BigDecimal price;

	public SparePart(String productNo, String description, String price) {
		this.productNo = productNo;
		matchCode = parse("ET" + productNo);
		this.description = description;
		this.price = new BigDecimal(price);
	}

	public SparePart(String productNo, String matchCode, String description, String price) {
		this.productNo = productNo;
		this.matchCode = parse(matchCode);
		this.description = description;
		this.price = new BigDecimal(price);
	}

	public String getDescription() {
		return description;
	}

	public String getMatchCode() {
		return matchCode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getProductNo() {
		return productNo;
	}

	private String parse(String matchCode) {
		return matchCode.trim().replace(" ", "").replaceAll("/", ".");
	}

}
