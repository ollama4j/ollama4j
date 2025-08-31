import React, { useEffect, useState, useRef } from 'react';

const TypewriterTextarea = ({
  textContent,
  typingSpeed = 50,
  pauseBetweenSentences = 1000,
  height = '200px',
  width = '100%',
  align = 'left',
  style = {},
}) => {
  const [displayedText, setDisplayedText] = useState('');
  const [charIndex, setCharIndex] = useState(0);
  const isTyping = useRef(false);

  // Flatten textContent to a string, preserving \n
  const fullText = textContent || '';

  useEffect(() => {
    if (!fullText) return;

    if (!isTyping.current) {
      isTyping.current = true;
    }

    if (charIndex > fullText.length) {
      // Reset to start from the beginning
      setCharIndex(0);
      setDisplayedText('');
      return;
    }

    if (charIndex < fullText.length) {
      const timeout = setTimeout(() => {
        setDisplayedText(fullText.slice(0, charIndex + 1));
        setCharIndex((prevCharIndex) => prevCharIndex + 1);
      }, fullText[charIndex] === '\n' ? typingSpeed : typingSpeed);
      return () => clearTimeout(timeout);
    } else {
      // Wait a bit, then restart
      const timeout = setTimeout(() => {
        setCharIndex(0);
        setDisplayedText('');
      }, pauseBetweenSentences);
      return () => clearTimeout(timeout);
    }
    // eslint-disable-next-line
  }, [charIndex, fullText, typingSpeed, pauseBetweenSentences]);

  return (
    <div
      style={{
        width: typeof width === 'number' ? `${width}px` : width,
        height: height,
        padding: '1rem',
        fontFamily: 'monospace',
        fontSize: '1rem',
        backgroundColor: '#f4f4f4',
        border: '1px solid #ccc',
        textAlign: align,
        resize: 'none',
        whiteSpace: 'pre-wrap',
        color: 'black',
        overflow: 'auto',
        ...style,
      }}
    >
      {displayedText}
    </div>
  );
};

export default TypewriterTextarea;