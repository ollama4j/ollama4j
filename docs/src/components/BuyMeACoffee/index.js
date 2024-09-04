import React from "react";

class BuyMeACoffee extends React.Component {
  constructor(props) {
    super(props)
    let script = document.createElement("script");
    script.src = 'https://cdnjs.buymeacoffee.com/1.0.0/widget.prod.min.js';
    script.dataset.name = 'BMC-Widget';
    script.dataset.cfasync = 'false';
    script.dataset.id = 'amithkoujalgi';
    script.dataset.description = 'Support me on Buy me a coffee!';
    script.dataset.message = 'If you like my work and want to say thanks, or encourage me to do more, you can buy me a coffee! 😊';
    script.dataset.color = '#2e8555';
    script.dataset.position = 'Right';
    script.dataset.x_margin = '18';
    script.dataset.y_margin = '18';
    script.async = true

    script.onload = function () {
      let evt = document.createEvent('Event');
      evt.initEvent('DOMContentLoaded', false, false);
      window.dispatchEvent(evt);
    }
    this.script = script
  }

  componentDidMount() {
    document.head.appendChild(this.script)
  }

  // componentWillUnmount() {
  //   document.head.removeChild(this.script);
  //   document.body.removeChild(document.getElementById("bmc-wbtn"))
  // }

  render() {
    return null
  }
}

export default BuyMeACoffee;