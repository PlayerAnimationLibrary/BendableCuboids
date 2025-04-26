Setup with Gradle:

```groovy
    repositories {
        maven {
            name "zigythebirdMods"
            url "https://maven.zigythebird.com/mods"
        }
    }
    
    dependencies {
        modImplementation "com.zigythebird.bendable_cuboids:bendable_cuboids-${project.bendylib_version}"
        include "com.zigythebird.bendable_cuboids:bendable_cuboids-${project.bendylib_version}"
        //you can find the latest version in GitHub packages
    }
```

Designed to be able to swap and bend cuboids.

The api provides a way to swap a cuboid with priorities, to be multi-mod compatible
(bend like in Mo'bends)

to swap, you have to create a class from MutableModelPart, and implement the methods.

You don't have to use existing bendableCuboid objects, you can create your own, BUT it's highly recommend (it's a lot's of work to code a bendable stuff)

and an example image:D  
![example](https://raw.githubusercontent.com/KosmX/bendy-lib/dev/example.png)  
  
The release branch contains the source of the latest release.
