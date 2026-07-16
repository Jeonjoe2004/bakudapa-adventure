import SwiftUI
import FirebaseFirestore

struct FeedView: View {
    @State private var posts: [Post] = []
    @State private var isLoading = true
    @State private var error: String?

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    LoadingView(text: "Loading feed...")
                } else if let error {
                    ErrorStateView(message: error, retry: loadPosts)
                } else if posts.isEmpty {
                    EmptyStateView(icon: "tray", message: "Belum ada postingan")
                } else {
                    List(posts) { post in
                        PostRow(post: post)
                            .listRowSeparator(.hidden)
                    }
                    .listStyle(.plain)
                    .refreshable { loadPosts() }
                }
            }
            .navigationTitle("Feed")
        }
        .onAppear(perform: loadPosts)
    }

    private func loadPosts() {
        isLoading = true; error = nil
        FirebaseManager.shared.posts()
            .order(by: "timestamp", descending: true)
            .limit(to: 20)
            .getDocuments { snap, err in
                isLoading = false
                if let err { error = err.localizedDescription; return }
                posts = snap?.documents.compactMap { d in
                    try? d.data(as: Post.self)
                } ?? []
            }
    }
}

private struct PostRow: View {
    let post: Post

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                AsyncImage(url: URL(string: post.authorImageUrl ?? "")) { p in
                    p.image?.resizable().scaledToFill()
                        ?? Image(systemName: "person.circle.fill").font(.title2)
                }
                .frame(width: 36, height: 36).clipShape(Circle())

                Text(post.authorName).font(.subheadline).fontWeight(.semibold)
                Spacer()
                Text(post.timestamp.asDate, style: .relative)
                    .font(.caption2).foregroundStyle(.secondary)
            }

            Text(post.content).font(.body)

            if let url = post.imageUrl {
                AsyncImage(url: URL(string: url)) { p in
                    p.image?.resizable().scaledToFill()
                        ?? Color.gray.opacity(0.2)
                }
                .frame(maxHeight: 250).clipShape(RoundedRectangle(cornerRadius: 12))
            }

            HStack(spacing: 16) {
                Label("\(post.likesCount)", systemImage: "heart")
                Label("\(post.commentsCount)", systemImage: "bubble.right")
            }
            .font(.caption).foregroundStyle(.secondary)
        }
        .padding(.vertical, 4)
    }
}
