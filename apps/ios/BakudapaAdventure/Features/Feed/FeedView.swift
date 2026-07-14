import SwiftUI

struct FeedView: View {
    var body: some View {
        NavigationStack {
            ContentUnavailableView("Coming Soon", systemImage: "newspaper", description: Text("Community feed will be here."))
                .navigationTitle("Feed")
        }
    }
}
