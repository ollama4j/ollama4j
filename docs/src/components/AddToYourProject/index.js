import React, { useState, useEffect } from 'react';
import CodeBlock from '@theme/CodeBlock';

const AddToYourProject = () => {
    const [releaseInfo, setReleaseInfo] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchLatestRelease = async () => {
            setLoading(true);
            setError(null);
            try {
                const response = await fetch('https://api.github.com/repos/ollama4j/ollama4j/releases/latest');
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                setReleaseInfo(data);
            } catch (err) {
                console.error('Failed to fetch release info:', err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchLatestRelease();
    }, []);

    return (
        <div style={{ width: '100%' }}>
            {loading ? (
                <div>Loading latest release info...</div>
            ) : error ? (
                <div>Error: {error.message}</div>
            ) : releaseInfo ? (
                <>
                    <h4>Using Maven <code>pom.xml</code></h4>
                    <CodeBlock className="language-xml">
                        {`<dependency>
    <groupId>io.github.ollama4j</groupId>
    <artifactId>ollama4j</artifactId>
    <version>${releaseInfo.name}</version>
</dependency>`}
                    </CodeBlock>
                    <h4>Using Groovy-based <code>build.gradle</code></h4>
                    <CodeBlock className="language-groovy">
                        {`dependencies {
    implementation 'io.github.ollama4j:ollama4j:${releaseInfo.name}'
}`}
                    </CodeBlock>
                    <h4>For Kotlin-based <code>build.gradle.kts</code></h4>
                    <CodeBlock className="language-kotlin">
                        {`dependencies {
    implementation("io.github.ollama4j:ollama4j:${releaseInfo.name}")
}`}
                    </CodeBlock>
                </>
            ) : null}
        </div>
    );
};

export default AddToYourProject;
