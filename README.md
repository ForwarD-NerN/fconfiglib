# FConfigLib
**FConfigLib** is a simple JSON configuration library designed specifically for Fabric Mod Loader.

## Features
* **Version compatibility**: FConfigLib is compatible with every Minecraft version, supported by Fabric Loader. It works on **Ornithe**, **LegacyFabric**, **Babric**, and other such projects.

* **Support for other games**: This library is not dependent on Minecraft code, it can be run on any other Java game loaded through Fabric Loader.

* **Easy to use API**: FConfigLib has annotation-based value limiters and validators. It allows for safe version conversion via config fixers.



Java 8+ is required in order for this mod to work.

## For mod devs
<details>
<summary>How to add to your project</summary>
<br>
 Add JitPack to your repositories list in build.gradle.

```groovy
maven {
    name "JitPack"
    url "https://jitpack.io"
}
```
<br>
 Add the following line to your dependencies in build.gradle.

```groovy
dependencies {
  ...
  modImplementation "com.github.ForwarD-NerN:fconfiglib:1.0.0")
}
```

</details>

Currently, there is no documentation available, however, you can look at an example [here](https://github.com/ForwarD-NerN/fconfiglib/blob/master/src/main/java/ru/nern/fconfiglib/example/ExampleMod.java).



