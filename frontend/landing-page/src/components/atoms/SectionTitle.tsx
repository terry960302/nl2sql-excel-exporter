import { ReactNode } from "react";
import clsx from "clsx";

interface SectionTitleProps {
  title: string;
  subtitle?: ReactNode;
  description?: string;
  className?: string;
}

const SectionTitle = ({
  title,
  subtitle,
  description,
  className = "",
}: SectionTitleProps) => {
  return (
    <div className={clsx("text-center space-y-4", className)}>
      <h2 className="text-2xl font-semibold text-gray-900">{title}</h2>
      {subtitle && (
        <div className="text-3xl md:text-4xl font-bold text-gray-900">
          {subtitle}
        </div>
      )}
      {description && (
        <p className="text-lg text-gray-600 max-w-2xl mx-auto">{description}</p>
      )}
    </div>
  );
};

export default SectionTitle;
