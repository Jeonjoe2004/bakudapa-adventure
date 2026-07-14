import SwiftUI

struct EmergencyView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Image(systemName: "sos").font(.system(size: 64)).foregroundStyle(.red)
                Text("Emergency SOS").font(.title).fontWeight(.bold)
                Text("Share your location with emergency contacts.")
                    .foregroundStyle(.secondary)
                Button(action: {}) {
                    Label("Send SOS Alert", systemImage: "location.fill")
                        .fontWeight(.semibold).frame(maxWidth: .infinity)
                        .padding().background(.red).foregroundStyle(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
            }
            .padding(32)
            .navigationTitle("Emergency")
        }
    }
}
