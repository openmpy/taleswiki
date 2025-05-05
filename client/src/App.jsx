import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import "./App.css";
import Footer from "./layouts/Footer";
import Header from "./layouts/Header";
import Sidebar from "./layouts/Sidebar";
import GuildDictionaryPage from "./pages/GuildDictionaryPage";
import HomePage from "./pages/HomePage";
import RunnerDictionaryPage from "./pages/RunnerDictionaryPage";

function App() {
  return (
    <Router>
      <div className="min-h-screen flex flex-col bg-gray-100">
        <Header />

        <div className="flex-1 flex p-4 gap-4">
          <main className="flex-1 bg-white p-6 rounded-lg border border-gray-200">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route
                path="/runner-dictionary"
                element={<RunnerDictionaryPage />}
              />
              <Route
                path="/guild-dictionary"
                element={<GuildDictionaryPage />}
              />
            </Routes>
          </main>

          <Sidebar />
        </div>

        <Footer />
      </div>
    </Router>
  );
}

export default App;
