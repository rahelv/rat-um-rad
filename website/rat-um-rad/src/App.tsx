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
      </header>
    </div>
  );
}

export default App;

