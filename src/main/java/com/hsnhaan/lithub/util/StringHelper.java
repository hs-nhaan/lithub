package com.hsnhaan.lithub.util;

import java.text.Normalizer;

import org.apache.commons.text.WordUtils;

public class StringHelper {

	public static String toSlug(String input) {
		input = input.replaceAll("[đĐ]", "d");
		input = Normalizer.normalize(input, Normalizer.Form.NFD);
		input = input.replaceAll("\\p{M}", "");
		input = input.replaceAll("[^a-zA-Z0-9\\s]", "");
		input = input.trim().replaceAll("\\s+", "-");
		
		return input.toLowerCase();
	}
	
	public static String toTitleCase(String input) {
		return WordUtils.capitalizeFully(input);
	}
	
}
