# LapisCore

A powerful core library for Spigot plugins, this is used for most of my more extensive plugins since it gives me a platform to build off of.

If you think it should have any extra features or you encounter a bug, make an issue and I'll do my best to make it happen

# Getting the API

The LapisCore API is on my personal Nexus server.

Simply use the LapisCore repo and dependency and Maven or Gradle will fetch the API

## Gradle

Declare Repo:

```
    maven {
        name = "lapismc-repo"
        url = "https://maven.lapismc.net/repository/maven/"
    }
```

Declare Dependency:

```
dependencies {
    implementation 'net.lapismc:LapisCore:1.12.5'
}
```
## Maven

Declare Repo:

```
        <repository>
            <id>lapismc-repo</id>
            <url>https://maven.lapismc.net/repository/maven/</url>
        </repository>
```

Declare Dependency:

```
<dependency>
  <groupId>net.lapismc</groupId>
  <artifactId>LapisCore</artifactId>
  <version>1.7.2</version>
</dependency>
```
