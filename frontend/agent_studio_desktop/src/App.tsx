import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ProtectedRoute } from "@/components/auth/ProtectedRoute";
import { PublicOnlyRoute } from "@/components/auth/PublicOnlyRoute";
import { Login } from "@/components/pages/Login";
import { Signup } from "@/components/pages/Signup";
import { Datasources } from "@/components/pages/Datasources";
import { DatasourceDetail } from "@/components/pages/DatasourceDetail";
import { Me } from "@/components/pages/Me";
import NotFound from "@/components/pages/NotFound";
import { RootRedirect } from "@/components/auth/RootRedirect";
import { useAuthInit } from "@/hooks/useAuthInit";

const App: React.FC = () => {
  useAuthInit();

  return (
    <Router>
      <Routes>
        <Route path="/" element={<RootRedirect />} />
        <Route
          path="/login"
          element={
            <PublicOnlyRoute>
              <Login />
            </PublicOnlyRoute>
          }
        />
        <Route
          path="/signup"
          element={
            <PublicOnlyRoute>
              <Signup />
            </PublicOnlyRoute>
          }
        />
        <Route
          path="/datasources"
          element={
            <ProtectedRoute>
              <Datasources />
            </ProtectedRoute>
          }
        />
        <Route
          path="/datasources/:datasourceId"
          element={
            <ProtectedRoute>
              <DatasourceDetail />
            </ProtectedRoute>
          }
        />
        <Route
          path="/me"
          element={
            <ProtectedRoute>
              <Me />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Router>
  );
};

export default App;
