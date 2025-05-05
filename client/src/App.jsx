import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import "./App.css";
import Footer from "./layouts/Footer";
import Header from "./layouts/Header";
import Sidebar from "./layouts/Sidebar";
import DictionaryPage from "./pages/DictionaryPage";
import DictionaryWritePage from "./pages/DictionaryWritePage";
import HomePage from "./pages/HomePage";
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen flex flex-col bg-gray-100">
          <Header />

          <div className="flex-1 px-0 py-4 md:px-4">
            <div className="flex flex-col md:flex-row md:items-start gap-4">
              <main className="w-full md:flex-1 bg-white md:p-6 md:rounded-lg p-4 rounded-none border border-gray-200">
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route
                    path="/runner-dictionary"
                    element={<DictionaryPage type="runner" />}
                  />
                  <Route
                    path="/guild-dictionary"
                    element={<DictionaryPage type="guild" />}
                  />
                  <Route
                    path="/dictionaries/write"
                    element={<DictionaryWritePage />}
                  />
                </Routes>
              </main>

              <div className="w-full md:w-64">
                <Sidebar className="w-full md:p-6 md:rounded-lg p-4 rounded-none" />
              </div>
            </div>
          </div>

          <Footer />
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
