package com.thinkaurelius.titan.blueprints.process.groovy;

import com.thinkaurelius.titan.blueprints.LMDBGraphComputerProvider;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.GroovyProcessComputerSuite;
import org.junit.runner.RunWith;

/**
 * @author Bryn Cooke
 */
@RunWith(GroovyProcessComputerSuite.class)
@GraphProviderClass(provider = LMDBGraphComputerProvider.class, graph = TitanGraph.class)
public class LMDBTitanGroovyProcessComputerTest {
}