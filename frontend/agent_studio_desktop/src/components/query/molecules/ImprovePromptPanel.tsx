import React from "react";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
} from "@/components/ui/dropdown-menu";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";

export const ImprovePromptPanel: React.FC = () => (
  <DropdownMenu>
    <DropdownMenuTrigger asChild>
      <Button variant="ghost" size="sm" className="text-xs">
        Improve Prompt
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent className="p-2 space-y-2">
      <label className="flex items-center gap-2 text-sm">
        <Checkbox id="opt1" /> <span>조건을 더 자세히</span>
      </label>
      <label className="flex items-center gap-2 text-sm">
        <Checkbox id="opt2" /> <span>출력 열 제한</span>
      </label>
      <label className="flex items-center gap-2 text-sm">
        <Checkbox id="opt3" /> <span>시간 필터 명시</span>
      </label>
      <label className="flex items-center gap-2 text-sm">
        <Checkbox id="opt4" /> <span>JOIN 여부 명시</span>
      </label>
    </DropdownMenuContent>
  </DropdownMenu>
);
