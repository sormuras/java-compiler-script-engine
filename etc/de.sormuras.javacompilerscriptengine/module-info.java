open module de.sormuras.javacompilerscriptengine {
  requires java.scripting;
  requires jdk.compiler;

  provides javax.script.ScriptEngineFactory with
      de.sormuras.javacompilerscriptengine.JavaCompilerScriptEngineFactory;

  requires org.junit.jupiter.api;
}
