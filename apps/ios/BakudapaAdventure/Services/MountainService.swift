import Foundation
import FirebaseFirestore

@MainActor
final class MountainService: ObservableObject {
    @Published var mountains: [Mountain] = []
    @Published var isLoading = false
    @Published var error: String? = nil

    private let db = FirebaseManager.shared.db

    func fetchMountains() async {
        isLoading = true; error = nil
        do {
            let snap = try await db.collection("mountains").order(by: "rating", descending: true).getDocuments()
            mountains = snap.documents.compactMap { try? $0.data(as: Mountain.self) }
        } catch { self.error = error.localizedDescription }
        isLoading = false
    }

    func fetchMountainDetail(id: String) async throws -> Mountain {
        let doc = try await db.collection("mountains").document(id).getDocument()
        return try doc.data(as: Mountain.self)
    }

    func search(_ query: String) async {
        guard !query.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        isLoading = true; error = nil
        do {
            let snap = try await db.collection("mountains")
                .order(by: "name")
                .start(at: [query])
                .end(at: [query + "\u{f8ff}"])
                .getDocuments()
            mountains = snap.documents.compactMap { try? $0.data(as: Mountain.self) }
        } catch { self.error = error.localizedDescription }
        isLoading = false
    }
}
