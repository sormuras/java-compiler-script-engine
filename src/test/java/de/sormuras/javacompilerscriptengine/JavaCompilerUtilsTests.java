package de.sormuras.javacompilerscriptengine;

import static de.sormuras.javacompilerscriptengine.JavaCompilerUtils.compile;
import static de.sormuras.javacompilerscriptengine.JavaCompilerUtils.source;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class JavaCompilerUtilsTests {

  @Test
  void compileString() {
    assertEquals("€", compile("€", "enum €{}").getName());
    assertEquals("A", compile("A", "enum A {}").getName());
    assertEquals("A", compile("A", "class A {}").getName());
    assertEquals("A", compile("A", "interface A {}").getName());
    assertEquals("A", compile("A", "@interface A {}").getName());
    assertEquals("a.A", compile("a.A", "package a; enum A {}").getName());
    assertEquals("a.b.A", compile("a.b.A", "package a.b; class A {}").getName());
    assertEquals("a.b.c.A", compile("a.b.c.A", "package a.b.c; interface A {}").getName());
    assertEquals("a.b.c.d.A", compile("a.b.c.d.A", "package a.b.c.d; @interface A {}").getName());
    assertThrows(RuntimeException.class, () -> compile("A", "enum E {}"));
  }

  @Test
  void hi() throws Exception {
    String code = "public class Hi { public String greet(String who) { return \"Hi \" + who;}}";
    JavaFileObject file = source("Hi.java", code);
    ClassLoader loader = compile(file);
    Class<?> hiClass = loader.loadClass("Hi");
    Object hiInstance = hiClass.getConstructor().newInstance();
    Method greetMethod = hiClass.getMethod("greet", String.class);
    assertEquals("Hi world", greetMethod.invoke(hiInstance, "world"));
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("Hello"));
  }

  @Test
  void lastModified() {
    JavaFileObject jfo = new JavaCompilerUtils.CharContentFileObject("abc", "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }

  @Test
  void multipleClassesWithDependingOnEachOther() {
    String codeA = "package a; public class A {}";
    String codeB = "package b; class B extends a.A {}";
    JavaFileObject fileA = source("listing/A.java", codeA);
    JavaFileObject fileB = source("listing/B.java", codeB);
    compile(fileA, fileB);
  }

  @Test
  void syntaxError() {
    assertThrows(Exception.class, () -> compile(source("F.java", "class 1F {}")));
  }
}
