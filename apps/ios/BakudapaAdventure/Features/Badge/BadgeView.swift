import SwiftUI

struct BadgeView: View {
    let badges: [(icon: String, name: String, color: Color, desc: String)] = [
        ("star.fill", "First Hike", .adventureGreen, "Completed your first hike"),
        ("mountain.2.fill", "Summit Seeker", .orange, "Reached 5 mountain peaks"),
        ("figure.hiking", "Trail Blazer", .blue, "Hiked 50km total"),
        ("clock.fill", "Early Bird", .purple, "Started a hike before sunrise"),
        ("flame.fill", "Streak Master", .red, "7-day hiking streak"),
        ("map.fill", "Explorer", .teal, "Visited 10 different trails"),
        ("crown.fill", "Mountain King", .yellow, "Climbed the highest peak"),
        ("person.3.fill", "Social Climber", .pink, "Joined 3 group hikes"),
    ]

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVGrid(columns: [GridItem(.adaptive(minimum: 160))], spacing: 16) {
                    ForEach(badges.indices, id: \.self) { i in
                        BadgeCard(icon: badges[i].0, name: badges[i].1,
                                  color: badges[i].2, desc: badges[i].3,
                                  unlocked: i < 3)
                    }
                }
                .padding()
            }
            .navigationTitle("Badges")
        }
    }
}

private struct BadgeCard: View {
    let icon: String
    let name: String
    let color: Color
    let desc: String
    let unlocked: Bool

    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon)
                .font(.title)
                .foregroundStyle(unlocked ? color : .gray.opacity(0.4))
                .frame(width: 48, height: 48)
                .background(unlocked ? color.opacity(0.15) : .gray.opacity(0.1))
                .clipShape(RoundedRectangle(cornerRadius: 12))

            Text(name).font(.caption).fontWeight(.semibold)

            Text(desc).font(.caption2).foregroundStyle(.secondary)
                .multilineTextAlignment(.center).lineLimit(2)
        }
        .padding(12)
        .frame(maxWidth: .infinity)
        .background(Color(.systemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .shadow(color: .black.opacity(0.05), radius: 4)
        .opacity(unlocked ? 1 : 0.5)
    }
}
