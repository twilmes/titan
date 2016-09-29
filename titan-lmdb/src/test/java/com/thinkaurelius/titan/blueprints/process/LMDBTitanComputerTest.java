package com.thinkaurelius.titan.blueprints.process;

import com.thinkaurelius.titan.blueprints.LMDBGraphComputerProvider;
import com.thinkaurelius.titan.core.TitanGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessComputerSuite;
import org.junit.runner.RunWith;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */
@RunWith(ProcessComputerSuite.class)
@GraphProviderClass(provider = LMDBGraphComputerProvider.class, graph = TitanGraph.class)
public class LMDBTitanComputerTest {
}