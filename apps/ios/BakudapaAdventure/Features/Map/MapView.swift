import SwiftUI
import MapKit

struct MapView: View {
    @State private var region = MKCoordinateRegion(
        center: CLLocationCoordinate2D(latitude: -2.0, longitude: 118.0),
        span: MKCoordinateSpan(latitudeDelta: 10, longitudeDelta: 10)
    )

    var body: some View {
        NavigationStack {
            Map(initialPosition: .region(region))
                .navigationTitle("Map")
                .ignoresSafeArea(edges: .bottom)
        }
    }
}
