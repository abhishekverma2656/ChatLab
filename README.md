# 💬 ChatLab

### A Simple, Secure & Real-Time Chat Application

<p align="center">
  <b>Connect. Chat. Stay Close.</b>
</p>

ChatLab is a real-time messaging application built to make conversations simple, smooth, and secure. Inspired by modern messaging platforms, ChatLab allows users to connect, exchange messages, and share their moments through a clean and intuitive interface.

---

## ✨ Features

- 🔐 **Secure Authentication** — OTP-based registration and login with JWT authentication.
- 💬 **Real-Time Messaging** — Send and receive messages instantly.
- 👥 **User Search** — Find and connect with other users.
- 🟢 **Online Communication** — Real-time communication using WebSockets.
- 🖼️ **Profile Management** — Customize your profile with a profile picture.
- 🔒 **Message Encryption** — AES-based message encryption.
- 📱 **Modern UI** — A smooth, dark-themed interface inspired by modern chat applications.
- 📨 **Chat Management** — View conversations and recent messages.

---

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- MySQL
- WebSocket
- JWT
- AES Encryption

### Frontend
- React Native
- Expo
- Expo Router
- JavaScript
- STOMP WebSocket Client

---

## 🏗️ Architecture

ChatLab follows a layered backend architecture:

```text
ChatLab
│
├── Frontend
│   ├── React Native
│   ├── Expo Router
│   └── STOMP WebSocket Client
│
└── Backend
    ├── Controller Layer
    ├── Service Layer
    ├── Repository Layer
    ├── Entity Layer
    ├── DTOs
    └── Security & Encryption
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have installed:

- Java
- Maven
- Node.js
- MySQL
- Android Studio
- Expo CLI / Expo tooling

### 1. Clone the Repository

```bash
git clone https://github.com/abhishekverma2656/ChatLab.git
```

```bash
cd ChatLab
```

### 2. Configure MySQL

Create a MySQL database:

```sql
CREATE DATABASE chatlab;
```

Configure your database credentials in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chatlab
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Configure your JWT and encryption secrets using environment variables.

### 3. Run the Backend

```bash
mvn spring-boot:run
```

The backend will start on:

```text
http://localhost:8080
```

### 4. Run the Frontend

Clone the frontend repository:

```bash
git clone https://github.com/brajesh9329/ChatApplication.git
```

```bash
cd ChatApplication
```

Install dependencies:

```bash
npm install
```

Start Expo:

```bash
npx expo start
```

Run the application using an Android emulator or a compatible Expo environment.

---

## 🎨 UI Preview

> Screenshots of the application will be added here.

| Home Screen | Chat Screen | Profile Screen |
|---|---|---|
| Coming Soon | Coming Soon | Coming Soon |

---

## 🔮 Future Improvements

- [ ] Group Chats
- [ ] Unread Message Count
- [ ] Message Read Receipts
- [ ] Typing Indicators
- [ ] Online / Offline Status
- [ ] Push Notifications
- [ ] Image and File Sharing
- [ ] Voice Messages
- [ ] End-to-End Encryption

---

## 🤝 Contributing

Contributions, suggestions, and feedback are always welcome!

If you have ideas to improve ChatLab, feel free to open an issue or submit a pull request.

---

## 👨‍💻 Developers

**Abhishek Verma**

- GitHub: [@abhishekverma2656](https://github.com/abhishekverma2656)

**Brajesh**

- GitHub: [@brajesh9329](https://github.com/brajesh9329)

---

<p align="center">
  Made with ❤️ using Java & React Native
</p>

<p align="center">
  ⭐ If you like ChatLab, consider giving it a star!
</p>
