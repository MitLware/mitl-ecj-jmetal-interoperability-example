lazy val root = (project in file(".")).
  settings(
    name := "MitLECJandJMetalInteropExample",
    version := "0.1.0",
    scalaVersion := "2.12.3"
    , mainClass in (Compile, run) := Some("ecjandjmetalexample.Main")
  )

libraryDependencies ++= Seq(
   "org.junit.jupiter" % "junit-jupiter-api" % "5.7.0" % Test
)

// End ///////////////////////////////////////////////////////////////


