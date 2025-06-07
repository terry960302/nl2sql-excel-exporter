import React, { useState, useEffect } from "react";
import styled from "@emotion/styled";
import logo from "@assets/nl2sql_logo_no_bg.png";

const HeaderContainer = styled.header<{ isScrolled: boolean }>`
  width: 100%;
  padding: 1.5rem 0;
  background-color: #ffffff;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: box-shadow 0.3s ease;
  box-shadow: ${({ isScrolled }) =>
    isScrolled ? "0 2px 4px rgba(0, 0, 0, 0.1)" : "none"};
`;

const Container = styled.div`
  max-width: 100vw;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const Logo = styled.img`
  height: 40px;
  width: auto;
`;

const Nav = styled.nav`
  display: flex;
  gap: 2rem;
  align-items: center;
`;

const NavLink = styled.a`
  color: #2e4f21;
  text-decoration: none;
  font-size: 16px;
  font-weight: 500;
  transition: color 0.3s ease;
  cursor: pointer;

  &:hover {
    color: #1a2f13;
  }
`;

const Header = () => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      const scrollPosition = window.scrollY;
      setIsScrolled(scrollPosition > 0);
    };

    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const handleNavClick = (
    e: React.MouseEvent<HTMLAnchorElement>,
    id: string
  ) => {
    e.preventDefault();
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  };

  return (
    <HeaderContainer isScrolled={isScrolled}>
      <Container>
        <Logo src={logo} alt="Logo" />
        <Nav>
          <NavLink
            href="#features"
            onClick={(e) => handleNavClick(e, "features")}
          >
            기능 소개
          </NavLink>
          <NavLink
            href="#testimonials"
            onClick={(e) => handleNavClick(e, "testimonials")}
          >
            추천 대상
          </NavLink>
          <NavLink href="#plans" onClick={(e) => handleNavClick(e, "plans")}>
            요금제 안내
          </NavLink>
        </Nav>
      </Container>
    </HeaderContainer>
  );
};

export default Header;
