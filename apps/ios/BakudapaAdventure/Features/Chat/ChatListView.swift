import SwiftUI

struct ChatListView: View {
    var body: some View {
        NavigationStack {
            ContentUnavailableView("Coming Soon", systemImage: "message", description: Text("Chat will be here."))
                .navigationTitle("Chat")
        }
    }
}
