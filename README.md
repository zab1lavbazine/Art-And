# 📌 ArtHub (Pinterest-like Android App)

**ArtHub** is a mobile application inspired by Pinterest, and was done as part of SP2 project 

---

## ✨ Features

- 🖼️ Upload and view photo posts
- 📁 Save posts into custom folders (collections)
- 💬 Write and view comments under posts
- 🔍 Explore visual content from other users
- ❤️ Clean and responsive Material UI

---

## 🛠 Tech Stack

| Component               | Description                                      |
|------------------------|--------------------------------------------------|
| **Kotlin**             | Primary programming language                     |
| **Retrofit**           | HTTP client for networking and API calls         |
| **Room Database**      | Local storage for offline data persistence       |
| **Koin**               | Lightweight dependency injection framework       |
| **Material Design 3**  | Modern UI components and styling                 |
| **Material Design 1**  | Some legacy components for specific views        |

---

## 🧩 Architecture

The app follows a **MVVM** (Model-View-ViewModel) architecture for better separation of concerns and testability.

- **ViewModels** handle UI logic and expose data to the views
- **Repositories** interact with both the API (via Retrofit) and local database (Room)
- **Dependency Injection** is managed with Koin modules for scalability and clarity

---

## 🚀 Getting Started

### Prerequisites

| Property               | Value             |
| ---------------------- | ----------------- |
| **Namespace**          | `com.example.art` |
| **Compile SDK**        | 34                |
| **Target SDK**         | 34                |
| **Min SDK**            | 24                |
| **Compose Compiler**   | 1.5.1             |
| **Java Compatibility** | Java 8            |




---

### ⚠️ Server Dependency

This application is **client-side only** and relies entirely on a backend server for its core functionality (e.g., posting, saving, loading, commenting). Please ensure the server is running and reachable via the specified API URL (see `strings.xml`) before launching the app.
