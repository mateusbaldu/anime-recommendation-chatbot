import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { ThemeProvider } from './components/theme-provider';
import { RootLayout } from './components/layout/RootLayout';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { OnboardingPage } from './pages/OnboardingPage';
import { ChatPage } from './pages/ChatPage';

const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      {
        path: '/login',
        element: <LoginPage />,
      },
      {
        element: <ProtectedRoute />,
        children: [
          {
            path: '/onboarding',
            element: <OnboardingPage />,
          },
          {
            path: '/chat',
            element: <ChatPage />,
          },
        ],
      },
      {
        path: '/',
        element: <Navigate to="/chat" replace />,
      },
      {
        path: '*',
        element: <Navigate to="/chat" replace />,
      },
    ],
  },
]);

function App() {
  return (
    <ThemeProvider defaultTheme="system" storageKey="anime-chatbot-theme">
      <RouterProvider router={router} />
    </ThemeProvider>
  );
}

export default App;
