import styled from "@emotion/styled";
import excelSampleScreen from "@assets/screens/excel_sample_screen.png";

const ImageContainer = styled.div`
  width: 100%;
  position: relative;
  aspect-ratio: 4/2.24;
  overflow: hidden;
  border-radius: 1rem;
`;

const Image = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
`;

const FeatureImage = () => {
  return (
    <ImageContainer>
      <Image src={excelSampleScreen} alt="excel sample" loading="lazy" />
    </ImageContainer>
  );
};

export default FeatureImage;
