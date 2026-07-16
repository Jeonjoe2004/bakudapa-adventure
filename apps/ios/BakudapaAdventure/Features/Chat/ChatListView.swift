import SwiftUI
import FirebaseFirestore

struct ChatListView: View {
    @State private var rooms: [ChatRoom] = []
    @State private var isLoading = true
    @State private var error: String?

    var body: some View {
        NavigationStack {
            Group {
                if isLoading {
                    LoadingView(text: "Loading chats...")
                } else if let error {
                    ErrorStateView(message: error, retry: loadRooms)
                } else if rooms.isEmpty {
                    EmptyStateView(icon: "message", message: "Belum ada chat")
                } else {
                    List(rooms) { room in
                        NavigationLink(value: AppScreen.chatRoom(roomId: room.id)) {
                            ChatRoomRow(room: room)
                        }
                    }
                    .listStyle(.plain)
                    .refreshable { loadRooms() }
                }
            }
            .navigationTitle("Chat")
            .navigationDestination(for: AppScreen.self) { screen in
                if case .chatRoom(let id) = screen {
                    ChatRoomView(roomId: id)
                }
            }
        }
        .onAppear(perform: loadRooms)
    }

    private func loadRooms() {
        isLoading = true; error = nil
        guard let uid = FirebaseManager.shared.auth.currentUser?.uid else {
            isLoading = false; error = "Not logged in"; return
        }
        FirebaseManager.shared.chats()
            .whereField("participants", arrayContains: uid)
            .order(by: "lastMessageTime", descending: true)
            .getDocuments { snap, err in
                isLoading = false
                if let err { error = err.localizedDescription; return }
                rooms = snap?.documents.compactMap { d in
                    try? d.data(as: ChatRoom.self)
                } ?? []
            }
    }
}

private struct ChatRoomRow: View {
    let room: ChatRoom

    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(Color.adventureGreen.opacity(0.3))
                .frame(width: 44, height: 44)
                .overlay { Text(room.name.prefix(2)).font(.caption).fontWeight(.bold) }

            VStack(alignment: .leading, spacing: 4) {
                Text(room.name).font(.subheadline).fontWeight(.semibold)
                if let last = room.lastMessage {
                    Text(last).font(.caption).foregroundStyle(.secondary).lineLimit(1)
                }
            }

            Spacer()

            if let t = room.lastMessageTime {
                Text(t.asDate, style: .relative)
                    .font(.caption2).foregroundStyle(.secondary)
            }
            if let unread = room.unreadCount, unread > 0 {
                Text("\(unread)").font(.caption2).padding(6)
                    .background(Color.adventureGreen).foregroundStyle(.white)
                    .clipShape(Circle())
            }
        }
        .padding(.vertical, 2)
    }
}

// MARK: - Chat Room Detail (placeholder)

private struct ChatRoomView: View {
    let roomId: String

    var body: some View {
        VStack {
            ContentUnavailableView("Chat Room", systemImage: "message",
                description: Text("Chat detail view — room \(roomId)"))
        }
        .navigationTitle("Room")
    }
}
