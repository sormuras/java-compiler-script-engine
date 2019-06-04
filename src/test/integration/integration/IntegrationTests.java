package integration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IntegrationTests {

  @Test
  void helloWorld() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("java");
    Assertions.assertNotNull(engine, "Engine not found?!");
    Assertions.assertEquals("World", engine.eval("return \"World\";"));
    Assertions.assertEquals("0.2.1", engine.getFactory().getEngineVersion());
    Assertions.assertEquals(
        "de.sormuras.javacompilerscriptengine@0.2.1",
        engine.getClass().getModule().getDescriptor().toNameAndVersion());
  }
}
