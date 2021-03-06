/**
 * Provides the Java Compiler Script Engine factory.
 */
module de.sormuras.javacompilerscriptengine {
  requires java.scripting;
  requires jdk.compiler;

  provides javax.script.ScriptEngineFactory with
      de.sormuras.javacompilerscriptengine.JavaCompilerScriptEngineFactory;
}
