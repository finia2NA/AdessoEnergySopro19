import React from 'react';
import { Router, Redirect, RouteComponentProps } from '@reach/router';
import UserScreen from './screens/user-screen';
import { MetersProvider } from './providers/meters-provider';
import { ReadingsProvider } from './providers/readings-provider';
import { IssuesProvider } from './providers/issues-provider';

const NotFound: React.FC<RouteComponentProps> = () => {
  return <Redirect to="/counters" noThrow />;
};

const UserApp: React.FC = () => {
  return (
    <IssuesProvider>
      <MetersProvider>
        <ReadingsProvider>
          <Router>
            <UserScreen path="/counters/*" />
            <NotFound default />
          </Router>
        </ReadingsProvider>
      </MetersProvider>
    </IssuesProvider>
  );
};

export default UserApp;
