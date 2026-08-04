import ComposeApp
import Mixpanel

class MixpanelAnalyticsHandler: DomainAnalyticsAdapter {
    
    private var mixpanel: MixpanelInstance? = nil

    func initialize(config: DomainAnalyticsConfig) {
        guard !config.token.isEmpty else { return }

        mixpanel = Mixpanel.initialize(token: config.token, trackAutomaticEvents: false, serverURL: config.serverUrl)
        mixpanel?.registerSuperProperties([
            "platform": "ios",
        ])

        if let userId = config.userId {
            mixpanel?.identify(distinctId: userId)
        }
    }

    func trackEvent(event: String, properties: [String: Any]) {
        let stringProps = properties.mapValues { String(describing: $0) }
        mixpanel?.track(event: event, properties: stringProps)
    }

    func identifyUser(userId: String) {
        mixpanel?.identify(distinctId: userId)
    }

    func reset() {
        mixpanel?.reset()
    }

    func flush() {
        mixpanel?.flush()
    }
}
