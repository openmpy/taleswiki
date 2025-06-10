import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Suspense, lazy, useState } from "react";
import { HiOutlineChat } from "react-icons/hi";
import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import "./App.css";
import ChatRoom from "./components/ChatRoom";
import ErrorBoundary from "./components/ErrorBoundary";
import LoadingSpinner from "./components/LoadingSpinner";
import ScrollButtons from "./components/ScrollButtons";
import ScrollToTop from "./components/ScrollToTop";
import Footer from "./layouts/Footer";
import Header from "./layouts/Header";
import Sidebar from "./layouts/Sidebar";
import "./utils/axiosConfig";

const HomePage = lazy(() => import("./pages/HomePage"));
const AdminPage = lazy(() => import("./pages/AdminPage"));
const BlockedPage = lazy(() => import("./pages/BlockedPage"));
const DictionaryCategoryPage = lazy(() =>
  import("./pages/DictionaryCategoryPage")
);
const DictionaryEditPage = lazy(() => import("./pages/DictionaryEditPage"));
const DictionaryLogPage = lazy(() => import("./pages/DictionaryLogPage"));
const DictionaryViewPage = lazy(() => import("./pages/DictionaryViewPage"));
const DictionaryWritePage = lazy(() => import("./pages/DictionaryWritePage"));
const DictionaryComparePage = lazy(() =>
  import("./pages/DictionaryComparePage")
);
const NotFoundPage = lazy(() => import("./pages/NotFoundPage"));

const queryClient = new QueryClient();

function App() {
  const [isChatExpanded, setIsChatExpanded] = useState(
    window.innerWidth >= 768
  );

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <Router>
          <ScrollToTop />
          <Routes>
            <Route path="/blocked" element={<BlockedPage />} />
            <Route
              path="*"
              element={
                <div className="min-h-screen flex flex-col bg-gray-100">
                  <Header />
                  <div className="flex-1 px-0 py-4 md:px-4">
                    <div className="flex flex-col md:flex-row md:items-start gap-4">
                      {isChatExpanded || window.innerWidth < 768 ? (
                        <div className="w-full md:w-96">
                          <ChatRoom
                            className="w-full"
                            onExpandChange={setIsChatExpanded}
                          />
                        </div>
                      ) : (
                        <button
                          onClick={() => setIsChatExpanded(true)}
                          className="fixed bottom-8 border-gray-600 border-1 left-8 bg-gray-800 hover:bg-gray-700 text-white px-3 py-3 rounded shadow-lg transition-colors duration-200 flex items-center gap-2"
                        >
                          <HiOutlineChat size={20} />
                        </button>
                      )}
                      <main
                        className={`w-full ${
                          isChatExpanded ? "md:flex-1" : "md:flex-1 md:ml-0"
                        } bg-white md:p-6 md:rounded-lg p-4 rounded-none border border-gray-200`}
                      >
                        <Suspense fallback={<LoadingSpinner />}>
                          <Routes>
                            <Route path="/" element={<HomePage />} />
                            <Route path="/admin" element={<AdminPage />} />
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
                            <Route
                              path="/dictionary/:id/compare"
                              element={<DictionaryComparePage />}
                            />
                            <Route path="*" element={<NotFoundPage />} />
                          </Routes>
                        </Suspense>
                      </main>
                      <div className="w-full md:w-64">
                        <Sidebar className="w-full md:rounded-lg rounded-none" />
                      </div>
                    </div>
                  </div>
                  <Footer />
                  <ScrollButtons />
                </div>
              }
            />
          </Routes>
        </Router>
      </QueryClientProvider>
    </ErrorBoundary>
  );
}

export default App;
