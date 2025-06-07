import styled from "@emotion/styled";
import Layout from "@templates/Layout";
import HeroSection from "@organisms/HeroSection";
import ServicesSection from "@organisms/ServicesSection";
import TestimonialsSection from "@organisms/TestimonialsSection";
import PlansSection from "@/components/organisms/PlansSection";
import PlanSection from "@organisms/PlanSection";
import CTASection from "@organisms/CTASection";
import DemoSection from "@organisms/DemoSection";
import Footer from "@organisms/Footer";

const AppContainer = styled.div`
  width: 100%;
  min-height: 100vh;
`;

function App() {
  return (
    <AppContainer>
      <Layout>
        <HeroSection />
        <ServicesSection />
        <TestimonialsSection />
        <DemoSection />
        <PlansSection />
        <PlanSection />
        <CTASection />
      </Layout>
      <Footer />
    </AppContainer>
  );
}

export default App;
