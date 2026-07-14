import Foundation
import FirebaseFirestore

@MainActor
final class TrailService: ObservableObject {
    @Published var trails: [Trail] = []
    @Published var isLoading = false
    @Published var error: String? = nil

    private let db = FirebaseManager.shared.db

    func fetchTrails(for mountainId: String) async {
        isLoading = true; error = nil
        do {
            let snap = try await db.collection("trails")
                .whereField("mountainId", isEqualTo: mountainId)
                .order(by: "popularity", descending: true)
                .getDocuments()
            trails = snap.documents.compactMap { try? $0.data(as: Trail.self) }
        } catch { self.error = error.localizedDescription }
        isLoading = false
    }

    func fetchTrailDetail(id: String) async throws -> Trail {
        let doc = try await db.collection("trails").document(id).getDocument()
        return try doc.data(as: Trail.self)
    }
}
