package integration;

import java.util.Properties;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PropertiesResolver.class)
class IntegrationTests {

  private final String version;

  IntegrationTests(Properties properties) {
    this.version = properties.getProperty("version", "?");
  }

  @Test
  void version() {
    Assertions.assertEquals("0.2.2-SNAPSHOT", version);
  }

  @Test
  @EnabledIf("isNamedModule")
  void helloWorld() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("java");
    Assertions.assertNotNull(engine, "Engine not found?!");
    Assertions.assertEquals("World", engine.eval("return \"World\";"));
    Assertions.assertEquals(version, engine.getFactory().getEngineVersion());
    Assertions.assertEquals(
        "de.sormuras.javacompilerscriptengine@" + version,
        engine.getClass().getModule().getDescriptor().toNameAndVersion());
  }

  boolean isNamedModule() {
    return IntegrationTests.class.getModule().isNamed();
  }
}
