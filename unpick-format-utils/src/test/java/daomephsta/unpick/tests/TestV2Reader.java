package daomephsta.unpick.tests;

import daomephsta.unpick.constantmappers.datadriven.parser.UnpickSyntaxException;
import daomephsta.unpick.constantmappers.datadriven.parser.v2.UnpickV2Reader;
import daomephsta.unpick.constantmappers.datadriven.parser.v2.UnpickV2Writer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestV2Reader {
	private final Path missingHeader;
	private final Path validSyntax;
	private final Path invalidKeyword;

	public TestV2Reader() throws URISyntaxException {
		Path dir = getResource("/v2_test_files");
		missingHeader = dir.resolve("missing_header.unpick");
		validSyntax = dir.resolve("valid_syntax.unpick");
		invalidKeyword = dir.resolve("invalid_keyword.unpick");
	}

	private static Path getResource(String name) throws URISyntaxException {
		return Paths.get(TestV2Reader.class.getResource(name).toURI());
	}

	@Test
	void testValidRead() throws IOException {
		File file = validSyntax.toFile();

		try (UnpickV2Reader reader = new UnpickV2Reader(Files.newInputStream(file.toPath()))) {
			reader.accept(new UnpickV2Writer());
		}
	}

	@Test
	void testMissingHeader() throws IOException {
		File file = missingHeader.toFile();

		try (UnpickV2Reader reader = new UnpickV2Reader(Files.newInputStream(file.toPath()))) {
			assertThrows(UnpickSyntaxException.class, () -> reader.accept(new UnpickV2Writer()));
		}
	}

	@Test
	void testInvalidKeyword() throws IOException {
		File file = invalidKeyword.toFile();

		try (UnpickV2Reader reader = new UnpickV2Reader(Files.newInputStream(file.toPath()))) {
			assertThrows(UnpickSyntaxException.class, () -> reader.accept(new UnpickV2Writer()));
		}
	}
}
