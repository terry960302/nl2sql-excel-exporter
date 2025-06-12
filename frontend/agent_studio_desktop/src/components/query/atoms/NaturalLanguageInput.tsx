import * as React from "react";
import { cn } from "@/lib/utils";

export interface NaturalLanguageInputProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {}

export const NaturalLanguageInput = React.forwardRef<HTMLTextAreaElement, NaturalLanguageInputProps>(
  ({ className, ...props }, ref) => (
    <textarea
      ref={ref}
      className={cn(
        "w-full min-h-[5rem] rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring resize-none",
        className
      )}
      {...props}
    />
  )
);
NaturalLanguageInput.displayName = "NaturalLanguageInput";
