package com.thinkaurelius.titan.blueprints.process;

import com.thinkaurelius.titan.blueprints.LMDBGraphProvider;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessPerformanceSuite;
import org.junit.runner.RunWith;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
@RunWith(ProcessPerformanceSuite.class)
@GraphProviderClass(provider = LMDBGraphProvider.class, graph = TitanGraph.class)
public class LMDBTitanProcessPerformanceTest {
}
