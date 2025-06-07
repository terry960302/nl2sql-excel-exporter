interface HeroImageProps {
  className?: string;
}

const HeroImage = ({ className = "" }: HeroImageProps) => {
  return (
    <div className={`relative ${className}`} aria-label="Globe graphic">
      <img
        alt="Globe graphic"
        className="w-full h-auto"
        src="/_assets/v9/30ca89838aacef8bff03e2f6795db2b153b180f7.png"
        srcSet="/_assets/v9/30ca89838aacef8bff03e2f6795db2b153b180f7.png 1421w, /_assets/v9/30ca89838aacef8bff03e2f6795db2b153b180f7.png?w=512 512w"
        sizes="273px"
      />
      <div className="absolute inset-0 bg-gradient-to-r from-white/0 to-white/10" />
    </div>
  );
};

export default HeroImage;
