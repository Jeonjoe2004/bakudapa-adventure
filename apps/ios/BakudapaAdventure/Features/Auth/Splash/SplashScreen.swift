import SwiftUI

struct SplashScreen: View {
    var body: some View {
        VStack {
            Image(systemName: "mountain.2.fill").font(.system(size: 80)).foregroundStyle(.adventureGreen)
            ProgressView().padding()
        }
    }
}
