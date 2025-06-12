import React from "react";
import { Badge } from "@/components/ui/badge";
import { LoaderCircle } from "lucide-react";

export type QueryStatus = "PENDING" | "COMPLETED" | "FAILED";

interface Props {
  status: QueryStatus;
}

export const QueryStatusTag: React.FC<Props> = ({ status }) => {
  if (status === "PENDING") {
    return (
      <Badge variant="secondary" className="flex items-center gap-1">
        <LoaderCircle className="size-3 animate-spin" /> 진행중
      </Badge>
    );
  }
  if (status === "COMPLETED") {
    return <Badge>완료</Badge>;
  }
  return <Badge variant="destructive">실패</Badge>;
};
