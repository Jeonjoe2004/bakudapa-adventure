import SwiftUI

// MARK: - Navigation
enum AppScreen: Hashable {
    case login, register, forgotPassword, verifyEmail, profileCompletion
    case home, map, tracking, feed, createPost, comments(postId: String)
    case chat, chatRoom(roomId: String), profile, emergency, badge, settings
    case mountainDetail(id: String), trailDetail(id: String)
}

// MARK: - Auth Flow
struct AuthFlowView: View {
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            LoginView(path: $path)
                .navigationDestination(for: AppScreen.self) { screen in
                    switch screen {
                    case .register: RegisterView(path: $path)
                    case .forgotPassword: ForgotPasswordView()
                    case .profileCompletion: ProfileCompletionView()
                    default: EmptyView()
                    }
                }
        }
    }
}

// MARK: - Main Tab
struct MainTabView: View {
    var body: some View {
        TabView {
            HomeView().tabItem { Label("Home", systemImage: "house") }
            MapView().tabItem { Label("Map", systemImage: "map") }
            FeedView().tabItem { Label("Feed", systemImage: "newspaper") }
            ChatListView().tabItem { Label("Chat", systemImage: "message") }
            ProfileView().tabItem { Label("Profile", systemImage: "person") }
        }
        .tint(.adventureGreen)
    }
}
