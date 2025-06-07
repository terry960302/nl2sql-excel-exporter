import styled from "@emotion/styled";
import bannerImage from "@assets/meeting.jpg";

const ImageContainer = styled.div`
  width: 100%;
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: 1rem;
`;

const Image = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
`;

const BannerImage = () => {
  return (
    <ImageContainer>
      <Image src={bannerImage} alt="Person working on laptop" loading="lazy" />
    </ImageContainer>
  );
};

export default BannerImage;
