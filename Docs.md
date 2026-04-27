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
├── build.gradle.kts            ← Project-level (global settings)  
└── app/  
└── build.gradle.kts            ← App-level (App's libraries) ← we edit this most
```

**Why two?** : Project-level applies to the whole project (like plugin versions). App-level is specific to app module.


## 🔄 Sync Gradle

- Look for the "Sync Now" bar that appears at the top
- Or go to File → Sync Project with Gradle Files

## 💡 What?

| What?               | Why?                                                                     |
|---------------------|--------------------------------------------------------------------------|
| libs.versions.toml  | One place for ALL version numbers — change once, updates everywhere      |
| composeBom          | Bill of Materials — Google guarantees all Compose libs                   |
| ksp instead of kapt | Newer, 2x faster annotation processor for Room & Hilt                    |
| hilt                | Pro-grade dependency injection                                           |

---

## Data Models (The Blueprint of Our App)
- A data class is a special Kotlin class whose only job is to hold data.
- Kotlin auto-generates equals(), hashCode(), toString().

## 📁 Create the Package Structure
```
com.node.book.data.model   
com.node.book.data.local  
com.node.book.data.repository  
com.node.book.di  
com.node.book.ui.screens  
com.node.book.ui.components  
com.node.book.ui.theme  
com.node.book.ui.viewmodel  
com.node.book.utils
```

---

## 💡 Annotation
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
folderId = null         "Random note"
```
--- 

# Room Database (DAO + Database Class)

## 💡 What is Room?
- Room is Google's official database library.
- It's a wrapper around SQLite (which is what Android uses under the hood) but instead of writing raw SQL everywhere, we write Kotlin interfaces and Room generates all the messy SQL code for.

> We write:  getNoteById(id) || Room does:  SELECT * FROM notes WHERE id = :id

---

```
data.local/  
├── FolderDao.kt      ← SQL queries for folders  
├── NoteDao.kt        ← SQL queries for notes    
└── NotesDatabase.kt  ← The actual database instance
```

## 💡 Understanding the Key Concepts

> What is a DAO?

**Data Access Object —** it's just an interface where we define every operation our app can do with the database. Room reads this interface and generates the actual implementation at compile time.

> What is Flow?

`fun getAllNotes(): Flow<List<Note>>`
Flow is like a live stream of data. When any note in the database changes, our UI automatically gets the update without us asking for it. Perfect for a notes app.

> What is suspend?

`suspend fun deleteNote(note: Note)`
suspend means "this runs on a background thread". Database operations can't run on the main UI thread — it would freeze app. suspend functions must be called from a coroutine.

> The difference visualized:

Flow    → "Keep watching, tell me whenever data changes"  
suspend → "Do this once in background, tell me when done"

getAllNotes()  → Flow   ✅ (we want live updates)  
deleteNote()  → suspend ✅ (one-time action)

> OnConflictStrategy.REPLACE

If we try to insert a note with an ID that already exists — just replace it. This is how we handle edits cleanly.

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

---

# Navigation Setup (The Roads Between Screens)
- In a multi-screen app need a system to move between screens.
- Jetpack Navigation Compose handles this. 
- Think of it like a GPS system for your app:

```
Home Screen  →  "navigate to NoteEditor"
NavHost      →  finds the route, loads the screen
NoteEditor   →  opens ✅
User hits back
NavHost      →  pops back to Home ✅
```

# 💡 Two Core Concepts
1. Routes — Every Screen Has an Address  
   Just like a website URL:
```
google.com/home      ← website route
google.com/settings  ← website route

notes/home           ← our app route
notes/editor/42      ← our app route (note id = 42)
```

2. NavHost — The Traffic Controller  
   One central place that says:  

```
"If route is home    → show HomeScreen"
"If route is editor  → show NoteEditorScreen"
```

### 📄 Files We Create Today
```
utils/
└── Screen.kt             ← All route definitions

ui/
└── NotesNavHost.kt       ← Navigation graph

MainActivity.kt           ← Updated to use NavHost
```

---

# 💡 Key Concepts Explained

`sealed class Screen(val route: String)`

- A sealed class means only these exact screens exist — nothing else can be added accidentally.
- The compiler knows every possible screen, so if you forget to handle one, it warns you.

### Compare
```
// ❌ Stringly typed — easy to make typos
navController.navigate("hoem")   // typo, crashes at runtime

// ✅ Sealed class — compiler catches mistakes
navController.navigate(Screen.Home.route)  // safe ✅
```

### Passing Data Between Screens

```
// Route definition — like a URL template
"note_editor/{noteId}"

// Actual navigation call
navController.navigate(Screen.NoteEditor.createRoute(noteId = 42))
// becomes → "note_editor/42"

// Receiving end — extracts the value back
val noteId = backStackEntry.arguments?.getInt("noteId")
```

### Why noteId = -1 for New Notes?
```
object NoteEditor : Screen("note_editor/{noteId}") {
    fun createRoute(noteId: Int = -1) = "note_editor/$noteId"
}
```

### We reuse the same screen for both creating and editing:

```
noteId = -1  →  "this is a brand new note"
noteId = 42  →  "load and edit note with id 42"
```

---

# 🏗️ Architecture So Far
```
NotesDatabase
      ↓
NoteDao / FolderDao
      ↓
NoteRepository
      ↓
NoteViewModel / FolderViewModel
      ↓
Screen.kt → route addresses
NotesNavHost → traffic controller   ← we are here ✅
      ↓
Actual Screens                      ← next steps
```

---

# Theme Setup (Colors, Typography, Dark/Light Mode)

## 💡 What is a Theme in Compose?
A theme is a single source of truth for how your app looks. Instead of hardcoding colors everywhere:

```agsl
// ❌ Hardcoded everywhere — nightmare to maintain
Text(color = Color(0xFF1A1A1A))
Card(backgroundColor = Color(0xFFFFFFFF))

// ✅ Themed — change once, updates everywhere
Text(color = MaterialTheme.colorScheme.onSurface)
Card(backgroundColor = MaterialTheme.colorScheme.surface)
```

When user switches dark mode → every color updates automatically. That's the power of theming.

📄 Files We Touch Today

```
ui.theme/
├── Color.kt        ← every color defined here
├── Type.kt         ← fonts and text sizes
└── Theme.kt        ← puts it all together
```

## 🏗️ Architecture So Far

```
NotesDatabase → DAOs → Repository → ViewModels → Navigation
      ↓
Theme (Colors + Typography)   ← we are here ✅
      ↓
UI Screens                    ← next step!
```

---

# Home Screen (The First Screen You'll See!)

## 💡 Plan for Home Screen

```
HomeScreen
├── TopBar        (search bar + hamburger menu icon)
├── DrawerContent (side menu with folders + navigation)
├── NoteCard      (each note in the list)
└── FAB           (floating button to create new note)
```

> Professional rule — never build one giant file. Break UI into small reusable components.

```
ui.components/
├── NoteCard.kt
└── DrawerContent.kt

ui.screens/
└── HomeScreen.kt
```

## The Color Scheme Slots

```
primary          → main brand color (our yellow)
background       → screen background
surface          → cards, sheets, dialogs
onSurface        → text/icons ON top of surface
surfaceVariant   → slightly different surface (dividers, chips)
onSurfaceVariant → secondary text color
error            → delete, warning colors
```

## isSystemInDarkTheme()
`darkTheme: Boolean = isSystemInDarkTheme()`  

- By default we follow the system setting.
- Later in Settings screen we'll override this with the user's manual preference stored in DataStore.

## 🎨 Our Color Palette Visualized

```
Light Mode                    Dark Mode
──────────────────────────    ──────────────────────────
Background  #F2F2F7 (gray)    Background  #1C1C1E (near black)
Surface     #FFFFFF (white)   Surface     #2C2C2E (dark gray)
Primary     #FFD60A (yellow)  Primary     #FFD60A (yellow — same)
Text        #1C1C1E (black)   Text        #FFFFFF (white)
```

## 🏗️ Architecture So Far

```
Database → DAOs → Repository → ViewModels → Navigation → Theme
      ↓
HomeScreen ✅  ← we are here
      ↓
NoteEditorScreen  ← next!
SettingsScreen    ← after that
```

# Note Editor Screen (The Heart of the App)

This is the biggest screen. It has:

- Title + content editing
- Text formatting (bold, italic, size)
- Background color picker
- Folder assignment
- Auto-save

### Files
```
ui.components/
└── FormattingToolbar.kt

ui.screens/
└── NoteEditorScreen.kt
```

