interface NavigationProps {
  className?: string;
}

const Navigation = ({ className = "" }: NavigationProps) => {
  return (
    <nav className={`flex items-center gap-6 ${className}`}>
      <a
        href="/services"
        className="text-gray-900 hover:text-primary transition-colors"
      >
        Services
      </a>
    </nav>
  );
};

export default Navigation;
