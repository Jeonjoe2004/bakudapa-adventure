import SwiftUI

// MARK: - Brand Colors
extension Color {
    static let adventureGreen = Color(hex: "2E7D32")
    static let adventureGreenLight = Color(hex: "60AD5E")
    static let adventureAmber = Color(hex: "FFA000")
    static let adventureAmberDark = Color(hex: "C67100")
    static let surfaceLight = Color(hex: "FBFBFB")
    static let surfaceDark = Color(hex: "1E1E1E")
}

extension Color {
    init(hex: String) {
        let scanner = Scanner(string: hex)
        _ = scanner.scanString("#")
        var rgb: UInt64 = 0
        scanner.scanHexInt64(&rgb)
        let r = Double((rgb >> 16) & 0xFF) / 255
        let g = Double((rgb >> 8) & 0xFF) / 255
        let b = Double(rgb & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

// MARK: - Difficulty Colors
enum Difficulty: String, Codable {
    case easy, moderate, hard, expert

    var color: Color {
        switch self {
        case .easy: return .green
        case .moderate: return .orange
        case .hard: return .red
        case .expert: return .purple
        }
    }
}
