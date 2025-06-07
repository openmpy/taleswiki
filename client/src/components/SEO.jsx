import { Helmet } from "react-helmet-async";

const SEO = ({ title }) => {
  const defaultTitle = "테일즈위키";

  return (
    <Helmet>
      <title>{title ? `${defaultTitle} - ${title}` : defaultTitle}</title>
    </Helmet>
  );
};

export default SEO;
