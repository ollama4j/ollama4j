import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import BuyMeACoffee from '@site/src/components/BuyMeACoffee';
import Heading from '@theme/Heading';
import styles from './index.module.css';
import BrowserOnly from '@docusaurus/BrowserOnly';
import LatestRelease from '@site/src/components/LatestRelease';



function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <img
          src="img/logo.svg"
          alt="Ollama4j Logo"
          className={styles.logo}
          style={{ maxWidth: '20vh' }}
        />
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link className="button button--secondary button--lg" to="/intro">
            Getting Started
          </Link>
        </div>
        <div style={{ marginTop: '3rem' }}>
          <LatestRelease />
        </div>
      </div>
    </header>
  );
}

export default function Home() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="Description will go into a meta tag in <head />">
      <HomepageHeader />
      <main>
        <HomepageFeatures />
        <BrowserOnly>
          {() => <BuyMeACoffee />}
        </BrowserOnly>
      </main>
    </Layout>
  );
}