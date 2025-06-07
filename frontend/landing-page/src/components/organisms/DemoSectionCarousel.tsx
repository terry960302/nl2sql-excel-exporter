import styled from "@emotion/styled";
import { useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface DemoSectionCarouselProps {
  images: string[];
}

const MediaContainer = styled.div`
  width: 100%;
  max-width: 1000px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  aspect-ratio: 16/9;
`;

const ImageCarousel = styled.div`
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
`;

const ImageWrapper = styled.div<{ currentIndex: number }>`
  display: flex;
  width: 100%;
  height: 100%;
  transform: translateX(${({ currentIndex }) => `-${currentIndex * 100}%`});
  transition: transform 0.5s ease-in-out;
`;

const Image = styled.img`
  width: 100%;
  height: 100%;
  flex-shrink: 0;
  object-fit: cover;
  object-position: top;
`;

const NavigationButton = styled.button<{ direction: "left" | "right" }>`
  position: absolute;
  top: 50%;
  ${({ direction }) => direction}: 1rem;
  transform: translateY(-50%);
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;

  &:hover {
    background-color: rgba(255, 255, 255, 0.95);
  }

  svg {
    width: 24px;
    height: 24px;
    color: #2e4f21;
  }
`;

const DemoSectionCarousel = ({ images }: DemoSectionCarouselProps) => {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const handlePrevious = () => {
    setCurrentImageIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const handleNext = () => {
    setCurrentImageIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
  };

  return (
    <MediaContainer>
      <ImageCarousel>
        <ImageWrapper currentIndex={currentImageIndex}>
          {images.map((image, index) => (
            <Image key={index} src={image} alt={`Demo image ${index + 1}`} />
          ))}
        </ImageWrapper>
        <NavigationButton
          direction="left"
          onClick={handlePrevious}
          aria-label="Previous image"
        >
          <ChevronLeft />
        </NavigationButton>
        <NavigationButton
          direction="right"
          onClick={handleNext}
          aria-label="Next image"
        >
          <ChevronRight />
        </NavigationButton>
      </ImageCarousel>
    </MediaContainer>
  );
};

export default DemoSectionCarousel;
