package integration;

import java.util.Properties;
import java.util.function.Predicate;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

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
  @EnabledIf(ModuleIsNamed.class)
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

  public static class ModuleIsNamed implements Predicate<ExtensionContext> {
    @Override
    public boolean test(ExtensionContext extensionContext) {
      return extensionContext.getRequiredTestClass().getModule().isNamed();
    }
  }
}
