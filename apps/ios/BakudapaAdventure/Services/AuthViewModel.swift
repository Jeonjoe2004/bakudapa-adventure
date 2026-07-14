import SwiftUI
import FirebaseAuth

@MainActor
final class AuthViewModel: ObservableObject {
    @Published var user: User? = nil
    @Published var isLoading = true
    @Published var isDarkMode = false

    private let auth = FirebaseManager.shared.auth
    private var handle: AuthStateDidChangeListenerHandle?

    init() {
        handle = auth.addStateDidChangeListener { [weak self] _, firebaseUser in
            self?.user = firebaseUser
            self?.isLoading = false
        }
    }

    deinit { if let handle = handle { auth.removeStateDidChangeListener(handle) } }

    func signIn(email: String, password: String) async throws {
        _ = try await auth.signIn(withEmail: email, password: password)
    }

    func signUp(email: String, password: String) async throws {
        _ = try await auth.createUser(withEmail: email, password: password)
    }

    func signOut() {
        try? auth.signOut()
    }

    func resetPassword(email: String) async throws {
        try await auth.sendPasswordReset(withEmail: email)
    }
}
