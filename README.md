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

## 📂 You Have TWO Gradle Files — Know the Difference

NotesApp/  
├── build.gradle.kts          ← Project-level (global settings)  
└── app/  
└── build.gradle.kts      ← App-level (YOUR app's libraries) ← we edit this most   

> 💡 Why two? Project-level applies to the whole project (like plugin versions). App-level is specific to your app module.

## 🔄 Now Sync Gradle
> After saving all 3 files:

- Look for the "Sync Now" bar that appears at the top
- Or go to File → Sync Project with Gradle Files

## 💡 What Did We Just Do?

| What?               | Why?                                                                      |
|---------------------|---------------------------------------------------------------------------|
| libs.versions.toml  | One place for ALL version numbers — change once, updates everywhere       |
| composeBom          | Bill of Materials — Google guarantees all Compose libs                    |
| ksp instead of kapt | Newer, 2x faster annotation processor for Room & Hilt                     |
| hilt                | Pro-grade dependency injection — we'll explain this deeply when we use it |

---

## Data Models (The Blueprint of Our App)
A data class is a special Kotlin class whose only job is to hold data. Kotlin auto-generates equals(), hashCode(), toString() for you.

## 📁 First — Create the Package Structure
com.notes.app.data.model   
com.notes.app.data.local  
com.notes.app.data.repository  
com.notes.app.di  
com.notes.app.ui.screens  
com.notes.app.ui.components  
com.notes.app.ui.theme  
com.notes.app.ui.viewmodel  
com.notes.app.utils  

> 💡 Why do this first? Clean package structure = clean mind.  
> Every file has a home.  
> This is how professional projects are organized — you can onboard a new developer and they instantly know where everything lives.

## 💡 Let's Understand Every Annotation
| Annotation                       | Meaning                                                                              |
|----------------------------------|--------------------------------------------------------------------------------------|
| @Entity                          | Tells Room "this class = a database table                                            |
| @PrimaryKey(autoGenerate = true) | Room auto-assigns a unique ID to every row                                           |
| @ForeignKey                      | Links notes to folders — like a relationship in SQL                                  |
| onDelete = SET_NULL              | If you delete a folder, its notes don't get deleted — they just become "unfoldered"  |

### 🔗 The Relationship Visualized

Folder Table              Note Table  
──────────────            ──────────────────────────  
id = 1  "C++ Notes"  ←── folderId = 1  "Pointers note"  
id = 2  "Python"     ←── folderId = 1  "Memory note"  
←── folderId = 2  "Lists note"  
folderId = null  "Random note"  

--- 

# Room Database (DAO + Database Class)

## 💡 What is Room?
- Room is Google's official database library.
- It's a wrapper around SQLite (which is what Android uses under the hood) but instead of writing raw SQL everywhere, you write Kotlin interfaces and Room generates all the messy SQL code for you.

> You write:  getNoteById(id) || Room does:  SELECT * FROM notes WHERE id = :id

data.local/  
├── FolderDao.kt      ← SQL queries for folders  
├── NoteDao.kt        ← SQL queries for notes    
└── NotesDatabase.kt  ← The actual database instance  

## 💡 Understanding the Key Concepts

> What is a DAO?

**Data Access Object —** it's just an interface where you define every operation your app can do with the database. Room reads this interface and generates the actual implementation at compile time.

> What is Flow?

`fun getAllNotes(): Flow<List<Note>>`
Flow is like a live stream of data. When any note in the database changes, your UI automatically gets the update without you asking for it. Perfect for a notes app.

> What is suspend?

`suspend fun deleteNote(note: Note)`
suspend means "this runs on a background thread". Database operations can't run on the main UI thread — it would freeze your app. suspend functions must be called from a coroutine.

> The difference visualized:

Flow    → "Keep watching, tell me whenever data changes"  
suspend → "Do this once in background, tell me when done"  

getAllNotes()  → Flow   ✅ (we want live updates)  
deleteNote()  → suspend ✅ (one-time action)  

> OnConflictStrategy.REPLACE

If you try to insert a note with an ID that already exists — just replace it. This is how we handle edits cleanly.

> 🏗️ The Full Picture So Far

Note.kt / Folder.kt          ← What data looks like  
            ↓  
NoteDao / FolderDao          ← How to read/write that data  
            ↓  
NotesDatabase                ← The database that holds it all  