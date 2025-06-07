import { ReactNode } from "react";
import clsx from "clsx";

interface SubHeadingProps {
  children: ReactNode;
  className?: string;
}

const SubHeading = ({ children, className = "" }: SubHeadingProps) => {
  return (
    <p
      className={clsx("text-lg md:text-xl text-gray-600 max-w-2xl", className)}
    >
      {children}
    </p>
  );
};

export default SubHeading;
