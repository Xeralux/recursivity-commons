organization := "com.recursivity"

name := "recursivity-commons"

version := "0.6-SNAPSHOT"

scalaVersion := "2.10.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "org.scala-lang" % "scalap" % "2.10.0"

pomExtra := {
  <name>{name}</name>
  <description>Recursivity Commons Project POM</description>
  <url>http://github.com/wfaler/recursivity-commons</url>
  <inceptionYear>2010</inceptionYear>
  <organization>
    <name>Recursivity Commons Project</name>
    <url>http://github.com/wfaler/recursivity-commons</url>
  </organization>
  <licenses>
    <license>
      <name>BSD</name>
      <url>http://github.com/wfaler/recursivity-commons/LICENSE</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git://github.com/wfaler/recursivity-commons.git</connection>
    <url>http://github.com/wfaler/recursivity-commons</url>
  </scm>
  <developers>
    <developer>
      <id>wfaler</id>
      <name>Wille Faler</name>
      <url>http://blog.recursivity.com</url>
    </developer>
  </developers>
}
