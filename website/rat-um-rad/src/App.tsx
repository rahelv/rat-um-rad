import React from "react";
import logo from "./logo.jpg";
import "./App.css";

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h1>Rat um Rad</h1>
        <p>Welcome to the best game online!</p>
        <a
          className="App-link"
          href="https://firebasestorage.googleapis.com/v0/b/rat-um-rad.appspot.com/o/rat-um-rad-win.jar?alt=media&token=91bd0e00-8ee4-4913-9123-e6a8841e3631"
          target="_blank"
          rel="noopener noreferrer"
        >
          Download JAR
        </a>
        <a
          className="App-link"
          href="https://firebasestorage.googleapis.com/v0/b/rat-um-rad.appspot.com/o/Rat%20um%20Rad%20Manual.pdf?alt=media&token=ff12dd34-7897-474f-a017-dbcd04ddba07"
          target="_blank"
          rel="noopener noreferrer"
        >
          Download Manual
        </a>

        <h3>Trailer</h3>

        <video width="640" height="480" controls>
          <source
            src="https://firebasestorage.googleapis.com/v0/b/rat-um-rad.appspot.com/o/trailer.mp4?alt=media&token=0afa5084-074a-4571-bf07-ed1bec10c641"
            type="video/mp4"
          />
          Your browser does not support the video tag.
        </video>
      </header>
    </div>
  );
}

export default App;
