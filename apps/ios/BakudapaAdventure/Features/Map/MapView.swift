import SwiftUI
import MapKit
import CoreLocation

extension CLLocationCoordinate2D: @retroactive Equatable {
    public static func == (lhs: CLLocationCoordinate2D, rhs: CLLocationCoordinate2D) -> Bool {
        lhs.latitude == rhs.latitude && lhs.longitude == rhs.longitude
    }
}

struct MapView: View {
    @State private var position: MapCameraPosition = .automatic
    @State private var tracking: TrackingState = .idle
    @State private var route: [CLLocationCoordinate2D] = []
    @State private var timer: Timer?
    @State private var elapsed: TimeInterval = 0

    enum TrackingState: Equatable { case idle, recording, paused }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                Map(position: $position, interactionModes: .all) {
                    UserAnnotation()
                    if route.count > 1 {
                        MapPolyline(coordinates: route)
                            .stroke(.adventureGreen, lineWidth: 4)
                    }
                }
                .mapControls {
                    MapUserLocationButton()
                    MapCompass()
                    MapScaleView()
                }

                // Tracking controls
                HStack(spacing: 24) {
                    Button(action: toggleTracking) {
                        Label(
                            tracking == .recording ? "Stop" : tracking == .paused ? "Resume" : "Start",
                            systemImage: tracking == .recording ? "stop.circle.fill" : "record.circle"
                        )
                        .font(.headline)
                        .foregroundStyle(tracking == .recording ? .red : .adventureGreen)
                    }

                    if tracking != .idle {
                        Text(elapsedString).font(.title3.monospacedDigit()).foregroundStyle(.secondary)

                        Button(action: { tracking = .paused; timer?.invalidate() }) {
                            Image(systemName: tracking == .paused ? "play.circle" : "pause.circle")
                                .font(.title2)
                        }
                        .disabled(tracking == .idle)
                    }
                }
                .padding()
                .background(.bar)
            }
            .navigationTitle("Map")
        }
    }

    private var elapsedString: String {
        let h = Int(elapsed) / 3600
        let m = (Int(elapsed) % 3600) / 60
        let s = Int(elapsed) % 60
        return h > 0 ? String(format: "%d:%02d:%02d", h, m, s) : String(format: "%02d:%02d", m, s)
    }

    private func toggleTracking() {
        switch tracking {
        case .idle:
            tracking = .recording
            route = []
            elapsed = 0
            timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
                elapsed += 1
            }
        case .recording, .paused:
            tracking = .idle
            timer?.invalidate()
            timer = nil
            // Simpan rute di sini nanti
        }
    }
}
