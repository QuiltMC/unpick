package daomephsta.unpick.tests;

import daomephsta.unpick.api.IClassResolver;
import daomephsta.unpick.api.constantmappers.ConstantMappers;
import daomephsta.unpick.api.constantresolvers.ConstantResolvers;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDataDrivenConstantMapperParsing {
	private final Path validSyntax;

	public TestDataDrivenConstantMapperParsing() throws URISyntaxException {
		Path dir = getResource("/v2_test_definitions");
		validSyntax = dir.resolve("valid_syntax.unpick");
	}

	private static Path getResource(String name) throws URISyntaxException {
		return Paths.get(TestDataDrivenConstantMapperParsing.class.getResource(name).toURI());
	}

	@Test
	void testDDCMParseV2() throws IOException {
		File file = validSyntax.toFile();

		try (InputStream stream = Files.newInputStream(file.toPath())) {
			// once classes begin being resolved, we know we have successfully parsed the definitions
			assertThrows(IClassResolver.ClassResolutionException.class, () -> ConstantMappers.dataDriven(new IClassResolver() {
				@Override
				public ClassReader resolveClassReader(String binaryName) throws ClassResolutionException {
					throw new ClassResolutionException(binaryName);
				}

				@Override
				public ClassNode resolveClassNode(String binaryName) throws ClassResolutionException {
					throw new ClassResolutionException(binaryName);
				}
			}, ConstantResolvers.bytecodeAnalysis(new MethodMockingClassResolver()), stream));
		}
	}
}
