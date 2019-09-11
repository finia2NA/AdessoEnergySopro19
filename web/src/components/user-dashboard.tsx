import React from 'react';
import ContainerCard from './generics/container-card';
import styles from './user-dashboard.module.scss';
import { useMeters } from '../providers/meters-provider';
import Spinner from './generics/spinner';
import { useAuth } from '../providers/authentication-provider';
import MeterList from './meters-list/meter-list';
import { Router, navigate } from '@reach/router';
import { SelectMeter } from './dashboard-content/select-call';
import NewMeter from './meters-list/new-meter';
import MeterInformation from './dashboard-content/meter-information';
import { MeterType } from '../typings/provider-data-interfaces';

const UserDashboard: React.FC = () => {
  const { userId } = useAuth();
  const { meters, isLoading, addMeter } = useMeters(userId);

  function createMeter(
    meterType: MeterType,
    name: string,
    meterNumber: string,
    initialValue: string
  ): void {
    addMeter({
      type: meterType,
      name,
      meterNumber
    });
  }

  if (isLoading)
    return (
      <ContainerCard className={styles.container}>
        <Spinner />
      </ContainerCard>
    );

  return (
    <ContainerCard className={styles.container}>
      <MeterList meters={meters} />
      <div className={styles.contentContainer}>
        <Router className={styles.router} basepath="/">
          <SelectMeter path="/" />
          <NewMeter
            path="/new"
            onCreate={createMeter}
            onCancel={() => navigate('../')}
          />
          <MeterInformation path=":id" />
        </Router>
      </div>
    </ContainerCard>
  );
};

export default UserDashboard;
