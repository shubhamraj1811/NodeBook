# 09-04-2026

- Created NodeBook android Project
- Selected Empty Activity
- Language : Kotlin
- Minimum SDK : API 26

### Project Structure
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

---

# 12-04-2026

### Create File
ui.viewmodel/
├── NoteViewModel.kt
└── FolderViewModel.kt

**WHY**: ViewModel

- create NoteViewModel and code it
- added search, current folder filter, notes list and actions