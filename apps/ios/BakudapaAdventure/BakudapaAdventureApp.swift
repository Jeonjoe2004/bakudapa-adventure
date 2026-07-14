import SwiftUI
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ app: UIApplication,
                     didFinishLaunchingWithOptions opts: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct BakudapaAdventureApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @StateObject private var authVM = AuthViewModel()

    var body: some Scene {
        WindowGroup {
            Group {
                if authVM.isLoading {
                    SplashScreen()
                } else if authVM.user != nil {
                    MainTabView()
                        .environmentObject(authVM)
                } else {
                    AuthFlowView()
                        .environmentObject(authVM)
                }
            }
            .preferredColorScheme(authVM.isDarkMode ? .dark : nil)
        }
    }
}
