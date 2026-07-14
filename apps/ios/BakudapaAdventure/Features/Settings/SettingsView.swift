import SwiftUI

struct SettingsView: View {
    @AppStorage("isDarkMode") private var isDarkMode = false
    @AppStorage("isMetric") private var isMetric = true
    @AppStorage("notifications") private var notifications = true
    @EnvironmentObject var authVM: AuthViewModel

    var body: some View {
        NavigationStack {
            List {
                Section("Preferences") {
                    Toggle(isOn: $isDarkMode) { Label("Dark Mode", systemImage: "palette") }
                    Toggle(isOn: $isMetric) { Label("Metric Units", systemImage: "ruler") }
                    Toggle(isOn: $notifications) { Label("Notifications", systemImage: "bell") }
                }
                Section("About") {
                    LabeledContent("Version", value: "1.0.0")
                    LabeledContent("Build", value: "1")
                }
                Section {
                    Button("Sign Out", role: .destructive) { authVM.signOut() }
                }
            }
            .navigationTitle("Settings")
        }
    }
}
