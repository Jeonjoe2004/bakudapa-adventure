# Security Policy

## Reporting a Vulnerability

Jika kamu menemukan celah keamanan di Bakudapa Adventure, tolong laporkan dengan membuat issue atau hubungi maintainer langsung.

**Jangan** buka issue publik untuk kerentanan kritis — kirim email atau DM ke maintainer.

## Scope

- Firebase Auth & Firestore security rules
- API endpoint authentication
- User data privacy
- Admin panel access control

## Security Measures

- Firestore security rules membatasi akses berbasis auth
- Admin endpoints dilindungi custom claims
- Storage rules memisahkan akses per role
- Firebase Functions hanya bisa dipanggil oleh user terautentikasi
