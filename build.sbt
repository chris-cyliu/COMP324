name := "COMP324"

version := "1.0"

lazy val `comp324` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws ,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )