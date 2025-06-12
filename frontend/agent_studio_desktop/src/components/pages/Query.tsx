import React, { useState } from "react";
import { NaturalLanguageQuerySection } from "@/components/query/organisms/NaturalLanguageQuerySection";
import { QueryHistoryList } from "@/components/query/organisms/QueryHistoryList";
import { QueryLog, QueryStatus } from "@/components/query/molecules/QueryLogItem";

export const Query: React.FC = () => {
  const [logs, setLogs] = useState<QueryLog[]>([]);

  const handleSubmit = (text: string) => {
    const id = crypto.randomUUID();
    const newLog: QueryLog = {
      id,
      naturalText: text,
      sql: "",
      downloadUrl: "",
      rowCount: 0,
      elapsedTime: 0,
      status: "PENDING" as QueryStatus,
    };
    setLogs((prev) => [newLog, ...prev]);

    // 임시 시뮬레이션
    setTimeout(() => {
      setLogs((prev) =>
        prev.map((log) =>
          log.id === id
            ? {
                ...log,
                status: "COMPLETED" as QueryStatus,
                sql: `SELECT * FROM table -- ${text}`,
                downloadUrl: "#",
                rowCount: Math.floor(Math.random() * 100),
                elapsedTime: 2,
              }
            : log
        )
      );
    }, 2000);
  };

  return (
    <div className="space-y-6">
      <NaturalLanguageQuerySection onSubmit={handleSubmit} />
      <QueryHistoryList items={logs} />
    </div>
  );
};

export default Query;
