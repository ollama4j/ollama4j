---
sidebar_position: 2
---

import CodeEmbed from '@site/src/components/CodeEmbed';
import TypewriterTextarea from '@site/src/components/TypewriterTextarea';

# Generate (Async)

### Generate response from a model asynchronously

This API lets you ask questions to the LLMs in a asynchronous way.
This is particularly helpful when you want to issue a generate request to the LLM and collect the response in the
background (such as threads) without blocking your code until the response arrives from the model.

This API corresponds to
the [completion](https://github.com/jmorganca/ollama/blob/main/docs/api.md#generate-a-completion) API.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateAsync.java" />

You will get a response similar to:

<TypewriterTextarea
textContent={`Here are the participating teams in the 2019 ICC Cricket World Cup:

1. Australia
2. Bangladesh
3. India
4. New Zealand
5. Pakistan
6. England
7. South Africa
8. West Indies (as a team)
9. Afghanistan`}
   typingSpeed={10}
   pauseBetweenSentences={1200}
   height="auto"
   width="100%"
   style={{ whiteSpace: 'pre-line' }}
   />

### Generate response from a model asynchronously with thinking and response streamed

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/GenerateAsyncWithThinking.java" />

<TypewriterTextarea
textContent={`WE NEED TO ANSWER THE QUESTION: "HOW LONG DOES IT TAKE FOR THE LIGHT FROM THE SUN TO REACH EARTH?" THE USER LIKELY EXPECTS THE TIME IN SECONDS, MINUTES, OR HOURS. LIGHT TRAVELS AT SPEED OF LIGHT (299,792,458 M/S). DISTANCE BETWEEN SUN AND EARTH IS ABOUT 1 AU (~149.6 MILLION KM). SO TRAVEL TIME = 1 AU / C ≈ 500 SECONDS ≈ 8.3 MINUTES. MORE PRECISELY, 8 MINUTES AND 20 SECONDS. PROVIDE CONTEXT: AVERAGE DISTANCE, VARYING DUE TO ELLIPTICAL ORBIT. SO ANSWER: ABOUT 8 MINUTES 20 SECONDS. ALSO MENTION THAT DUE TO VARIATION: FROM 8:07 TO 8:20. PROVIDE DETAILS. ALSO MENTION THAT WE REFER TO THE TIME LIGHT TAKES TO TRAVEL 1 ASTRONOMICAL UNIT.

ALSO MIGHT MENTION: FOR MORE PRECISE: 499 SECONDS = 8 MIN 19 S. VARIATION DUE TO EARTH'S ORBIT: FROM 8 MIN 6 S TO 8 MIN 20 S. SO ANSWER.

LET'S CRAFT AN EXPLANATION.

the sun’s light takes a little over **eight minutes** to get to earth.

| quantity | value |
|----------|-------|
| distance (average) | 1 astronomical unit (au) ≈ 149,600,000 km |
| speed of light | \(c = 299,792,458\) m s⁻¹ |
| light‑travel time | \(\displaystyle \frac{1\ \text{au}}{c} \approx 499\ \text{s}\) |

499 seconds is **8 min 19 s**.

because the earth’s orbit is slightly elliptical, the distance varies from about 147 million km (at perihelion) to 152 million km (at aphelion). this gives a light‑travel time that ranges roughly from **8 min 6 s** to **8 min 20 s**. thus, when we look at the sun, we’re seeing it as it was about eight minutes agoComplete thinking response: We need to answer the question: "How long does it take for the light from the Sun to reach Earth?" The user likely expects the time in seconds, minutes, or hours. Light travels at speed of light (299,792,458 m/s). Distance between Sun and Earth is about 1 AU (~149.6 million km). So travel time = 1 AU / c ≈ 500 seconds ≈ 8.3 minutes. More precisely, 8 minutes and 20 seconds. Provide context: average distance, varying due to elliptical orbit. So answer: about 8 minutes 20 seconds. Also mention that due to variation: from 8:07 to 8:20. Provide details. Also mention that we refer to the time light takes to travel 1 astronomical unit.

Also might mention: For more precise: 499 seconds = 8 min 19 s. Variation due to Earth's orbit: from 8 min 6 s to 8 min 20 s. So answer.

Let's craft an explanation.
Complete response: The Sun’s light takes a little over **eight minutes** to get to Earth.

| Quantity | Value |
|----------|-------|
| Distance (average) | 1 astronomical unit (AU) ≈ 149,600,000 km |
| Speed of light | \(c = 299,792,458\) m s⁻¹ |
| Light‑travel time | \(\displaystyle \frac{1\ \text{AU}}{c} \approx 499\ \text{s}\) |

499 seconds is **8 min 19 s**.

Because the Earth’s orbit is slightly elliptical, the distance varies from about 147 million km (at perihelion) to 152 million km (at aphelion). This gives a light‑travel time that ranges roughly from **8 min 6 s** to **8 min 20 s**. Thus, when we look at the Sun, we’re seeing it as it was about eight minutes ago.`}
   typingSpeed={5}
   pauseBetweenSentences={1200}
   height="auto"
   width="100%"
   style={{ whiteSpace: 'pre-line' }}
   />
