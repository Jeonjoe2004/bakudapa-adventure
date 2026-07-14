import SwiftUI

struct BadgeView: View {
    var body: some View {
        NavigationStack {
            ContentUnavailableView("Coming Soon", systemImage: "medal", description: Text("Achievements will be here."))
                .navigationTitle("Badges")
        }
    }
}
