## Dependencies
1. JavaFX SDK 21.0.7
## How to use
### JavaFX
In the makefile (Linux) or make.bat file (Windows), change the JAVAFX path into the installation path of your JavaFX SDK.

Then use the make files to compile or run the program
Tip: use make help, or make.bat help to see available commands

If using VSCode, add a ```.vscode``` folder to the project's root, then create a settings.json file. Inside, add
```
{
    "java.project.referencedLibraries": [
        "{path_to_javafx}", // Replace to actual path to JavaFX's Jar file
    ]
}
```