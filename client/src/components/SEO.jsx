import { Helmet } from "react-helmet-async";
import { useLocation } from "react-router-dom";

const SEO = ({ title, description, keywords, ogImage }) => {
  const location = useLocation();
  const defaultTitle = "테일즈위키";
  const defaultDescription =
    "테일즈위키는 공익성을 가지는 커뮤니티입니다. 누구나 문서를 조회, 작성, 편집할 수 있습니다.";
  const defaultKeywords = "테일즈런너, 테일즈위키, 게임위키, 게임정보";
  const defaultOgImage = "/og-image.png";
  const currentUrl = `https://taleswiki.com${location.pathname}`;

  return (
    <Helmet>
      <title>{title ? `${defaultTitle} - ${title}` : defaultTitle}</title>
      <meta name="description" content={description || defaultDescription} />
      <meta name="keywords" content={keywords || defaultKeywords} />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content="website" />
      <meta property="og:url" content={currentUrl} />
      <meta property="og:title" content={title || defaultTitle} />
      <meta
        property="og:description"
        content={description || defaultDescription}
      />
      <meta property="og:image" content={ogImage || defaultOgImage} />
      <meta property="og:image:width" content="1200" />
      <meta property="og:image:height" content="630" />
      <meta property="og:site_name" content="테일즈위키" />
      <meta property="og:locale" content="ko_KR" />

      {/* Twitter */}
      <meta name="twitter:card" content="summary_large_image" />
      <meta name="twitter:url" content={currentUrl} />
      <meta name="twitter:title" content={title || defaultTitle} />
      <meta
        name="twitter:description"
        content={description || defaultDescription}
      />
      <meta name="twitter:image" content={ogImage || defaultOgImage} />
    </Helmet>
  );
};

export default SEO;
