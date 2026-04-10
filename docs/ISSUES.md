# Known issues and improvements

Living checklist of bugs, code-quality issues, and feature ideas in this repo.
Tick items off as they get fixed; add new ones as they are found.

> **Scope notes (2026-04-10):**
> 1. The screenshot UI was deleted from this app — `FullScreenCapture`,
>    `UserDefinedCapture`, and `capture/ScreenCapture` are gone, and the
>    dropdown is video-only. Screenshots live in the sibling `screenshotcaptor`
>    repo.
> 2. The recorder + MP4 path was redesigned. AVI is recorded natively; MP4
>    records to a temp `*.tmp.avi` and converts to a distinct `*.mp4` via
>    ffmpeg on a background thread. `VideoConverter` now takes `Properties`
>    via its constructor, defaults `ffmpeg_path` to `"ffmpeg"` (PATH lookup),
>    and rejects equal input/output paths.
>
> All bugs and code-quality items below are resolved as of this date. Only the
> feature-ideas section remains open.

## Bugs (real, will misbehave today)

- [x] ~~**Recorder writes AVI under any extension.**~~ *Resolved: the dropdown
  is now `AVI, MP4`. AVI is written natively. MP4 is produced by recording an
  intermediate `*.tmp.avi` and converting to a distinct `*.mp4` via ffmpeg.*
- [x] ~~**MP4 "conversion" reads and writes the same path.**~~ *Resolved:
  `VideoConverter.convertToMp4(input, output)` rejects equal paths and
  `ScreenRecorder.startRecording` always uses a distinct `*.tmp.avi` for the
  temp recording. Conversion runs on a worker thread (`mp4-convert`) so the
  EDT is no longer blocked, and the start button is re-labeled
  `button.converting` while it runs. On failure the temp recording is
  preserved and the user is shown a `JOptionPane`.*
- [x] ~~**Screenshot buttons accept video extensions.**~~ *Resolved by deleting
  the screenshot buttons. The dropdown is now video-only.*
- [x] ~~**`messages_fr.properties` is mojibake.**~~ *Resolved: file rewritten
  using `\uXXXX` escapes for accented characters.*
- [x] ~~**Windows-only `filePath` default in `app.properties`.**~~ *Resolved:
  shipped defaults are now blank for both `filePath` and `ffmpeg_path`. Blank
  `filePath` falls through to `Paths.get("").toAbsolutePath()`; blank
  `ffmpeg_path` falls through to `"ffmpeg"`, which `ProcessBuilder` resolves
  on `$PATH`.*
- [x] ~~**`UserDefinedCapture` NPEs on a click without drag.**~~ *Resolved: the
  whole class was deleted with the screenshot UI.*
- [x] ~~**Recorder NPEs on stop after a failed start.**~~ *Resolved:
  `ScreenRecording.stopRecording` is now null-safe and clears the field in a
  `finally` block. `startRecording` returns `boolean`, and `ScreenRecorder`
  shows a `JOptionPane` and leaves the UI idle if it returns false.*

## Code-quality issues

- [x] ~~**`System.exit(1)` on missing properties is harsh.**~~ *Resolved:
  `loadProperties` now shows a `JOptionPane` error before exiting, and the
  stream is closed via try-with-resources (it was previously leaked).*
- [x] ~~**`VideoConverter` reloads `app.properties` itself.**~~ *Resolved: the
  constructor now takes `Properties`, and the duplicate `loadProperties`
  helper is gone.*
- [x] ~~**`commons-cli` dependency declared but unused.**~~ *Resolved: removed
  from `pom.xml`. CLI flag support is now a feature idea, not dead weight.*
- [x] ~~**`AppUtils.sleep` swallows interrupt status.**~~ *Resolved: now calls
  `Thread.currentThread().interrupt()` before rethrowing.*
- [x] ~~**Dead code in `ScreenCapture`.**~~ *Resolved: `ScreenCapture` deleted.*
- [x] ~~**Stray `System.out.println` in `UserDefinedCapture`.**~~ *Resolved:
  `UserDefinedCapture` deleted.*
- [x] ~~**`KeyListener` implemented but never registered.**~~ *Resolved:
  `ScreenRecorder` no longer implements `KeyListener`. (A global hotkey to stop
  recording is still listed under feature ideas.)*
- [x] ~~**`target/` is not in `.gitignore`.**~~ *Resolved: added under a
  `### Maven ###` section.*
- [x] ~~**No `<project.reporting.outputEncoding>` in `pom.xml`.**~~ *Resolved:
  added alongside the existing `project.build.sourceEncoding`.*

## Feature ideas (not bugs)

- [ ] Global "stop recording" hotkey — today the only way to stop is to make
  the floating window visible again.
- [ ] Remember last-used directory / extension across runs.
- [ ] Visual recording indicator (red dot, tray icon, elapsed timer).
- [ ] Audio capture — `audioFormat` is currently `null` in the Monte recorder.
- [ ] Multi-monitor support — currently always grabs `getDefaultScreenDevice()`.
- [ ] CLI flag support (`--output-dir`, `--format`, `--headless`, etc.) — the
  `commons-cli` dependency was removed when it was found unused; reintroduce
  it if/when this is wired up.
