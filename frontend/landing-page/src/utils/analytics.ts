// Google Analytics 이벤트 추적 함수
export const trackEvent = (
  eventName: string,
  eventParams?: Record<string, any>
) => {
  if (typeof window !== "undefined" && window.gtag) {
    window.gtag("event", eventName, eventParams);
  }
};

// CTA 버튼 클릭 이벤트 추적
export const trackCTAClick = (ctaType: string) => {
  trackEvent("cta_click", {
    cta_type: ctaType,
    timestamp: new Date().toISOString(),
  });
};
