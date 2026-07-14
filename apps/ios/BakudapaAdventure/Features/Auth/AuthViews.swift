import SwiftUI

struct LoginView: View {
    @Binding var path: NavigationPath
    @EnvironmentObject var authVM: AuthViewModel
    @State private var email = "admin@bakudapa.com"
    @State private var password = ""
    @State private var error: String?
    @State private var loading = false

    var body: some View {
        VStack(spacing: 24) {
            Spacer()
            Image(systemName: "mountain.2.fill").font(.system(size: 60)).foregroundStyle(.adventureGreen)
            Text("Bakudapa Adventure").font(.title).fontWeight(.bold)
            Text("Admin Dashboard").font(.subheadline).foregroundStyle(.secondary)

            VStack(spacing: 16) {
                TextField("Email", text: $email).textFieldStyle(.roundedBorder).textContentType(.emailAddress).autocapitalization(.none)
                SecureField("Password", text: $password).textFieldStyle(.roundedBorder)
                if let error { Text(error).font(.caption).foregroundStyle(.red) }
                Button(action: login) {
                    if loading { ProgressView().tint(.white) }
                    else { Text("Sign In").fontWeight(.semibold) }
                }
                .buttonStyle(.borderedProminent).tint(.adventureGreen).frame(maxWidth: .infinity).disabled(loading)
            }
            .padding(.horizontal, 32)

            Button("Don't have an account? Register") { path.append(AppScreen.register) }
                .font(.subheadline)
            Button("Forgot password?") { path.append(AppScreen.forgotPassword) }
                .font(.caption)
            Spacer()
        }
    }

    private func login() {
        loading = true; error = nil
        Task {
            do { try await authVM.signIn(email: email, password: password) }
            catch { self.error = error.localizedDescription }
            loading = false
        }
    }
}

struct RegisterView: View {
    @Binding var path: NavigationPath
    @EnvironmentObject var authVM: AuthViewModel
    @State private var email = ""
    @State private var password = ""
    @State private var confirm = ""
    @State private var error: String?
    @State private var loading = false

    var body: some View {
        VStack(spacing: 20) {
            Text("Create Account").font(.title2).fontWeight(.bold)
            TextField("Email", text: $email).textFieldStyle(.roundedBorder).autocapitalization(.none)
            SecureField("Password", text: $password).textFieldStyle(.roundedBorder)
            SecureField("Confirm Password", text: $confirm).textFieldStyle(.roundedBorder)
            if let error { Text(error).font(.caption).foregroundStyle(.red) }
            Button(action: register) {
                if loading { ProgressView().tint(.white) }
                else { Text("Sign Up").fontWeight(.semibold) }
            }
            .buttonStyle(.borderedProminent).tint(.adventureGreen).disabled(loading)
            Button("Already have an account? Login") { path.removeLast() }
                .font(.subheadline)
        }
        .padding(32)
    }

    private func register() {
        guard password == confirm else { error = "Passwords don't match"; return }
        loading = true; error = nil
        Task {
            do { try await authVM.signUp(email: email, password: password); path.append(AppScreen.profileCompletion) }
            catch { self.error = error.localizedDescription }
            loading = false
        }
    }
}

struct ForgotPasswordView: View {
    @EnvironmentObject var authVM: AuthViewModel
    @State private var email = ""
    @State private var message: String?
    @State private var loading = false

    var body: some View {
        VStack(spacing: 20) {
            Text("Reset Password").font(.title2).fontWeight(.bold)
            TextField("Email", text: $email).textFieldStyle(.roundedBorder).autocapitalization(.none)
            if let message { Text(message).font(.caption).foregroundStyle(.green) }
            Button(action: reset) {
                if loading { ProgressView() } else { Text("Send Reset Link").fontWeight(.semibold) }
            }
            .buttonStyle(.borderedProminent).tint(.adventureGreen).disabled(loading)
        }
        .padding(32)
    }

    private func reset() {
        loading = true
        Task {
            do { try await authVM.resetPassword(email: email); message = "Reset link sent!" }
            catch { message = error.localizedDescription }
            loading = false
        }
    }
}

struct ProfileCompletionView: View {
    var body: some View {
        Text("Complete Your Profile")
            .font(.title2).fontWeight(.bold)
    }
}
