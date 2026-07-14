import SwiftUI

struct HomeView: View {
    @StateObject private var mountainService = MountainService()
    @State private var searchText = ""
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            ScrollView {
                VStack(spacing: 16) {
                    // Search
                    HStack {
                        Image(systemName: "magnifyingglass").foregroundStyle(.secondary)
                        TextField("Search mountains, trails...", text: $searchText)
                            .onSubmit { Task { await mountainService.search(searchText) } }
                    }
                    .padding(12).background(Color(.systemGray6)).clipShape(RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal)

                    // Quick actions
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 4)) {
                        QuickAction(icon: "mountain.2", label: "Mountains") {}
                        QuickAction(icon: "map", label: "Maps") {}
                        QuickAction(icon: "figure.hiking", label: "Trails") {}
                        QuickAction(icon: "person.3", label: "Community") {}
                    }
                    .padding(.horizontal)

                    // Mountains
                    SectionHeader(title: "Recommended")
                    if mountainService.isLoading {
                        ProgressView().padding()
                    } else {
                        ScrollView(.horizontal, showsIndicators: false) {
                            HStack(spacing: 12) {
                                ForEach(mountainService.mountains) { m in
                                    NavigationLink(value: AppScreen.mountainDetail(id: m.id)) {
                                        MountainCard(name: m.name, location: m.location, elevation: m.elevation, rating: m.rating, imageUrl: m.imageUrl)
                                            .frame(width: 220)
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
            }
            .navigationTitle("Bakudapa Adventure")
            .task { await mountainService.fetchMountains() }
            .navigationDestination(for: AppScreen.self) { screen in
                switch screen {
                case .mountainDetail(let id): MountainDetailView(mountainId: id)
                case .trailDetail(let id): TrailDetailView(trailId: id)
                default: EmptyView()
                }
            }
        }
    }
}

struct QuickAction: View {
    let icon: String
    let label: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 6) {
                Image(systemName: icon).font(.title3)
                    .frame(width: 48, height: 48)
                    .background(Color.adventureGreen.opacity(0.12))
                    .clipShape(Circle())
                Text(label).font(.caption2).fontWeight(.medium)
            }
            .foregroundStyle(.primary)
        }
    }
}

struct SectionHeader: View {
    let title: String
    var body: some View {
        HStack {
            Text(title).font(.title3).fontWeight(.bold)
            Spacer()
            Button("See All") {}.font(.subheadline)
        }
        .padding(.horizontal)
    }
}
