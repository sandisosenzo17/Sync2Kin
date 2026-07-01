# Sync2Kin

A file synchronization desktop app that watches dedicated folders and automatically shares files across your connected devices, DropBox, OneDrive, and Google Drive.

## What It Does

Inside the SyncKin folder there are folders dedicated to the available storage. Drop a file into the desired folder and it gets distributed to the parent folder of the selected storage. If the file is copied into SyncKin, it will be distributed to all the enabled storage devices. Empty files are not distributed.

- **LAN Sync** — files are shared across devices on the same local network without an internet connection
- **Google Drive Sync** — files are uploaded to Google Drive automatically
- **Non-destructive** — deleting a file locally does not delete it on other devices
- **Duplication zones** — the watched folders are drop zones, not mirrors; after a successful transfer the source file is moved to the Recycle Bin

## Project Status

Currently in active development. Windows desktop app (JavaFX) is the primary target. Android support is planned for a later phase.

## Roadmap

| Phase | Description | Status |
|-------|-------------|--------|
| 1 | File system event detection | ✅ Complete |
| 2 | Google Drive authentication & upload | ✅ Complete |
| 3 | JavaFX GUI application | 🔄 In Progress
| 4 | LAN device discovery | ⏳ Pending |
| 5 | File transfer engine | ⏳ Pending |
| 6 | LAN sync logic & deduplication | ⏳ Pending |
| 7 | WAN support | ⏳ Pending |

## Tech Stack

- **Language** — Java 21
- **UI** — JavaFX
- **Build** — Maven
- **File watching** — `java.nio.file.WatchService`
- **Cloud** — Google Drive Java SDK (OAuth2)

## Folder Structure

```
Sync2Kin/           <- Watched root folder
├── GDrive          <- Drop here to upload to Google Drive
├── DBox            <- Drop here to upload to DropBox
└── Network         <- Drop here to upload to connected devices
```

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sync2kin		<- App logic, UI and settings
│   │   └── resources						<- UI of the app in JavaFX
│   └── test										<- Test logic and UI
├── pom.xml
└── README.md
```

## Getting Started

> Setup instructions will be added as the project matures.
