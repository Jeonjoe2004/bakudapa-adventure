import Foundation
import FirebaseFirestore

struct Mountain: Codable, Identifiable {
    let id: String
    let name: String
    let location: String
    let elevation: Int
    let imageUrl: String
    let rating: Float
    let description: String?
    let difficulty: String?
    let bestSeason: String?
    let latitude: Double?
    let longitude: Double?
    let distance: Double?
}

struct Trail: Codable, Identifiable {
    let id: String
    let name: String
    let mountainId: String
    let mountainName: String
    let difficulty: String
    let durationMinutes: Int
    let distanceKm: Double
    let imageUrl: String
    let description: String?
    let popularity: Int?
    let elevationGain: Int?
    let maxElevation: Int?
    let recommendedGear: [String]?
    let waterSources: [String]?
    let campingSpots: [String]?
}

struct Post: Codable, Identifiable {
    let id: String
    let authorName: String
    let authorImageUrl: String?
    let content: String
    let imageUrl: String?
    let timestamp: TimeInterval
    let likesCount: Int
    let commentsCount: Int
}

struct ChatRoom: Codable, Identifiable {
    let id: String
    let name: String
    let lastMessage: String?
    let lastMessageTime: TimeInterval?
    let participants: [String]
    let unreadCount: Int?
}

struct Message: Codable, Identifiable {
    let id: String
    let senderId: String
    let senderName: String
    let text: String
    let timestamp: Timestamp?
}

struct UserProfile: Codable, Identifiable {
    let id: String
    let displayName: String?
    let email: String?
    let photoUrl: String?
    let level: Int?
    let xp: Int?
}

struct HikingRoute: Codable, Identifiable {
    let id: String
    let name: String
    let distanceMeters: Double
    let durationMillis: Int64
    let startTime: TimeInterval?
    let endTime: TimeInterval?
}

struct EmergencyContact: Codable, Identifiable {
    let id: String
    let name: String
    let phone: String
}
