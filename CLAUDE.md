# CLAUDE.md

Guidance for Claude Code working in this repository.

## Project

Java 17 Swing desktop app for **screen recording only**. Captures the default screen via the Monte Media `monte-screen-recorder` library. AVI is written natively; MP4 is produced by recording an intermediate `*.tmp.avi` and converting it via an external `ffmpeg` binary on a background thread. Screenshot functionality lives in the sibling `screenshotcaptor` repo and was deliberately removed from this one — see `docs/ISSUES.md` for the rationale and any open items.

- GroupId/Artifact: `groupId:screenrecorder:1.0`
- Main class: `in.adarshr.screenrecorder.ScreenRecorder`
- Build: Maven (`pom.xml`), packaged as a fat jar via `maven-assembly-plugin` (`jar-with-dependencies`)
- Logging: SLF4J + Logback (`src/main/resources/logback.xml`)
- i18n: `messages.properties` / `messages_fr.properties`

## Build & run

```bash
mvn clean package
java -jar target/screenrecorder-jar-with-dependencies.jar
```

The app loads `config/app.properties` at startup (first via classpath, then by relative file path), so run from the project root or ensure `config/app.properties` is on the working-directory path.

## Layout

```
src/main/java/in/adarshr/screenrecorder/
  ScreenRecorder.java              # JFrame entry point, recording lifecycle, ffmpeg dispatch
  feature/
    ScreenRecording.java           # Monte recorder lifecycle; start returns boolean
    SpecializedScreenRecorder.java # Subclass overriding output file path
  convert/VideoConverter.java      # Shells out to ffmpeg; takes Properties via constructor
  util/AppUtils.java               # sleep helper
config/app.properties              # File types, output dir, ffmpeg config
src/main/resources/                # logback.xml, messages*.properties, MANIFEST.MF
docs/ISSUES.md                     # Living bug & improvement list
```

## Recording flow

`ScreenRecorder.actionPerformed` is the entry point for the start/stop button. It is the only place that knows about temp files; `ScreenRecording` and `VideoConverter` both stay format-agnostic.

- **AVI**: `recordingFile == finalOutput`. Recorder writes directly to the user's chosen path. No conversion step.
- **MP4**: `recordingFile = baseName + ".tmp.avi"`, `pendingMp4Output = baseName + ".mp4"`. After `stopRecording`, the button is disabled and re-labeled `button.converting`, and a worker thread runs `VideoConverter.convertToMp4(temp, finalOutput)`. On success the temp file is deleted; on failure a `JOptionPane` warns the user and the temp recording is preserved so no work is lost. UI updates always re-enter the EDT via `SwingUtilities.invokeLater`.
- `ScreenRecording.startRecording` returns `false` (instead of swallowing exceptions) when Monte fails to start. `ScreenRecorder` shows an error dialog and leaves the UI in its idle state.
- `ScreenRecording.stopRecording` is null-safe and clears `screenRecorder` in a `finally` block, so a failed start followed by a stop click no longer NPEs.

## Notes for edits

- `config/app.properties` ships with **blank** `filePath` and `ffmpeg_path`. Blank `filePath` falls through to `Paths.get("").toAbsolutePath()` (i.e. the working directory). Blank `ffmpeg_path` falls through to `"ffmpeg"`, which `ProcessBuilder` resolves on `$PATH`. Set absolute paths only when you need to override those defaults.
- `VideoConverter` is constructed with the already-loaded `Properties` from `ScreenRecorder` — do not have it reload `app.properties` itself. The conversion command is `ffmpeg -y -i <in> -vcodec <h264_codec> -acodec <mp2_codec> <out>`; codecs are configurable via `h264_codec` / `mp2_codec`. ffmpeg stdout/stderr are merged and logged at DEBUG level.
- `VideoConverter.convertToMp4` rejects equal input/output paths (`IllegalArgumentException`). The MP4 flow in `ScreenRecorder.startRecording` ensures the temp `*.tmp.avi` is always distinct from the final `*.mp4`.
- `loadProperties` shows a `JOptionPane` error and calls `System.exit(1)` on failure. Preserve the exit unless you have a good reason — the rest of the app assumes properties are non-null.
- Logback writes to `logs/ScreenRecorder.log` (rolling, 30-day history under `logs/archived/`) relative to the working directory. Both `logs/` and `target/` are gitignored.
- No test sources exist (`src/test` is absent) and no test runner is configured.
- `messages_fr.properties` uses `\uXXXX` escapes for non-ASCII characters — keep it that way to avoid mojibake regardless of platform encoding.
- The recording dropdown is populated solely from the `videoFileTypes` property. Adding a value other than `AVI`/`MP4` will produce a file with a misleading extension because the underlying recorder always writes AVI bytes; only `MP4` has a conversion path.
