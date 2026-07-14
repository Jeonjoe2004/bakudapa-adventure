import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var authVM: AuthViewModel

    var body: some View {
        NavigationStack {
            List {
                Section {
                    Label("Edit Profile", systemImage: "person")
                    Label("Settings", systemImage: "gearshape")
                }
                Section {
                    Button("Sign Out", role: .destructive) { authVM.signOut() }
                }
            }
            .navigationTitle("Profile")
        }
    }
}
