# LapisCore

A powerful core library for Spigot plugins, this is used for most of my more extensive plugins since it gives me a platform to build off of.

If you think it should have any extra features or you encounter a bug, make an issue and I'll do my best to make it happen

# Getting the API

The LapisCore API is on my personal Maven server.

Simply use the LapisCore repo and dependency and Maven or Gradle will fetch the API

## Gradle

Declare Repo:

```
maven {
    name "lapisMCReleases"
    url "https://maven.lapismc.net/releases"
}
```

Declare Dependency:

```
dependencies {
    implementation "net.lapismc:LapisCore:VERSION"
}
```
## Maven

Declare Repo:

```
<repository>
  <id>LapisMC-releases</id>
  <name>Lapis MC Maven Repo</name>
  <url>https://maven.lapismc.net/releases</url>
</repository>
```

Declare Dependency:

```
<dependency>
  <groupId>net.lapismc</groupId>
  <artifactId>LapisCore</artifactId>
  <version>VERSION</version>
</dependency>
```
