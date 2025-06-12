import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useAuthStore } from "@/stores/authStore";
import { authClient } from "@/lib/api/authClient";
import { UserInfo } from "@/types/auth";

const Container = styled.div`
  padding: 2rem;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
`;

const Title = styled.h1`
  font-size: 1.5rem;
  color: #333;
`;

const Form = styled.form`
  max-width: 600px;
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const FormGroup = styled.div`
  margin-bottom: 1.5rem;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
`;

const StyledInput = styled(Input)`
  width: 100%;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
`;

export const Me: React.FC = () => {
  const navigate = useNavigate();
  const { clearAuth, accessToken, userId } = useAuthStore();
  const [profile, setProfile] = useState<UserInfo | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    email: "",
  });

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const userInfo = await authClient.getMyInfo(accessToken!);
        setProfile(userInfo);
        setFormData({
          name: userInfo.organization.name,
          email: userInfo.email,
        });
      } catch (error) {
        console.error("프로필 정보를 불러오는데 실패했습니다:", error);
      }
    };

    fetchProfile();
  }, [accessToken, userId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
  };

  const handleLogout = () => {
    clearAuth();
    navigate("/login");
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  if (!profile) {
    return <div>로딩 중...</div>;
  }

  return (
    <Container>
      <Header>
        <Title>내 프로필</Title>
        <Button variant="outline" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>

      <Form onSubmit={handleSubmit}>
        <FormGroup>
          <Label>이름</Label>
          <StyledInput
            name="name"
            value={formData.name}
            onChange={handleChange}
            disabled={!isEditing}
            required
          />
        </FormGroup>

        <FormGroup>
          <Label>이메일</Label>
          <StyledInput
            name="email"
            type="email"
            value={formData.email}
            onChange={handleChange}
            disabled={!isEditing}
            required
          />
        </FormGroup>

        <FormGroup>
          <Label>조직</Label>
          <StyledInput value={profile?.organization.name} disabled />
        </FormGroup>

        <FormGroup>
          <Label>사용량</Label>
          <StyledInput
            value={`${profile?.usedQuota} / ${profile?.totalQuota}`}
            disabled
          />
        </FormGroup>

        <ButtonGroup>
          {isEditing ? (
            <>
              <Button type="submit">저장</Button>
              <Button
                type="button"
                variant="outline"
                onClick={() => setIsEditing(false)}
              >
                취소
              </Button>
            </>
          ) : (
            <Button type="button" onClick={() => setIsEditing(true)}>
              수정
            </Button>
          )}
        </ButtonGroup>
      </Form>
    </Container>
  );
};
