# NodeBOOK app by Shubham - DevLog

## 🏛️ Architecture: MVVM + Clean Architecture
```
UI Layer (Composable)  
↕  
ViewModel Layer (State & Logic)  
↕  
Repository Layer (Single source of truth)  
↕  
Database Layer (Room - local storage)
```

### Why This
- Each layer has one job.
- Easy to debug, easy to scale.
- This is what Google recommends and what is see in every professional codebase.

## 📦 Libraries Used
| Library               | Purpose                               |
|:----------------------|:--------------------------------------|
| Room                  | Local Database (store notes/folder)   |
| Jetpack Compose       | Modern UI toolkit                     |
| ViewModel + StateFlow | Manage UI state                       |
| Navigation Compose    | Move Between Screens                  |
| Hilt                  | Dependency Injection (pro-level glue) |
| Datastore             | Save Settings (Name,Theme)            |


## 💻 Understanding the Data Structure (The "Model")
- Folder Entity: Contains `folderId`, `folderName`, and `icon`.
- Note Entity: Contains `noteId`, `parentFolderId` (this links it to a folder), `title`, `content`, `backgroundColor`, and `timestamp`.

## 📁 Project Folder Structure

```
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
```

## 📂 TWO Gradle Files — Know the Difference

```
NotesApp/  
├── build.gradle.kts          ← Project-level (global settings)  
└── app/  
└── build.gradle.kts      ← App-level (App's libraries) ← we edit this most
```

**Why two?** : Project-level applies to the whole project (like plugin versions). App-level is specific to app module.


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
```
com.notes.app.data.model   
com.notes.app.data.local  
com.notes.app.data.repository  
com.notes.app.di  
com.notes.app.ui.screens  
com.notes.app.ui.components  
com.notes.app.ui.theme  
com.notes.app.ui.viewmodel  
com.notes.app.utils
```

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
```
Folder Table              Note Table  
──────────────            ──────────────────────────  
id = 1  "C++ Notes"  ←── folderId = 1  "Pointers note"  
id = 2  "Python"     ←── folderId = 1  "Memory note"  ←── folderId = 2  "Lists note"  
folderId = null  "Random note"
```
--- 

# Room Database (DAO + Database Class)

## 💡 What is Room?
- Room is Google's official database library.
- It's a wrapper around SQLite (which is what Android uses under the hood) but instead of writing raw SQL everywhere, you write Kotlin interfaces and Room generates all the messy SQL code for you.

> You write:  getNoteById(id) || Room does:  SELECT * FROM notes WHERE id = :id
```
data.local/  
├── FolderDao.kt      ← SQL queries for folders  
├── NoteDao.kt        ← SQL queries for notes    
└── NotesDatabase.kt  ← The actual database instance
```

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
> 
```
Note.kt / Folder.kt          ← What data looks like  
                ↓  
NoteDao / FolderDao          ← How to read/write that data  
                ↓  
NotesDatabase                ← The database that holds it all
```
---

# Repository + Dependency Injection (Hilt)

> 💡 What is a Repository?
- Right now our ViewModels could talk directly to the DAO.
- But professionals add a Repository layer in between.
- Here's why:
  ❌ Without Repository: ViewModel → NoteDao (directly)  
  ✅ With Repository: ViewModel → NoteRepository → NoteDao

- The ViewModel doesn't care where data comes from — local DB, internet API, cache.
- The Repository handles that decision. This is called separation of concerns.

> 💡 What is Hilt (Dependency Injection)?
- This is the concept that trips up most beginners. Let's make it simple.
- Without Hilt — you manually create every object:

```agsl
val db = NotesDatabase.create(context)
val dao = db.noteDao()
val repo = NoteRepository(dao)
val viewModel = NoteViewModel(repo) 
// You manage all of this yourself 😰
```

- With Hilt — you just say "I need this" and Hilt builds it for you:

```
@HiltViewModel
class NoteViewModel @Inject constructor(
private val repository: NoteRepository  // Hilt gives this to you automatically ✅  
)
```

- Hilt is like a smart factory that knows how to build every object in your app.

> What is AndroidManifest.xml?

It's not a UI file at all. Think of it as your app's ID card that tells Android:
- What is this app called?
- What permissions does it need? (camera, internet etc)
- Which Activity opens first?
- What Application class runs at startup?  ← this is why we edit it

> 💡 How Hilt Works — The Full Picture

```
@HiltAndroidApp          ← Starts Hilt in your whole app  
            ↓  
@InstallIn               ← This module lives for the app's lifetime  
            ↓    
@Provides                ← "Here's how to build this object"   
            ↓  
@Inject                  ← "Please give me this object"  
            ↓  
@AndroidEntryPoint       ← "This Activity/Fragment uses Hilt"
```

- Hilt reads all of this at compile time and generates all the wiring code automatically.

---

# 🏗️ Full Architecture So Far
```
NotesApplication  (Hilt starts here)  
        ↓  
DatabaseModule    (Hilt knows how to build DB, DAOs)  
        ↓  
NoteRepository    (Hilt builds this with DAOs injected)  
        ↓  
ViewModel         (will get Repository injected) ← next steps  
        ↓  
UI Screens        (will get ViewModel injected)  ← next steps
```

---

# ViewModels (The Brain of Each Screen)

> 💡 What is a ViewModel?
- A ViewModel sits between UI and Repository.
- It holds all the logic and state for a screen.


UI Screen  →  "Hey ViewModel, user clicked delete"  
ViewModel  →  runs the logic, calls repository  
Repository →  deletes from database  
ViewModel  →  updates state  
UI Screen  →  automatically recomposes ✅

### Why not just put logic inside the UI directly?
- If rotate phone, the UI is destroyed and recreated
- ViewModel survives rotation — data stays safe
- Clean separation — UI just displays, ViewModel just thinks

> 💡 What is StateFlow?

**This is how ViewModel talks back to the UI:**

```
// ViewModel holds this
val notes = MutableStateFlow<List<Note>>(emptyList())
// UI watches this
val notes by viewModel.notes.collectAsState()
// whenever notes changes → UI recomposes automatically ✅
```

Think of it as a walkie talkie — ViewModel broadcasts, UI listens.

### 📄 Files We Create Today
```
ui.viewmodel/
├── NoteViewModel.kt
└── FolderViewModel.kt
```

## 💡 Key Concepts Explained - viewModelScope.launch

```agsl
fun deleteNote(note: Note) {
    viewModelScope.launch {       // opens a coroutine (background thread)
        repository.deleteNote(note)  // runs here safely off main thread
    }
}
```

- Database work can't run on the UI thread.
- `viewModelScope` gives us a safe background thread that automatically cancels when the ViewModel is destroyed.
- No memory leaks.

### The Smart Notes Flow

```agsl
val notes = combine(searchQuery, selectedFolderId)
    .flatMapLatest { (query, folderId) ->
        when {
            query.isNotBlank() -> search results
            folderId != null   -> folder notes
            else               -> all notes
        }
    }
```

- This is reactive logic — whenever searchQuery OR selectedFolderId changes, notes automatically recalculates.
- No manual refresh needed anywhere.

### `stateIn` — Why We Need It
```agsl
.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),  // stop collecting 5s after UI leaves
    initialValue = emptyList()                        // show empty list before DB responds
)
```
- Converts a cold Flow into a hot StateFlow that the UI can safely read from.

# 🏗️ Architecture So Far

```
NotesDatabase
      ↓
NoteDao / FolderDao
      ↓
NoteRepository
      ↓
NoteViewModel / FolderViewModel   ← we are here ✅
      ↓
UI Screens                        ← next steps
```