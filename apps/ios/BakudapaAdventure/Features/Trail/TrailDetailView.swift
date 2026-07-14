import SwiftUI

struct TrailDetailView: View {
    let trailId: String
    @State private var trail: Trail?
    @State private var loading = true
    @State private var error: String?

    var body: some View {
        Group {
            if loading { LoadingView(text: "Loading trail...") }
            else if let error { ErrorStateView(message: error) { Task { await load() } } }
            else if let trail {
                ScrollView {
                    AsyncImage(url: URL(string: trail.imageUrl)) { phase in
                        phase.image?.resizable().scaledToFill() ?? Color.gray.opacity(0.3)
                    }
                    .frame(height: 220).clipped()

                    VStack(spacing: 16) {
                        // Stats
                        HStack(spacing: 32) {
                            StatItem(value: "\(trail.distanceKm, specifier: "%.1f")km", label: "Distance")
                            StatItem(value: durationText(trail.durationMinutes), label: "Duration")
                            StatItem(value: "\(trail.elevationGain ?? 0)m", label: "Elevation")
                        }

                        // Difficulty
                        DifficultyBadge(difficulty: trail.difficulty)
                            .frame(maxWidth: .infinity, alignment: .leading)

                        // Description
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Description").font(.title3).fontWeight(.bold)
                            Text(trail.description ?? "No description available.")
                                .foregroundStyle(.secondary)
                        }

                        // Gear
                        if let gear = trail.recommendedGear, !gear.isEmpty {
                            SectionList(title: "Recommended Gear", icon: "checklist", items: gear)
                        }

                        // Water
                        if let water = trail.waterSources, !water.isEmpty {
                            SectionList(title: "Water Sources", icon: "drop", items: water)
                        }

                        // Camping
                        if let camping = trail.campingSpots, !camping.isEmpty {
                            SectionList(title: "Camping Spots", icon: "tent", items: camping)
                        }

                        // Start button
                        NavigationLink(value: AppScreen.tracking) {
                            Label("Start Tracking", systemImage: "play.fill")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(.adventureGreen)
                                .foregroundStyle(.white)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                    .padding()
                }
                .navigationTitle(trail.name)
                .navigationBarTitleDisplayMode(.inline)
            }
        }
        .task { await load() }
        .navigationDestination(for: AppScreen.self) { screen in
            if case .tracking = screen { MapView() }
            else { EmptyView() }
        }
    }

    private func load() async {
        loading = true; error = nil
        do {
            let service = TrailService()
            trail = try await service.fetchTrailDetail(id: trailId)
        } catch { self.error = error.localizedDescription }
        loading = false
    }

    private func durationText(_ mins: Int) -> String {
        mins >= 60 ? "\(mins / 60)h \(mins % 60)m" : "\(mins)m"
    }
}

struct SectionList: View {
    let title: String
    let icon: String
    let items: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title).font(.title3).fontWeight(.bold)
            ForEach(items, id: \.self) { item in
                Label(item, systemImage: icon)
                    .font(.subheadline)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
