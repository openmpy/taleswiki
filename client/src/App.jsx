import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import "./App.css";
import ScrollButtons from "./components/ScrollButtons";
import ScrollToTop from "./components/ScrollToTop";
import Footer from "./layouts/Footer";
import Header from "./layouts/Header";
import Sidebar from "./layouts/Sidebar";
import DictionaryCategoryPage from "./pages/DictionaryCategoryPage";
import DictionaryEditPage from "./pages/DictionaryEditPage";
import DictionaryLogPage from "./pages/DictionaryLogPage";
import DictionaryViewPage from "./pages/DictionaryViewPage";
import DictionaryWritePage from "./pages/DictionaryWritePage";
import HomePage from "./pages/HomePage";
import NotFoundPage from "./pages/NotFoundPage";
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <ScrollToTop />
        <div className="min-h-screen flex flex-col bg-gray-100">
          <Header />

          <div className="flex-1 px-0 py-4 md:px-4">
            <div className="flex flex-col md:flex-row md:items-start gap-4">
              <main className="w-full md:flex-1 bg-white md:p-6 md:rounded-lg p-4 rounded-none border border-gray-200">
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route
                    path="/dictionary/runner"
                    element={<DictionaryCategoryPage type="runner" />}
                  />
                  <Route
                    path="/dictionary/guild"
                    element={<DictionaryCategoryPage type="guild" />}
                  />
                  <Route
                    path="/dictionary/write"
                    element={<DictionaryWritePage />}
                  />
                  <Route
                    path="/dictionary/:id"
                    element={<DictionaryViewPage />}
                  />
                  <Route
                    path="/dictionary/:id/edit"
                    element={<DictionaryEditPage />}
                  />
                  <Route
                    path="/dictionary/:id/log"
                    element={<DictionaryLogPage />}
                  />
                  <Route path="*" element={<NotFoundPage />} />
                </Routes>
              </main>

              <div className="w-full md:w-64">
                <Sidebar className="w-full md:p-6 md:rounded-lg p-4 rounded-none" />
              </div>
            </div>
          </div>

          <Footer />
          <ScrollButtons />
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;
