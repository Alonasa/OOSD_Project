# OOSD — Encoding Words with Suffixes

A small Java console application that encodes and decodes text using a mapping of word suffixes. The app presents a menu, lets you choose a mapping file, an input text file, and an optional output file, then performs encoding or decoding accordingly.

Main features:
- Interactive console menu (Runner -> Menu)
- Encode a text file using a mapping table
- Decode a previously encoded file
- Progress output and simple console colouring

## Technologies used
- Java (standard edition)
- Core Java APIs: java.util, java.nio
- No external dependencies

Key packages/classes:
- `ie.atu.sw.Runner` — application entry point
- `ie.atu.sw.Menu` — I/O and workflow control
- `ie.atu.sw.ArraysProcessor`, `ie.atu.sw.UtilMethods` — utilities and data helpers
- `ie.atu.sw.CipherProcessor`, `ie.atu.sw.SuffixProcessor`, `ie.atu.sw.WordsProcessor` — main processing logic
- `ie.atu.sw.FileReader` — file processing utilities
- `ie.atu.sw.ProgressBar`, `ie.atu.sw.ConsoleColour` — UI feedback helpers

## Project layout
- `ie/atu/sw/*.java` — source code
- `bin/` — compiled classes and sample resources (e.g., `textfiles/`, `encodings-10000/`, `out.txt`)

Note: The app assumes/read/writes files relative to where you run it. The menu mentions a default output file `./out.txt`.

## Prerequisites
- Java 17+ (Java 11 may also work, but 17 is recommended)
- Windows PowerShell or a terminal of your choice

To check Java is available:
```
java -version
javac -version
```

## Build
From the project root (where this README.md is located):

Compile sources into `bin`:
```
javac -d bin ie\atu\sw\*.java
```

This will create package folders under `bin` and place `.class` files there.

## Run
Run the main class from `bin` on the classpath:
```
java -cp bin ie.atu.sw.Runner
```

You will see a menu like:
```
(1) Specify Mapping File
(2) Specify Text File to Encode
(3) Specify Output File (default: ./out.txt)
(4) Encode Text File
(5) Decode Text File
(6) Quit
Select Option [1-6]>: 
```

Recommended order:
1. Choose (1) and provide the mapping file path (e.g., `bin\encodings-10000\encodings-10000.csv`).
2. Choose (2) and provide an input text file path (e.g., one of the samples: `bin\textfiles\MobyDickMelville.txt`).
3. Optionally choose (3) to specify a custom output path (otherwise `./out.txt` in the working directory is used).
4. Choose (4) to encode. The result is written to the chosen output file (or `out.txt`).
5. Choose (5) to decode the previously encoded file from the default `./out.txt` using the mapping file.

Paths can be absolute (e.g., `C:\path\to\file.txt`) or relative to the current working directory.

## Tips & Troubleshooting
- If you get "No mapping file specified" or "No input file specified", go back and use options (1) and (2) before encoding.
- Decoding uses `./out.txt` by default; ensure that file exists and contains encoded content.
- Use double backslashes in some shells if needed, but in PowerShell a single backslash in paths is fine.
- If you use an IDE, set the project SDK to Java 17 and run the `ie.atu.sw.Runner` main class.

## License
Educational/demo project. No specific licence is provided in the repository.
