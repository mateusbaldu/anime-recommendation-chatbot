import { createBrowserRouter, RouterProvider, Navigate, redirect } from 'react-router-dom';
import { ThemeProvider } from './components/theme-provider';
import { RootLayout } from './components/layout/RootLayout';
import { OnboardingPage } from './pages/OnboardingPage';
import { ChatPage } from './pages/ChatPage';


function rootLoader() {
  const guestId = localStorage.getItem('guestSessionId');
  const hasOnboarded = localStorage.getItem('hasCompletedOnboarding');

  if (!guestId) {
    const newGuestId = crypto.randomUUID();
    localStorage.setItem('guestSessionId', newGuestId);
    return redirect('/onboarding');
  }

  if (hasOnboarded === 'true') {
    return redirect('/chat');
  }

  return redirect('/onboarding');
}

function chatGuardLoader() {
  const hasOnboarded = localStorage.getItem('hasCompletedOnboarding');
  if (hasOnboarded !== 'true') {
    return redirect('/onboarding');
  }
  return null;
}

const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      {
        path: '/',
        loader: rootLoader,
        element: null,
      },
      {
        path: '/onboarding',
        element: <OnboardingPage />,
      },
      {
        path: '/chat',
        loader: chatGuardLoader,
        element: <ChatPage />,
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
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
