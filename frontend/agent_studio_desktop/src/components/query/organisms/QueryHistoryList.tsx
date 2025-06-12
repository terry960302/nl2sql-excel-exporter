import React from "react";
import { QueryLogItem, QueryLog } from "../molecules/QueryLogItem";

interface Props {
  items: QueryLog[];
}

export const QueryHistoryList: React.FC<Props> = ({ items }) => (
  <div className="space-y-4">
    {items.map((item) => (
      <QueryLogItem key={item.id} item={item} />
    ))}
  </div>
);
