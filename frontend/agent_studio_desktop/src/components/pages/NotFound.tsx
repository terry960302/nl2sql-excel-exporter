import React from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";

const NotFound: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-[60vh] flex flex-col items-center justify-center">
      <h1 className="text-9xl font-bold text-primary">404</h1>
      <h2 className="text-2xl font-semibold mt-4">페이지를 찾을 수 없습니다</h2>
      <p className="text-muted-foreground mt-2">
        요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
      </p>
      <div className="mt-8 space-x-4">
        <Button onClick={() => navigate(-1)} variant="outline">
          이전 페이지로
        </Button>
        <Button onClick={() => navigate("/")} variant="default">
          홈으로 가기
        </Button>
      </div>
    </div>
  );
};

export default NotFound;
