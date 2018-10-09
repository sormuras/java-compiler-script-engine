package de.sormuras.javacompilerscriptengine;

import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class JavaCompilerScriptEngineFactory implements ScriptEngineFactory {

  @Override
  public String getEngineName() {
    return (String) getParameter(ScriptEngine.ENGINE);
  }

  @Override
  public String getEngineVersion() {
    return (String) getParameter(ScriptEngine.ENGINE_VERSION);
  }

  @Override
  public List<String> getExtensions() {
    return Collections.singletonList("java");
  }

  @Override
  public List<String> getMimeTypes() {
    return Collections.singletonList("text/java");
  }

  @Override
  public List<String> getNames() {
    return Collections.singletonList("java");
  }

  @Override
  public String getLanguageName() {
    return (String) getParameter(ScriptEngine.LANGUAGE);
  }

  @Override
  public String getLanguageVersion() {
    return (String) getParameter(ScriptEngine.LANGUAGE_VERSION);
  }

  @Override
  public Object getParameter(final String key) {
    switch (key) {
      case ScriptEngine.NAME:
        return "Giacomo";
      case ScriptEngine.ENGINE:
        return "Java Compiler Script Engine";
      case ScriptEngine.ENGINE_VERSION:
        return "0.1-SNAPSHOT";
      case ScriptEngine.LANGUAGE:
        return "Java";
      case ScriptEngine.LANGUAGE_VERSION:
        return System.getProperty("java.version");
      default:
        return null;
    }
  }

  @Override
  public String getMethodCallSyntax(String object, String method, String... args) {
    return object + '.' + method + '(' + String.join(", ", args) + ')';
  }

  @Override
  public String getOutputStatement(String toDisplay) {
    return "System.out.println(\"" + toDisplay + "\")";
  }

  @Override
  public String getProgram(String... statements) {
    String delimiter = ";\n";
    return String.join(delimiter, statements) + delimiter;
  }

  @Override
  public ScriptEngine getScriptEngine() {
    return new JavaCompilerScriptEngine(this);
  }
}
