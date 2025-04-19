import React from 'react';
import "react-image-gallery/styles/css/image-gallery.css";
import ImageGallery from "react-image-gallery";

const images = [
    {
        original: require('@site/static/img/ollama4j-ui/1.png').default,
        thumbnail: require('@site/static/img/ollama4j-ui/1.png').default,
    },
    {
        original: require('@site/static/img/ollama4j-ui/2.png').default,
        thumbnail: require('@site/static/img/ollama4j-ui/2.png').default,
    },
    {
        original: require('@site/static/img/ollama4j-ui/3.png').default,
        thumbnail: require('@site/static/img/ollama4j-ui/3.png').default,
    },
    {
        original: require('@site/static/img/ollama4j-ui/4.png').default,
        thumbnail: require('@site/static/img/ollama4j-ui/4.png').default,
    },
];

class Ollama4jUIImageCarousel extends React.Component {
    renderItem = (item) => {
        return (
            <div className="image-gallery-image" style={{ textAlign: 'center' }}>
                <img
                    src={item.original}
                    alt=""
                    style={{ maxHeight: '500px', width: 'auto', maxWidth: '100%' }}
                />
            </div>
        );
    };

    render() {
        return (
            <div style={{ margin: '0 auto', maxWidth: '800px' }}>
                <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center' }}>
                    <h2>Explore the stuff we have built with Ollama4j</h2>
                    <h4>
                        <a href="https://github.com/ollama4j/ollama4j-ui" target='_blank' rel="noopener noreferrer">
                            Ollama4j UI - Desktop UI built in Java with Swing
                        </a>
                    </h4>
                </div>
                <ImageGallery items={images} renderItem={this.renderItem} />
            </div>
        );
    }
}

export default Ollama4jUIImageCarousel;
