import ComposeApp
import Mixpanel

class MixpanelAnalyticsHandler: AnalyticsAdapter {

    func initialize(config: AnalyticsConfig) {
        guard !config.token.isEmpty else { return }

        Mixpanel.initialize(token: config.token, trackAutomaticEvents: false, serverURL: config.serverUrl)
        Mixpanel.mainInstance().registerSuperProperties([
            "platform": "ios",
            "environment": BuildKonfig.FLAVOR_NAME
        ])

        if let userId = config.userId {
            Mixpanel.mainInstance().identify(distinctId: userId)
        }
    }

    func trackEvent(event: String, properties: [String: Any]) {
        let stringProps = properties.mapValues { String(describing: $0) }
        Mixpanel.mainInstance().track(event: event, properties: stringProps)
    }

    func identifyUser(userId: String) {
        Mixpanel.mainInstance().identify(distinctId: userId)
    }

    func reset() {
        Mixpanel.mainInstance().reset()
    }

    func flush() {
        Mixpanel.mainInstance().flush()
    }
}
