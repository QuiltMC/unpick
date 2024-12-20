package daomephsta.unpick.impl.constantmappers.datadriven;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import daomephsta.unpick.api.IClassResolver;
import daomephsta.unpick.api.constantresolvers.IConstantResolver;
import daomephsta.unpick.constantmappers.datadriven.parser.UnpickSyntaxException;
import daomephsta.unpick.impl.constantmappers.SimpleAbstractConstantMapper;
import daomephsta.unpick.impl.constantmappers.datadriven.parser.V1Parser;
import daomephsta.unpick.impl.constantmappers.datadriven.parser.v2.V2Parser;
import daomephsta.unpick.impl.representations.AbstractConstantGroup;
import daomephsta.unpick.impl.representations.TargetMethods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Maps inlined values to constants using mappings defined in a file
 * @author Daomephsta
 */
public class DataDrivenConstantMapper extends SimpleAbstractConstantMapper
{
	private static final Logger LOGGER = LogManager.getLogger("unpick");
	private final TargetMethods targetMethods;

	public DataDrivenConstantMapper(IClassResolver classResolver, IConstantResolver constantResolver, InputStream... mappingSources)
	{
		super(new HashMap<>());
		TargetMethods.Builder targetMethodsBuilder = TargetMethods.builder(classResolver);
		for (InputStream mappingSource : mappingSources)
		{
			try
			{
				//Avoid buffering, so that only the version specifier bytes are consumed
				byte[] version = new byte [2];
				mappingSource.read(version);

				// prepend the version to the stream (parsers will expect it to be present)
				List<InputStream> streams = Arrays.asList(new ByteArrayInputStream(version), mappingSource);
				InputStream newMappingSource = new SequenceInputStream(Collections.enumeration(streams));

				if (version[0] == 'v')
				{
					switch (version[1])
					{
					case '1':
						V1Parser.INSTANCE.parse(newMappingSource, constantGroups, targetMethodsBuilder);
						break;

					case '2':
						V2Parser.parse(newMappingSource, constantGroups, targetMethodsBuilder);
						break;

					default :
						throw new UnpickSyntaxException(1, "Unknown version " + (char) version[1]);
					}
				}
				else
					throw new UnpickSyntaxException(1, "Missing version");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		this.targetMethods = targetMethodsBuilder.build();
		LOGGER.info("Loaded " + targetMethods);
		boolean resolved = true;
		for (AbstractConstantGroup<?> group : constantGroups.values())
		{
			group.resolveAllConstants(constantResolver);
			if (!group.isResolved())
				resolved = false;
		}
		if (!resolved)
			throw new RuntimeException("One or more constants failed to resolve, check the log for details");
	}

	@Override
	protected TargetMethods getTargetMethods()
	{
		return targetMethods;
	}
}
