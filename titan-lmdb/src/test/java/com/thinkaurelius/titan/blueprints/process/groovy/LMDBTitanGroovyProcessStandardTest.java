package com.thinkaurelius.titan.blueprints.process.groovy;

import com.thinkaurelius.titan.blueprints.LMDBGraphProvider;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.GroovyProcessStandardSuite;
import org.junit.runner.RunWith;

/**
 * @author Bryn Cooke
 */
@RunWith(GroovyProcessStandardSuite.class)
@GraphProviderClass(provider = LMDBGraphProvider.class, graph = TitanGraph.class)
public class LMDBTitanGroovyProcessStandardTest {
}