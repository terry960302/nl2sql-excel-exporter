interface LogoProps {
  className?: string;
}

const Logo = ({ className = "" }: LogoProps) => {
  return (
    <div className={`cursor-pointer ${className}`} role="link" tabIndex={0}>
      <div className="relative">
        <img
          src="/_assets/v9/e59e92e7e8f053c2c42441b825d0843162a46065.svg"
          alt="Logo"
          className="w-auto h-auto"
        />
      </div>
    </div>
  );
};

export default Logo;
