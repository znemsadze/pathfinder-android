package gse.pathfinder.api;

public class Translate {
	static final String LAT = "qwertyuiopasdfghjklzxcvbnmWRTSJZC";
	static final String KA = "ქწერტყუიოპასდფგჰჯკლზხცვბნმჭღთშჟძჩ";

	public static String translate(String text, String from, String to) {
		if (text != null) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				int index = from.indexOf(c);
				if (index != -1) {
					b.append(to.charAt(index));
				} else {
					b.append(c);
				}
			}
			return b.toString();
		}
		return text;
	}

	public static String ka(String text) {
		return translate(text, LAT, KA);
	}
}