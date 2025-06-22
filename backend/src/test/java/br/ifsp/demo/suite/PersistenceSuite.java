package br.ifsp.demo.suite;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({"br.ifsp.demo"})
@SuiteDisplayName("All Persistence Tests")
@IncludeTags({"PersistenceTest"})
public class PersistenceSuite { }