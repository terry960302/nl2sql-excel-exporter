import styled from "@emotion/styled";
import logo from "@assets/nl2sql_logo_no_bg.png";

const FooterContainer = styled.footer`
  width: 100%;
  padding: 4rem 0;
  background-color: #f8f8f8;
`;

const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
`;

const ContentWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
`;

const LeftSection = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const LogoWrapper = styled.div`
  width: fit-content;
`;

const Logo = styled.img`
  width: 120px;
  height: auto;
`;

const CompanyInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const CompanyName = styled.h3`
  font-size: 24px;
  font-weight: 500;
  color: #005f91;
  letter-spacing: -1.08px;
  margin: 0;
`;

const CompanyDescription = styled.p`
  font-size: 16px;
  font-weight: 400;
  color: #4a4a4a;
  letter-spacing: -0.72px;
  line-height: 1.5;
  margin: 0;
`;

const RightSection = styled.div`
  display: flex;
  text-align: right;
  flex-direction: column;
  justify-content: space-between;
`;

const Copyright = styled.p`
  font-size: 16px;
  font-weight: 400;
  color: #4a4a4a;
  letter-spacing: -0.72px;
  margin: 0;
`;

const Footer = () => {
  return (
    <FooterContainer>
      <Container>
        <ContentWrapper>
          <LeftSection>
            <LogoWrapper>
              <Logo src={logo} alt="Logo" loading="lazy" />
            </LogoWrapper>
            <CompanyInfo>
              <CompanyName>NL2SQL Studio</CompanyName>
              <CompanyDescription>
                NL2SQL Studio는 데이터 조회 작업을 AI로 자동화하는 솔루션입니다.
              </CompanyDescription>
            </CompanyInfo>
          </LeftSection>
          <RightSection>
            <Copyright>© 2025 All Rights Reserved</Copyright>
          </RightSection>
        </ContentWrapper>
      </Container>
    </FooterContainer>
  );
};

export default Footer;
