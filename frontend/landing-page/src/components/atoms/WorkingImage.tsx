import styled from "@emotion/styled";

const ImageContainer = styled.div`
  width: 100%;
  position: relative;
  aspect-ratio: 16/9;
  overflow: hidden;
`;

const Image = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
`;

const WorkingImage = () => {
  return (
    <ImageContainer>
      <Image
        src="/_assets/v9/da1b9d9a7611a453dcf81bdf037b97ad401d8e58.png"
        alt="Person working on laptop"
        loading="lazy"
      />
    </ImageContainer>
  );
};

export default WorkingImage;
