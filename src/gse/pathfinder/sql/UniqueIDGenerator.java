package gse.pathfinder.sql;

import java.util.UUID;

public class UniqueIDGenerator {
	static String generateId() {
		return System.currentTimeMillis() + UUID.randomUUID().toString();
	}
}
