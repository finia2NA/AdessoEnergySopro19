import React, { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import App from './app';
import './index.scss';
import { AuthenticationProvider } from './providers/authentication-provider';
import { config } from './services/ad-api';
import { getApiBaseDomain } from './lib/app-config';

config.init({
  baseUrl: getApiBaseDomain()
});

ReactDOM.render(
  <StrictMode>
    <AuthenticationProvider
      override={{
        isLoggedIn: true,
        userId: 'kaka',
        isAdmin: false
      }}
    >
      <App />
    </AuthenticationProvider>
  </StrictMode>,
  document.getElementById('root')
);
