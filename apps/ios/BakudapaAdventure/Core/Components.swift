import SwiftUI

// MARK: - Loading State
struct LoadingView: View {
    let text: String
    var body: some View {
        VStack(spacing: 12) {
            ProgressView()
                .tint(.adventureGreen)
            Text(text).font(.subheadline).foregroundStyle(.secondary)
        }
    }
}

// MARK: - Empty State
struct EmptyStateView: View {
    let icon: String
    let message: String
    var body: some View {
        ContentUnavailableView(icon, systemImage: "tray")
            .padding()
    }
}

// MARK: - Error State
struct ErrorStateView: View {
    let message: String
    let retry: (() -> Void)?

    var body: some View {
        ContentUnavailableView {
            Label("Something went wrong", systemImage: "exclamationmark.triangle")
        } description: {
            Text(message).font(.subheadline)
        } actions: {
            if let retry {
                Button("Retry", action: retry)
                    .buttonStyle(.borderedProminent)
                    .tint(.adventureGreen)
            }
        }
    }
}

// MARK: - Mountain Card
struct MountainCard: View {
    let name: String
    let location: String
    let elevation: Int
    let rating: Float
    let imageUrl: String

    var body: some View {
        VStack(alignment: .leading) {
            AsyncImage(url: URL(string: imageUrl)) { phase in
                phase.image?.resizable().scaledToFill()
                    ?? Color.gray.opacity(0.2)
            }
            .frame(height: 130).clipped()

            VStack(alignment: .leading, spacing: 4) {
                Text(name).font(.headline).fontWeight(.bold).lineLimit(1)
                Label(location, systemImage: "location").font(.caption).foregroundStyle(.secondary)
                HStack {
                    Label("\(rating, specifier: "%.1f")", systemImage: "star.fill")
                        .font(.caption).foregroundStyle(.orange)
                    Spacer()
                    Text("\(elevation)m").font(.caption2).foregroundStyle(.adventureGreen)
                }
            }
            .padding(.horizontal, 8).padding(.bottom, 8)
        }
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(radius: 2)
    }
}
