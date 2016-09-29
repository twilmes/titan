package com.thinkaurelius.titan.blueprints.structure;

import com.thinkaurelius.titan.blueprints.LMDBGraphProvider;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.structure.StructurePerformanceSuite;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
@Ignore
@RunWith(StructurePerformanceSuite.class)
@GraphProviderClass(provider = LMDBGraphProvider.class, graph = TitanGraph.class)
public class LMDBTitanStructurePerformanceTest {
}
