import { createBrowserRouter, RouterProvider, Navigate, redirect } from 'react-router-dom';
import { ThemeProvider } from './components/theme-provider';
import { RootLayout } from './components/layout/RootLayout';
import { OnboardingPage } from './pages/OnboardingPage';
import { ChatPage } from './pages/ChatPage';


function rootLoader() {
  const guestId = localStorage.getItem('guestSessionId');

  if (!guestId) {

    const newGuestId = crypto.randomUUID();
    localStorage.setItem('guestSessionId', newGuestId);
    return redirect('/onboarding');
  }


  return redirect('/chat');
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
