# SRS-001 Authentication

Version: 1.0

Status: Draft

---

## Tujuan

Mengelola autentikasi pengguna menggunakan Firebase Authentication.

---

## Fitur

- Login
- Register
- Logout
- Forgot Password
- Email Verification
- Google Login
- Apple Login (iOS)
- Guest Mode

---

## Aktor

- User
- Admin

---

## Functional Requirements

### Register

User dapat membuat akun menggunakan:

- Email
- Google

Data:

- Nama
- Email
- Password

### Login

User dapat login menggunakan:

- Email
- Google

### Logout

Menghapus sesi login.

### Forgot Password

Mengirim email reset password.

### Email Verification

Email harus diverifikasi sebelum menggunakan fitur tertentu.

### Guest Mode

Guest hanya dapat melihat:

- Gunung
- Peta
- Artikel

Tidak dapat:

- Chat
- Upload Jalur
- Tracking
- Komentar
- Bookmark

---

## Non Functional Requirements

- Login < 3 detik
- HTTPS
- Password minimal 8 karakter
- Android & iOS

---

## Database

Collection:

users

Field:

- uid
- name
- email
- photoUrl
- provider
- role
- emailVerified
- createdAt

---

## UI

- Splash
- Onboarding
- Login
- Register
- Forgot Password
- Verify Email

---

## Security

- Firebase Authentication
- HTTPS
- Firebase Rules
- JWT Token

---

## Error Handling

- Email sudah digunakan
- Password salah
- Email belum diverifikasi
- Internet tidak tersedia
- Firebase Error

---

## Future Improvement

- Login Nomor HP
- MFA
- Face ID
- Fingerprint
- Login Kampus
