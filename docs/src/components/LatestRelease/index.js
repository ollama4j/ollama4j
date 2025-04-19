import React, { useState, useEffect } from 'react';

const LatestRelease = ({ showReleaseDate }) => {
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
        <div style={{ display: 'flex', justifyContent: 'center' }}>
            {loading ? (
                <div>Loading latest release info...</div>
            ) : error ? (
                <div>Error: {error.message}</div>
            ) : releaseInfo ? (
                <div>
                    {/* <h4 style={{ display: 'flex', justifyContent: 'center'}}>Latest Release</h4> */}
                    <div>
                        <span style={{ fontWeight: 'bold'}}>Latest Version</span>: <a href={releaseInfo.html_url} target='_blank' rel="noopener noreferrer"><span style={{color: 'white', fontWeight: 'bold', backgroundColor:'#11bc11', borderRadius: '15px', padding: '5px'}}>{releaseInfo.name}</span></a>
                        {showReleaseDate && ` released on ${new Date(releaseInfo.published_at).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })}`}
                    </div>
                    {/* <pre style={{ whiteSpace: 'pre-wrap' }}>
                        {JSON.stringify(releaseInfo, null, 2)}
                    </pre> */}
                </div>
            ) : null}
        </div>
    );
};

export default LatestRelease;
