import React from 'react';
import styles from './meter-list.module.scss';
import { Meter } from '../../typings/provider-data-interfaces';
import { SubTitle } from '../generics/text';
import MeterItem from './meter-item';
import { Link } from '@reach/router';

interface MeterListProps {
  meters: Meter[] | null;
  hideAdd?: boolean;
}

const MeterList: React.FC<MeterListProps> = ({ meters, hideAdd }) => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <SubTitle>Zähler</SubTitle>
        {!hideAdd && (
          <Link className={styles.addLink} to="new">
            Neuen Zähler hinzufügen
          </Link>
        )}
      </div>

      {meters !== null && (
        <nav className={styles.list}>
          {meters.map(meter => (
            <MeterItem
              key={meter.id}
              id={meter.id}
              type={meter.type}
              meterNumber={meter.meterNumber}
              date={meter.lastReading.createdAt.toLocaleDateString()}
              name={meter.name}
            />
          ))}
        </nav>
      )}
    </div>
  );
};

export default MeterList;
