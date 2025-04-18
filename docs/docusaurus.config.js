// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'Ollama4j',
    tagline: 'Java library for interacting with Ollama.',
    favicon: 'img/favicon.ico',

    // Set the production url of your site here
    url: 'https://your-docusaurus-site.example.com',
    // Set the /<baseUrl>/ pathname under which your site is served
    // For GitHub pages deployment, it is often '/<projectName>/'
    baseUrl: '/ollama4j/',

    // GitHub pages deployment config.
    // If you aren't using GitHub pages, you don't need these.
    organizationName: 'ollama4j', // Usually your GitHub org/user name.
    projectName: 'ollama4j', // Usually your repo name.

    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',

    // Even if you don't use internationalization, you can use this field to set
    // useful metadata like html lang. For example, if your site is Chinese, you
    // may want to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },

    presets: [
        [
            'classic',
            /** @type {import('@docusaurus/preset-classic').Options} */
            ({
                docs: {
                    path: 'docs',
                    routeBasePath: '', // change this to any URL route you'd want. For example: `home` - if you want /home/intro.
                    sidebarPath: './sidebars.js',
                    // Please change this to your repo.
                    // Remove this to remove the "edit this page" links.
                    editUrl:
                        'https://github.com/ollama4j/ollama4j/blob/main/docs',
                },
                blog: {
                    showReadingTime: true,
                    // Please change this to your repo.
                    // Remove this to remove the "edit this page" links.
                    editUrl:
                        'https://github.com/ollama4j/ollama4j/blob/main/docs',
                },
                theme: {
                    customCss: './src/css/custom.css',
                },
                gtag: {
                    trackingID: 'G-G7FLH6FNDC',
                    anonymizeIP: false,
                },
            }),
        ],
    ],

    themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
            // Replace with your project's social card
            image: 'img/docusaurus-social-card.jpg',
            navbar: {
                title: 'Ollama4j',
                logo: {
                    alt: 'Ollama4j Logo',
                    src: 'img/logo.svg',
                },
                items: [
                    {
                        type: 'docSidebar',
                        sidebarId: 'tutorialSidebar',
                        position: 'left',
                        label: 'Docs',
                    },
                    {to: 'https://github.com/ollama4j/ollama4j-examples', label: 'Examples', position: 'left'},
                    {to: 'https://ollama4j.github.io/ollama4j/apidocs/', label: 'Javadoc', position: 'left'},
                    {to: 'https://ollama4j.github.io/ollama4j/doxygen/html/', label: 'Doxygen', position: 'left'},
                    {to: '/blog', label: 'Blog', position: 'left'},
                    {
                        href: 'https://github.com/ollama4j/ollama4j',
                        label: 'GitHub',
                        position: 'right',
                    },
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'Quick Links',
                        items: [
                            {
                                label: 'Ollama4j Examples',
                                to: 'https://github.com/ollama4j/ollama4j-examples',
                            },
                            {
                                label: 'Blog',
                                to: '/blog',
                            },
                            {
                                label: 'GitHub',
                                href: 'https://github.com/ollama4j/ollama4j',
                            },
                        ],
                    },
                    {
                        title: 'Stuff built with Ollama4j',
                        items: [
                            {
                                label: 'Ollama4j Web UI',
                                to: 'https://github.com/ollama4j/ollama4j-web-ui',
                            },
                            {
                                label: 'Ollama4j Desktop UI with Swing',
                                to: 'https://github.com/ollama4j/ollama4j-ui',
                            },
                        ],
                    },
                    {
                        title: 'Community',
                        items: [
                            {
                                label: 'Stack Overflow',
                                href: 'https://stackoverflow.com/questions/tagged/ollama4j',
                            },
                            {
                                label: 'Twitter',
                                href: 'https://twitter.com/ollama4j',
                            },
                        ],
                    }
                ],
                copyright: `Ollama4j Documentation ${new Date().getFullYear()}. Built with Docusaurus.`,
            },
            prism: {
                theme: prismThemes.github,
                darkTheme: prismThemes.dracula,
                additionalLanguages: ['java'],
            },
            algolia: {
                // The application ID provided by Algolia
                appId: '7HJ3MZ6GBX',
                // Public API key: it is safe to commit it
                apiKey: '3037a6d8706a6347b1844ca6ecd582b0',
                indexName: 'ollama4jio',
                // Optional: see doc section below
                contextualSearch: true,
                // Optional: Specify domains where the navigation should occur through window.location instead on history.push. Useful when our Algolia config crawls multiple documentation sites and we want to navigate with window.location.href to them.
                externalUrlRegex: 'external\\.com|domain\\.com',
                // Optional: Replace parts of the item URLs from Algolia. Useful when using the same search index for multiple deployments using a different baseUrl. You can use regexp or string in the `from` param. For example: localhost:3000 vs myCompany.com/docs
                replaceSearchResultPathname: {
                    from: '/docs/', // or as RegExp: /\/docs\//
                    to: '/',
                },
                // Optional: Algolia search parameters
                searchParameters: {},
                // Optional: path for search page that enabled by default (`false` to disable it)
                searchPagePath: 'search',
                // Optional: whether the insights feature is enabled or not on Docsearch (`false` by default)
                insights: false,
                //... other Algolia params
            },
        }),
    markdown: {
        mermaid: true,
    },
    themes: ['@docusaurus/theme-mermaid']
};

export default config;
