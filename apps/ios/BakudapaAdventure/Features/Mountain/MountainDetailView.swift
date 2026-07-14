import SwiftUI

struct MountainDetailView: View {
    let mountainId: String
    @StateObject private var trailService = TrailService()
    @State private var mountain: Mountain?
    @State private var loading = true
    @State private var error: String?

    var body: some View {
        Group {
            if loading { LoadingView(text: "Loading mountain...") }
            else if let error { ErrorStateView(message: error) { Task { await load() } } }
            else if let mountain {
                ScrollView {
                    AsyncImage(url: URL(string: mountain.imageUrl)) { phase in
                        phase.image?.resizable().scaledToFill()
                            ?? Color.gray.opacity(0.3)
                    }
                    .frame(height: 250).clipped()

                    VStack(spacing: 16) {
                        // Stats
                        HStack(spacing: 32) {
                            StatItem(value: "\(mountain.elevation)m", label: "Elevation")
                            StatItem(value: String(format: "%.1f", mountain.rating), label: "Rating")
                            StatItem(value: mountain.difficulty ?? "-", label: "Difficulty")
                        }
                        .padding(.top)

                        // Description
                        VStack(alignment: .leading, spacing: 8) {
                            Text("About").font(.title3).fontWeight(.bold)
                            Text(mountain.description ?? "No description available.")
                                .foregroundStyle(.secondary)
                        }

                        // Best season
                        if let season = mountain.bestSeason, !season.isEmpty {
                            Label(season, systemImage: "calendar")
                                .frame(maxWidth: .infinity, alignment: .leading)
                        }

                        // Trails
                        Text("Trails").font(.title3).fontWeight(.bold).frame(maxWidth: .infinity, alignment: .leading)
                        if trailService.isLoading {
                            ProgressView()
                        } else {
                            ForEach(trailService.trails) { trail in
                                NavigationLink(value: AppScreen.trailDetail(id: trail.id)) {
                                    TrailRow(trail: trail)
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }
                    .padding()
                }
                .navigationTitle(mountain.name)
                .navigationBarTitleDisplayMode(.inline)
            }
        }
        .task { await load() }
        .navigationDestination(for: AppScreen.self) { screen in
            if case .trailDetail(let id) = screen { TrailDetailView(trailId: id) }
            else { EmptyView() }
        }
    }

    private func load() async {
        loading = true; error = nil
        do {
            mountain = try await MountainService().fetchMountainDetail(id: mountainId)
            await trailService.fetchTrails(for: mountainId)
        } catch { self.error = error.localizedDescription }
        loading = false
    }
}

struct StatItem: View {
    let value: String
    let label: String
    var body: some View {
        VStack(spacing: 2) {
            Text(value).font(.title3).fontWeight(.bold)
            Text(label).font(.caption).foregroundStyle(.secondary)
        }
    }
}

struct TrailRow: View {
    let trail: Trail
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: trail.imageUrl)) { phase in
                phase.image?.resizable().scaledToFill() ?? Color.gray.opacity(0.2)
            }
            .frame(width: 60, height: 60).clipShape(RoundedRectangle(cornerRadius: 8))

            VStack(alignment: .leading, spacing: 2) {
                Text(trail.name).font(.subheadline).fontWeight(.semibold)
                Text("\(trail.distanceKm, specifier: "%.1f")km • \(trail.durationMinutes)min")
                    .font(.caption).foregroundStyle(.secondary)
                DifficultyBadge(difficulty: trail.difficulty)
            }
            Spacer()
            Image(systemName: "chevron.right").font(.caption).foregroundStyle(.secondary)
        }
        .padding(8)
        .background(Color(.systemGray6)).clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

struct DifficultyBadge: View {
    let difficulty: String
    var body: some View {
        let d = Difficulty(rawValue: difficulty.lowercased()) ?? .moderate
        Text(difficulty.capitalized).font(.caption2).fontWeight(.bold)
            .padding(.horizontal, 8).padding(.vertical, 2)
            .background(d.color.opacity(0.15))
            .foregroundStyle(d.color)
            .clipShape(RoundedRectangle(cornerRadius: 4))
    }
}
