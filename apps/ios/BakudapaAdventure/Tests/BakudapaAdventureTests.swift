import XCTest
@testable import BakudapaAdventure

final class BakudapaAdventureTests: XCTestCase {
    func testMountainModel() {
        let m = Mountain(
            id: "semeru_1",
            name: "Semeru",
            location: "Jawa Timur",
            elevation: 3676,
            imageUrl: "https://example.com/semeru.jpg",
            rating: 4.8,
            description: "Gunung tertinggi di Jawa",
            difficulty: "HARD",
            bestSeason: "Apr-Oct",
            latitude: -8.1078,
            longitude: 112.9225,
            distance: 12.5
        )
        XCTAssertEqual(m.name, "Semeru")
        XCTAssertGreaterThan(m.elevation, 0)
    }

    func testTrailModel() {
        let t = Trail(
            id: "trail_1",
            name: "Ranupane",
            mountainId: "semeru_1",
            mountainName: "Semeru",
            difficulty: "MODERATE",
            durationMinutes: 480,
            distanceKm: 12.0,
            imageUrl: "https://example.com/trail.jpg",
            popularity: 85,
            elevationGain: 1500,
            maxElevation: 3676
        )
        XCTAssertEqual(t.mountainName, "Semeru")
        XCTAssertGreaterThan(t.distanceKm, 0)
    }

    func testPostModel() {
        let p = Post(
            id: "post_1",
            authorName: "Pendaki",
            authorImageUrl: nil,
            content: "Gunung Indah!",
            imageUrl: nil,
            timestamp: Date().timeIntervalSince1970,
            likesCount: 5,
            commentsCount: 2
        )
        XCTAssertEqual(p.content, "Gunung Indah!")
        XCTAssertGreaterThanOrEqual(p.likesCount, 0)
    }

    func testChatRoomModel() {
        let r = ChatRoom(
            id: "room_1",
            name: "Hiking Club",
            lastMessage: "Kapan naik?",
            lastMessageTime: Date().timeIntervalSince1970,
            participants: ["user_a", "user_b"],
            unreadCount: 3
        )
        XCTAssertEqual(r.name, "Hiking Club")
        XCTAssertEqual(r.participants.count, 2)
    }

    func testDifficultyColorMapping() {
        XCTAssertEqual(Difficulty.easy.color, .green)
        XCTAssertEqual(Difficulty.moderate.color, .orange)
        XCTAssertEqual(Difficulty.hard.color, .red)
        XCTAssertEqual(Difficulty.expert.color, .purple)
    }
}
