import javax.script.*;
import org.junit.jupiter.api.*;

class BasicTests {
  @Test
  void hello() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("java");
    Assertions.assertNotNull(engine, "Engine not found?!");
    Assertions.assertEquals("World", engine.eval("return \"World\";"));
  }
}
