// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "BakudapaAdventure",
    platforms: [
        .iOS(.v17)
    ],
    dependencies: [
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "11.0.0"),
    ],
    targets: [
        .executableTarget(
            name: "BakudapaAdventure",
            dependencies: [
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseStorage", package: "firebase-ios-sdk"),
            ],
            resources: [
                .process("GoogleService-Info.plist")
            ]
        ),
        .testTarget(
            name: "BakudapaAdventureTests",
            dependencies: ["BakudapaAdventure"]
        ),
    ]
)
