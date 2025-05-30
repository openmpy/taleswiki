import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Suspense, lazy } from "react";
import { HelmetProvider } from "react-helmet-async";
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
const NotFoundPage = lazy(() => import("./pages/NotFoundPage"));

const queryClient = new QueryClient();

function App() {
  return (
    <ErrorBoundary>
      <HelmetProvider>
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
                        <div className="w-full md:w-96">
                          <ChatRoom className="w-full md:rounded-lg rounded-none" />
                        </div>
                        <main className="w-full md:flex-1 bg-white md:p-6 md:rounded-lg p-4 rounded-none border border-gray-200">
                          <Suspense fallback={<LoadingSpinner />}>
                            <Routes>
                              <Route path="/" element={<HomePage />} />
                              <Route path="/admin" element={<AdminPage />} />
                              <Route
                                path="/dictionary/runner"
                                element={
                                  <DictionaryCategoryPage type="runner" />
                                }
                              />
                              <Route
                                path="/dictionary/guild"
                                element={
                                  <DictionaryCategoryPage type="guild" />
                                }
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
      </HelmetProvider>
    </ErrorBoundary>
  );
}

export default App;
