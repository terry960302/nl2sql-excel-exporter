import { ANALYTICS_EVENTS, CTA_TYPES } from "@constants/analytics";

interface AnalyticsEventParams {
  [key: string]: string | number | boolean | null | undefined;
}

class Analytics {
  private static instance: Analytics;
  private isInitialized: boolean = false;

  private constructor() {}

  static getInstance(): Analytics {
    if (!Analytics.instance) {
      Analytics.instance = new Analytics();
    }
    return Analytics.instance;
  }

  initialize(): void {
    if (this.isInitialized) return;

    // Google Analytics 초기화 로직
    if (typeof window !== "undefined" && typeof window.gtag === "function") {
      this.isInitialized = true;
    }
  }

  trackEvent(eventName: string, eventParams?: AnalyticsEventParams): void {
    if (!this.isInitialized) return;

    if (typeof window !== "undefined" && window.gtag) {
      window.gtag("event", eventName, eventParams);
    }
  }

  trackCTAClick(
    ctaType: keyof typeof CTA_TYPES,
    payload?: AnalyticsEventParams
  ): void {
    this.trackEvent(ANALYTICS_EVENTS.CTA_CLICK, {
      cta_type: CTA_TYPES[ctaType],
      timestamp: new Date().toISOString(),
      ...payload,
    });
  }
}

export const analytics = Analytics.getInstance();
