import React, { useEffect, useState, useRef } from 'react';

const TypewriterTextarea = ({ textContent, typingSpeed = 50, pauseBetweenSentences = 1000, height = '200px', width = '100%' }) => {
  const [text, setText] = useState('');
  const [sentenceIndex, setSentenceIndex] = useState(0);
  const [charIndex, setCharIndex] = useState(0);
  const sentences = textContent ? textContent.split('\n') : [];
  const isTyping = useRef(false);

  useEffect(() => {
    if (!textContent) return;

    if (!isTyping.current) {
      isTyping.current = true;
    }

    if (sentenceIndex >= sentences.length) {
      // Reset to start from the beginning
      setSentenceIndex(0);
      setCharIndex(0);
      setText('');
      return;
    }

    const currentSentence = sentences[sentenceIndex];

    if (charIndex < currentSentence.length) {
      const timeout = setTimeout(() => {
        setText((prevText) => prevText + currentSentence[charIndex]);
        setCharIndex((prevCharIndex) => prevCharIndex + 1);
      }, typingSpeed);

      return () => clearTimeout(timeout);
    } else {
      // Wait a bit, then go to the next sentence
      const timeout = setTimeout(() => {
        setSentenceIndex((prev) => prev + 1);
        setCharIndex(0);
      }, pauseBetweenSentences);

      return () => clearTimeout(timeout);
    }
  }, [charIndex, sentenceIndex, sentences, typingSpeed, pauseBetweenSentences, textContent]);

  return (
    <textarea
      value={text}
      readOnly
      rows={10}
      cols={5}
      style={{
        width: typeof width === 'number' ? `${width}px` : width,
        height: height,
        padding: '1rem',
        fontFamily: 'monospace',
        fontSize: '1rem',
        backgroundColor: '#f4f4f4',
        border: '1px solid #ccc',
        resize: 'none',
        whiteSpace: 'pre-wrap',
      }}
    />
  );
};

export default TypewriterTextarea;
