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
                    ChatRoomDetailView(roomId: id)
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

// MARK: - Chat Room Detail

private struct ChatRoomDetailView: View {
    let roomId: String
    @State private var messages: [Message] = []
    @State private var inputText = ""
    @State private var isLoading = true

    var body: some View {
        VStack(spacing: 0) {
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 8) {
                        ForEach(messages, id: \.id) { msg in
                            MessageBubble(message: msg)
                        }
                    }
                    .padding()
                }
                .onChange(of: messages.count) {
                    if let last = messages.last { proxy.scrollTo(last.id, anchor: .bottom) }
                }
            }

            HStack(spacing: 8) {
                TextField("Tulis pesan...", text: $inputText)
                    .textFieldStyle(.roundedBorder)
                    .textInputAutocapitalization(.never)
                Button(action: sendMessage) {
                    Image(systemName: "paperplane.fill")
                        .foregroundStyle(inputText.trim().isEmpty ? .gray : .adventureGreen)
                }
                .disabled(inputText.trim().isEmpty)
            }
            .padding()
            .background(.bar)
        }
        .navigationTitle("Chat")
        .onAppear(perform: loadMessages)
    }

    private func loadMessages() {
        isLoading = true
        FirebaseManager.shared.db.collection("chats").document(roomId)
            .collection("messages")
            .order(by: "timestamp")
            .getDocuments { snap, _ in
                isLoading = false
                messages = snap?.documents.compactMap { try? $0.data(as: Message.self) } ?? []
            }
    }

    private func sendMessage() {
        let text = inputText.trim()
        guard !text.isEmpty else { return }
        inputText = ""
        guard let sender = FirebaseManager.shared.auth.currentUser else { return }
        let msg: [String: Any] = [
            "id": UUID().uuidString,
            "senderId": sender.uid,
            "senderName": sender.displayName ?? "User",
            "text": text,
            "timestamp": Timestamp(date: Date()),
        ]
        FirebaseManager.shared.db.collection("chats").document(roomId)
            .collection("messages").addDocument(data: msg)
        // Refresh
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) { loadMessages() }
    }
}

private struct MessageBubble: View {
    let message: Message
    @State private var userId = FirebaseManager.shared.auth.currentUser?.uid

    var isMine: Bool { message.senderId == userId }

    var body: some View {
        HStack {
            if isMine { Spacer(minLength: 60) }
            VStack(alignment: isMine ? .trailing : .leading, spacing: 2) {
                if !isMine {
                    Text(message.senderName).font(.caption2).foregroundStyle(.secondary)
                }
                Text(message.text)
                    .padding(.horizontal, 12).padding(.vertical, 8)
                    .background(isMine ? Color.adventureGreen : Color(.systemGray5))
                    .foregroundStyle(isMine ? .white : .primary)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            if !isMine { Spacer(minLength: 60) }
        }
    }
}
