import { ReactNode } from "react";
import clsx from "clsx";

interface HeadingProps {
  children: ReactNode;
  className?: string;
  as?: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
}

const Heading = ({
  children,
  className = "",
  as: Component = "h1",
}: HeadingProps) => {
  return (
    <Component
      className={clsx(
        "font-bold tracking-tight",
        {
          "text-4xl md:text-5xl lg:text-6xl": Component === "h1",
          "text-3xl md:text-4xl lg:text-5xl": Component === "h2",
          "text-2xl md:text-3xl lg:text-4xl": Component === "h3",
        },
        className
      )}
    >
      {children}
    </Component>
  );
};

export default Heading;
