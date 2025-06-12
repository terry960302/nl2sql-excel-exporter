import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { authClient } from "@/lib/api/authClient";
import { useAuthStore } from "@/stores/authStore";

const LoginContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f5f5;
`;

const LoginForm = styled.form`
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h1`
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
`;

const StyledInput = styled(Input)`
  width: 100%;
  margin-bottom: 1rem;
`;

const SignupLink = styled.p`
  text-align: center;
  margin-top: 1rem;

  a {
    color: #4a90e2;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
`;

export const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { setTokens } = useAuthStore();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      const { accessToken, refreshToken } = await authClient.login({
        email,
        password,
      });
      setTokens(accessToken, refreshToken);
      navigate("/datasources");
    } catch (error: any) {
      setError(error.response?.data?.message || "로그인에 실패했습니다.");
    }
  };

  return (
    <LoginContainer>
      <LoginForm onSubmit={handleSubmit}>
        <Title>로그인</Title>
        {error && <div className="text-red-500 mb-4">{error}</div>}
        <StyledInput
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <StyledInput
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <Button type="submit" className="w-full">
          로그인
        </Button>
        <SignupLink>
          계정이 없으신가요? <a href="/signup">회원가입</a>
        </SignupLink>
      </LoginForm>
    </LoginContainer>
  );
};
