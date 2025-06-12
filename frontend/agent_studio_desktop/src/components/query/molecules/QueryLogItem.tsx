import React from "react";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Download } from "lucide-react";
import { QueryStatusTag, QueryStatus } from "../atoms/QueryStatusTag";
import { ImprovePromptPanel } from "./ImprovePromptPanel";

export interface QueryLog {
  id: string;
  naturalText: string;
  sql: string;
  downloadUrl: string;
  rowCount: number;
  elapsedTime: number;
  status: QueryStatus;
}

interface Props {
  item: QueryLog;
}

export const QueryLogItem: React.FC<Props> = ({ item }) => {
  return (
    <Card>
      <CardHeader className="flex flex-row items-start justify-between">
        <div>
          <CardTitle className="text-sm">{item.naturalText}</CardTitle>
        </div>
        <QueryStatusTag status={item.status} />
      </CardHeader>
      <CardContent className="space-y-2">
        <div>
          <p className="text-sm font-semibold">SQL</p>
          <pre className="mt-1 bg-muted p-2 rounded-md overflow-x-auto text-xs">
            <code>{item.sql || ""}</code>
          </pre>
        </div>
        {item.status === "COMPLETED" && (
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">
              {item.elapsedTime}s · {item.rowCount} rows
            </span>
            <Button variant="outline" size="sm" asChild>
              <a href={item.downloadUrl} className="flex items-center gap-1">
                <Download className="size-4" /> Excel
              </a>
            </Button>
          </div>
        )}
        <ImprovePromptPanel />
      </CardContent>
    </Card>
  );
};
