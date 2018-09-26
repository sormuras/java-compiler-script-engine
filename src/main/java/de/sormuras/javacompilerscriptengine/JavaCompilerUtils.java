package de.sormuras.javacompilerscriptengine;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/** In-memory file manager and compiler support. */
final class JavaCompilerUtils {

  static class ByteArrayFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream stream;

    ByteArrayFileObject(String canonical, Kind kind) {
      super(URI.create("giacomo:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    byte[] getBytes() {
      return stream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
      this.stream = new ByteArrayOutputStream(2000);
      return stream;
    }
  }

  static class CharContentFileObject extends SimpleJavaFileObject {

    private final String charContent;
    private final long lastModified;

    CharContentFileObject(String uri, String charContent) {
      this(URI.create(uri), charContent);
    }

    CharContentFileObject(URI uri, String charContent) {
      super(uri, Kind.SOURCE);
      this.charContent = charContent;
      this.lastModified = System.currentTimeMillis();
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
      return charContent;
    }

    @Override
    public long getLastModified() {
      return lastModified;
    }
  }

  static class SourceFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream stream;

    SourceFileObject(String canonical, Kind kind) {
      super(URI.create("giacomo:///" + canonical.replace('.', '/') + kind.extension), kind);
    }

    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
      try {
        return stream.toString(StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        if (ignoreEncodingErrors) {
          return stream.toString();
        }
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public OutputStream openOutputStream() {
      this.stream = new ByteArrayOutputStream(2000);
      return stream;
    }
  }

  static class Manager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Map<String, ByteArrayFileObject> map = new HashMap<>();
    private final ClassLoader parent;

    Manager(StandardJavaFileManager standardManager, ClassLoader parent) {
      super(standardManager);
      this.parent = parent != null ? parent : getClass().getClassLoader();
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
      return new SecureLoader(parent, map);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        Location location, String name, Kind kind, FileObject sibling) {
      switch (kind) {
        case CLASS:
          ByteArrayFileObject object = new ByteArrayFileObject(name, kind);
          map.put(name, object);
          return object;
        case SOURCE:
          return new SourceFileObject(name, kind);
        default:
          throw new UnsupportedOperationException("kind not supported: " + kind);
      }
    }

    @Override
    public boolean isSameFile(FileObject fileA, FileObject fileB) {
      return fileA.toUri().equals(fileB.toUri());
    }
  }

  static class SecureLoader extends SecureClassLoader {
    private final Map<String, ByteArrayFileObject> map;

    SecureLoader(ClassLoader parent, Map<String, ByteArrayFileObject> map) {
      super(parent);
      this.map = map;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
      ByteArrayFileObject object = map.get(className);
      if (object == null) {
        throw new ClassNotFoundException(className);
      }
      byte[] bytes = object.getBytes();
      return super.defineClass(className, bytes, 0, bytes.length);
    }
  }

  /** Compile Java source for the given class name, load and return it as a Class instance. */
  static Class<?> compile(String className, String charContent) {
    ClassLoader loader = compile(source(className.replace('.', '/') + ".java", charContent));
    try {
      return loader.loadClass(className);
    } catch (ClassNotFoundException exception) {
      throw new RuntimeException("Class or interface '" + className + "' not found?!", exception);
    }
  }

  static ClassLoader compile(JavaFileObject... units) {
    return compile(null, emptyList(), emptyList(), asList(units));
  }

  /** Convenient {@link JavaCompiler} facade returning a ClassLoader with all compiled units. */
  static ClassLoader compile(
      ClassLoader parent,
      List<String> options,
      List<Processor> processors,
      List<JavaFileObject> units) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    Objects.requireNonNull(compiler, "No system java compiler available - JDK is required!");
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager standardFileManager =
        compiler.getStandardFileManager(diagnostics, Locale.getDefault(), StandardCharsets.UTF_8);
    Manager manager = new Manager(standardFileManager, parent);
    CompilationTask task = compiler.getTask(null, manager, diagnostics, options, null, units);
    if (!processors.isEmpty()) {
      task.setProcessors(processors);
    }
    boolean success = task.call();
    if (!success) {
      throw new RuntimeException("Compilation failed! " + diagnostics.getDiagnostics());
    }
    return manager.getClassLoader(StandardLocation.CLASS_PATH);
  }

  static JavaFileObject source(String uri, String charContent) {
    return source(URI.create(uri), charContent);
  }

  static JavaFileObject source(URI uri, String charContent) {
    return new CharContentFileObject(uri, charContent);
  }
}
