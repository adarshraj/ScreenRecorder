# ScreenRecorder

A small Java 17 Swing desktop app for recording the screen. Records AVI
natively via the [Monte Media](https://github.com/stephenc/monte-screen-recorder)
library and produces MP4 by recording an intermediate `*.tmp.avi` and
converting it on a background thread with an external `ffmpeg` binary.

If you're looking for a screenshot tool, see the sibling project
[`screenshotcaptor`](../screenshotcaptor) — it owns that job.

## Requirements

- Java 17 or newer
- Maven 3.6+ to build
- `ffmpeg` on your `PATH` (only needed if you want MP4 output)

## Build

```bash
mvn clean package
```

This produces a fat jar at `target/screenrecorder-jar-with-dependencies.jar`.

## Run

```bash
java -jar target/screenrecorder-jar-with-dependencies.jar
```

Run from the project root so `config/app.properties` is reachable. The window
shows a filename field, a format dropdown (`AVI` or `MP4`), and a single
**Start Screen Recording** button. Click it to start, click **Stop Screen
Recording** to finish. If you picked MP4, the button switches to
**Converting...** while ffmpeg runs and re-enables itself when the conversion
is done.

## Configuration

All config lives in `config/app.properties`. The shipped defaults are
cross-platform — both `filePath` and `ffmpeg_path` are blank by design.

| Key | Default | Meaning |
| --- | --- | --- |
| `videoFileTypes` | `AVI,MP4` | Comma-separated formats shown in the dropdown |
| `filePath` | *(blank)* | Output directory. Blank → working directory. |
| `timeFormat` | `yyyyMMdd_HHmmssSSS` | `SimpleDateFormat` pattern used in the auto-generated filename |
| `fileNamePrefix` | `REC` | String prepended to the timestamp |
| `ffmpeg_path` | *(blank)* | Path to the `ffmpeg` binary. Blank → `"ffmpeg"` resolved on `$PATH`. |
| `h264_codec` | `h264` | Video codec passed to `ffmpeg -vcodec` |
| `mp2_codec` | `mp2` | Audio codec passed to `ffmpeg -acodec` |

Set absolute paths only when you need to override the defaults — e.g. point
`ffmpeg_path` at a specific binary, or `filePath` at a fixed output directory.

## How recording works

- **AVI**: the recorder writes directly to your chosen path. No conversion
  step.
- **MP4**: the recorder writes to a temporary `<basename>.tmp.avi`. After
  you click **Stop**, a worker thread runs
  `ffmpeg -y -i <temp> -vcodec <h264_codec> -acodec <mp2_codec> <output>.mp4`.
  On success the temp file is deleted; on failure it is preserved and a
  dialog tells you where to find it, so you never lose a recording to a
  failed conversion.

The conversion runs off the EDT, so the UI stays responsive while ffmpeg
works.

## Logging

SLF4J + Logback. Logs go to both the console and `logs/ScreenRecorder.log`,
with daily rotation under `logs/archived/` (30-day history). ffmpeg's combined
stdout/stderr is logged at `DEBUG`; bump the root level in
`src/main/resources/logback.xml` if you want to see it.

## Internationalization

UI strings live in `src/main/resources/messages.properties` (English) and
`messages_fr.properties` (French). The French file uses `\uXXXX` escapes for
non-ASCII characters so it stays correct regardless of platform encoding.
Add a new locale by dropping in `messages_<lang>.properties`.

## Project layout

```
src/main/java/in/adarshr/screenrecorder/
  ScreenRecorder.java              # JFrame entry point and recording lifecycle
  feature/
    ScreenRecording.java           # Monte recorder lifecycle (start/stop)
    SpecializedScreenRecorder.java # Subclass overriding the output file path
  convert/VideoConverter.java      # Wraps ffmpeg for MP4 conversion
  util/AppUtils.java               # sleep helper
config/app.properties              # Runtime configuration
src/main/resources/                # logback.xml, messages*.properties
docs/ISSUES.md                     # Known issues, fixed bugs, feature ideas
CLAUDE.md                          # Notes for Claude Code
```

## Status and roadmap

The bug list in `docs/ISSUES.md` is currently empty — every known issue and
code-quality item has been fixed. What's still open is in the
**Feature ideas** section: a global stop hotkey, last-used directory
persistence, a recording indicator (red dot / tray / timer), audio capture,
multi-monitor selection, and CLI flag support.

## License

See [`LICENSE`](LICENSE).
