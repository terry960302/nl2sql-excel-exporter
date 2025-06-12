import React, { useState } from "react";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";
import { NaturalLanguageInput } from "../atoms/NaturalLanguageInput";

interface Props {
  onSubmit: (text: string) => void;
}

export const NaturalLanguageQuerySection: React.FC<Props> = ({ onSubmit }) => {
  const [text, setText] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!text.trim()) return;
    onSubmit(text);
    setText("");
  };

  return (
    <Card className="max-w-xl mx-auto">
      <form onSubmit={handleSubmit}>
        <CardHeader>
          <CardTitle>자연어 질의</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <NaturalLanguageInput
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="예: 지난달 VIP 고객 비율은?"
          />
          <p className="text-sm text-muted-foreground">
            예) 지난달 VIP 고객 비율은? <br />예) 어제 가입한 회원 수는?
          </p>
        </CardContent>
        <CardFooter className="justify-end">
          <Button type="submit">
            실행 <ArrowRight className="size-4 ml-2" />
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
};
