import React, { useState, useEffect } from 'react';
import CodeBlock from '@theme/CodeBlock';

const CodeEmbed = ({ src }) => {
    const [code, setCode] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let isMounted = true;

        const fetchCodeFromUrl = async (url) => {
            if (!isMounted) return;

            setLoading(true);
            setError(null);

            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.text();
                if (isMounted) {
                    setCode(data);
                }
            } catch (err) {
                console.error('Failed to fetch code:', err);
                if (isMounted) {
                    setError(err);
                    setCode(`// Failed to load code from ${url}\n// ${err.message}`);
                }
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        };

        if (src) {
            fetchCodeFromUrl(src);
        }

        return () => {
            isMounted = false;
        };
    }, [src]);

    const githubUrl = src ? src.replace('https://raw.githubusercontent.com', 'https://github.com').replace('/refs/heads/', '/blob/') : null;
    const fileName = src ? src.substring(src.lastIndexOf('/') + 1) : null;

    return (
        loading ? (
            <div>Loading code...</div>
        ) : error ? (
            <div>Error: {error.message}</div>
        ) : (
            <div style={{ backgroundColor: '#f5f5f5', padding: '0px', borderRadius: '5px' }}>
                <div style={{ textAlign: 'right' }}>
                    {githubUrl && (
                        <a href={githubUrl} target="_blank" rel="noopener noreferrer" style={{ paddingRight: '15px', color: 'gray', fontSize: '0.8em', fontStyle: 'italic', display: 'inline-flex', alignItems: 'center' }}>
                            View on GitHub
                        </a>
                    )}
                </div>
                <CodeBlock title={fileName} className="language-java">{code}</CodeBlock>
            </div>
        )
    );
};

export default CodeEmbed;
