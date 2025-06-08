import { ReactNode } from "react";
import styled from "@emotion/styled";
import Header from "@organisms/Header";

interface LayoutProps {
  children: ReactNode;
}

const Container = styled.div`
  position: relative;
  width: 100%;
  min-height: 100vh;
  background-color: #ffffff;
`;

const ContentWrapper = styled.div`
  position: relative;
  width: 100%;
  max-width: 100vw;
  margin: 0 auto;
`;

const MainContent = styled.main`
  position: relative;
  width: 100%;
  flex: 1;
`;

const Layout = ({ children }: LayoutProps) => {
  return (
    <Container>
      <ContentWrapper>
        <Header />
        <MainContent>{children}</MainContent>
      </ContentWrapper>
    </Container>
  );
};

export default Layout;
