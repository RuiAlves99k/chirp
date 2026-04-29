import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        InitKoinKt.doInitKoin(analyticsAdapter: MixpanelAnalyticsHandler(), config: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    ExternalUriHandler.shared.onNewUri(uri: url.absoluteString)
                }
        }
    }
}
