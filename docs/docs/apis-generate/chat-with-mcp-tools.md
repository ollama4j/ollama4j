---
sidebar_position: 9
---

import CodeEmbed from '@site/src/components/CodeEmbed';

# Chat with MCP Tools

:::note

This is an experimental feature and is subject to change in order to improve the usage experience.

:::

### Bring in your MCP tools

Here is a sample Python MCP tool that can fetch the geocode and weather details for a given city.

To set it up, make sure you have Python and the required dependencies installed.

Create a file named `weather_open_meteo.py` with the following content:

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/resources/weather_open_meteo.py" language="python"/>

To make setting up the tool easier, we’ve prepared the necessary resources so you can get started quickly. Follow the steps below.

- Run the following command to install all required packages:

```bash
pip install -r https://raw.githubusercontent.com/ollama4j/ollama4j-examples/main/src/main/resources/requirements.txt
```

- After installing the dependencies, run the script using:

```shell
python /path/to/weather_open_meteo.py
```

That's it! The tool is now ready to run in your terminal. You should see it waiting for input, which means the MCP tool is fully operational. If you encounter any errors, please fix them and try running the tool again.

### Define the  MCP tools JSON

Use the following JSON format to define the MCP tools in a standard format that is widely used by tools such as Claude. Make sure to adjust the Python file path and the MCP tool file path.

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/resources/mcp-config.json" language="json"/>

### Use MCP Tools in Chat

<CodeEmbed src="https://raw.githubusercontent.com/ollama4j/ollama4j-examples/refs/heads/main/src/main/java/io/github/ollama4j/examples/ChatWithMCPToolExample.java"/>

:::tip[LLM Response]
_Calling MCP Tool: 'weather.geocode_city' with arguments: [count=1, language=en, name=Bengaluru]._

Based on the information provided, the geocode for Bengaluru (Bangalore), India is as follows:
- Name: Bengaluru
- Country: India
- Latitude: 12.97194
- Longitude: 77.59369
- Elevation: 920.0 m
- Timezone: Asia/Kolkata
- Population: 8,443,675
- Feature Code: PPLA
:::
