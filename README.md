# NodeBOOK app by Shubham

## 🏛️ Architecture: MVVM + Clean Architecture

UI Layer (Composables)  
↕  
ViewModel Layer (State & Logic)  
↕  
Repository Layer (Single source of truth)  
↕  
Database Layer (Room - local storage)

## Why This
- Each layer has one job.
- Easy to debug, easy to scale.
- This is what Google recommends and what you'll see in every professional codebase.

## 📦 Libraries We'll Use
| Library               | Purpose                               |
|:----------------------|:--------------------------------------|
| Room                  | Local Database (store notes/folder)   |
| Jetpack Compose       | Modern UI toolkit                     |
| ViewModel + StateFlow | Manage UI state                       |
| Navigation Compose    | Move Between Screens                  |
| Hilt                  | Dependency Injection (pro-level glue) |
| Datastore             | Save Settings (Name,Theme)            |


## 💻 Understanding the Data Structure (The "Model")

Since we want folders containing notes, we need a One-to-Many relationship.
- Folder Entity: Contains `folderId`, `folderName`, and `icon`.
- Note Entity: Contains `noteId`, `parentFolderId` (this links it to a folder), `title`, `content`, `backgroundColor`, and `timestamp`.
## 📁 Project Folder Structure

com.notes.app/   
├── data/  
│   ├── local/          ← Room DB, DAOs  
│   ├── model/          ← Note, Folder data classes  
│   └── repository/     ← Data access logic  
├── di/                 ← Hilt dependency injection  
├── ui/  
│   ├── screens/        ← Home, NoteEditor, Folder, Settings  
│   ├── components/     ← Reusable UI pieces  
│   ├── theme/          ← Colors, Typography, Dark/Light  
│   └── viewmodel/      ← ViewModels for each screen  
└── utils/              ← Helper functions  

> 💡 Pro tip: The reason we use API 26 as minimum is it covers 95%+ of active Android devices while giving us access to modern APIs we need.