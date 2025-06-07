import styled from "@emotion/styled";

interface DemoSectionVideoProps {
  videoUrl: string;
}

const MediaContainer = styled.div`
  width: 100%;
  max-width: 1000px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
`;

const Video = styled.video`
  width: 100%;
  height: auto;
  display: block;
`;

const DemoSectionVideo = ({ videoUrl }: DemoSectionVideoProps) => {
  return (
    <MediaContainer>
      <Video controls>
        <source src={videoUrl} type="video/mp4" />
        Your browser does not support the video tag.
      </Video>
    </MediaContainer>
  );
};

export default DemoSectionVideo;
