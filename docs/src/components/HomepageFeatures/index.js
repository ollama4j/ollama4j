import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';
import Ollama4jUIImageCarousel from '@site/src/components/Ollama4jUIImageCarousel';

const FeatureList = [
  {
    title: 'Developer-Friendly',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Provides clean and simple <code>APIs</code> for interacting with Ollama, including model management, chat functionalities, and authentication capability when Ollama is deployed behind proxy server.
      </>
    ),
  },
  {
    title: 'Chat with Images',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Supports interactions with vision/image models, allowing you to build applications that can understand and respond to visual content.  Upload images directly into your chat sessions and receive intelligent, context-aware replies.
      </>
    ),
  },
  {
    title: 'Tools Support',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Supports tool/function calling with tool calling models such as mistral, llama3.x, qwen, etc. Empower your models to interact with external services and data sources, enabling more complex and dynamic AI workflows.
      </>
    ),
  },
];
const UsageList = [
  {
    title: 'Datafaker',
    Svg: require('@site/static/img/datafaker.svg').default,
    link: 'https://www.datafaker.net/',
    description: (
      <>
        A powerful fake data generation library designed for JVM programs, offering over 200 data providers to easily create realistic and diverse datasets within minutes.
      </>
    ),
    imageSize: '20%',
  },
  {
    title: 'Katie',
    Svg: require('@site/static/img/katie_logo_v3.svg').default,
    link: 'https://katie.qa/home',
    description: (
      <>
        An Open Source AI-based question-answering platform that helps companies and organizations make their private domain knowledge accessible and useful to their employees and customers.
      </>
    ),
    imageSize: '30%',
  },
  {
    title: 'AI Player',
    Svg: require('@site/static/img/ai-player.svg').default,
    link: 'https://modrinth.com/mod/ai-player',
    description: (
      <>
        A minecraft mod which aims to add a "second player" into the game which will actually be intelligent.
      </>
    ),
    imageSize: '15%',
  },
  {
    title: 'Ollama Translator Plugin',
    Svg: require('@site/static/img/minecraft-spigot.svg').default,
    link: 'https://github.com/liebki/ollama-translator',
    description: (
      <>
        A minecraft 1.21 spigot plugin allows to easily break language barriers by using ollama on the server to translate all messages into a specfic target language.
      </>
    ),
    imageSize: '20%',
  },
  {
    title: 'JnsCLI',
    Svg: require('@site/static/img/jnscli.svg').default,
    link: 'https://github.com/mirum8/jnscli',
    description: (
      <>
        JnsCLI is a command-line tool for Jenkins, allowing you to manage jobs, builds, and configurations directly from the terminal. It also features AI-powered error analysis for quick troubleshooting.
      </>
    ),
    imageSize: '20%',
  },
  {
    title: 'Featured in a Research Article on AI-Assisted Code Optimization',
    Svg: require('@site/static/img/pmc-logo.svg').default,
    link: 'https://pmc.ncbi.nlm.nih.gov/articles/PMC11750896/',
    description: (
      <>
        Ollama4j was used in a research article – “Large Language Model Based Mutations in Genetic Improvement” (PubMed Central).
      </>
    ),
    imageSize: '50%',
  },
];

function Feature({ Svg, title, description }) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

function Usage({ Svg, title, description, link, imageSize }) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" style={{ width: imageSize }} />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3" style={{ color: 'red' }}>
          {link ? (
            <a href={link} target="_blank" rel="noopener noreferrer" style={{ color: '#11bc11' }}>
              {title}
            </a>
          ) : (
            <span style={{ color: 'red' }}>{title}</span>
          )}
        </Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <>
      <section className={styles.features}>
        <div className="container">
          <div className="row">
            {FeatureList.map((props, idx) => (
              <Feature key={idx} {...props} />
            ))}
          </div>
        </div>
      </section>
      <hr />
      <section className={styles.features}>
        <div className="container">
          <div style={{ fontSize: '20px', fontWeight: 'bold', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            Check out who's using Ollama4j
          </div>
          <div className="row">
            {UsageList.map((props, idx) => (
              <Usage key={idx} {...props} />
            ))}
          </div>
        </div>
      </section>
      <section className={styles.features}>
        <div className="container">
          <Ollama4jUIImageCarousel></Ollama4jUIImageCarousel>
        </div>
      </section>
    </>
  );
}
