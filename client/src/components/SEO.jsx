import { Helmet } from "react-helmet-async";

const SEO = ({ title, description, keywords, ogImage, ogUrl }) => {
  const defaultTitle = "테일즈위키";
  const defaultDescription =
    "테일즈위키는 공익성을 가지는 커뮤니티입니다. 누구나 문서를 조회, 작성, 편집할 수 있습니다.";
  const defaultKeywords = "테일즈런너, 테일즈위키, 게임위키, 게임정보";
  const defaultOgImage = "/og-image.png";
  const defaultOgUrl = "https://taleswiki.com";

  return (
    <Helmet>
      <title>{title ? `${defaultTitle} - ${title}` : defaultTitle}</title>
      <meta name="description" content={description || defaultDescription} />
      <meta name="keywords" content={keywords || defaultKeywords} />

      {/* Open Graph / Facebook */}
      <meta property="og:type" content="website" />
      <meta property="og:url" content={ogUrl || defaultOgUrl} />
      <meta property="og:title" content={title || defaultTitle} />
      <meta
        property="og:description"
        content={description || defaultDescription}
      />
      <meta property="og:image" content={ogImage || defaultOgImage} />

      {/* Twitter */}
      <meta property="twitter:card" content="summary_large_image" />
      <meta property="twitter:url" content={ogUrl || defaultOgUrl} />
      <meta property="twitter:title" content={title || defaultTitle} />
      <meta
        property="twitter:description"
        content={description || defaultDescription}
      />
      <meta property="twitter:image" content={ogImage || defaultOgImage} />
    </Helmet>
  );
};

export default SEO;
