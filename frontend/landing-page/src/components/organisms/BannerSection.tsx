import styled from "@emotion/styled";
import BannerImage from "@atoms/BannerImage";
import { breakpoints } from "@constants/breakpoints";

const Section = styled.section`
  width: 100%;
  padding: 4rem 0;
  background-color: #ffffff;
  @media (max-width: ${breakpoints.mobile}) {
    padding: 2rem 0;
  }
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  @media (max-width: ${breakpoints.mobile}) {
    padding: 0 1rem;
  }
`;

const BannerSection = () => {
  return (
    <Section>
      <Container>
        <BannerImage />
      </Container>
    </Section>
  );
};

export default BannerSection;
