import FirebaseFirestore
import FirebaseAuth
import Combine

final class FirebaseManager {
    static let shared = FirebaseManager()

    let db = Firestore.firestore()
    let auth = Auth.auth()

    private init() {}

    // MARK: - Collections
    func mountains() -> CollectionReference { db.collection("mountains") }
    func trails() -> CollectionReference { db.collection("trails") }
    func users() -> CollectionReference { db.collection("users") }
    func posts() -> CollectionReference { db.collection("posts") }
    func chats() -> CollectionReference { db.collection("chats") }
}
