# java-compiler-script-engine
JSR 223 facade for `javac`, the foundation Java compiler

## Deploy to bintray.com

[Deploying 3rd-party JARs to remote repository](https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html)

Module
```
mvn org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy-file
  -DrepositoryId=bintray-sormuras-maven
  -Durl=https://api.bintray.com/maven/sormuras/maven/java-compiler-script-engine/;publish=1
  -DpomFile=etc\pom.xml
  -Dfile=target\main\modules\de.sormuras.javacompilerscriptengine-${version}.jar
  -Dsources=target\main\sources\de.sormuras.javacompilerscriptengine-${version}-sources.jar
  -Djavadoc=target\main\javadoc\java-compiler-script-engine-${version}-javadoc.jar
```
